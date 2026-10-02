// 実行: java StubMockDrill.java
//
// 目的: スタブ・フェイク・モック・ダミーを「自分の手で書いて」違いを掴む。
//      Mockito等のライブラリは一切使わない（ライブラリの魔法が概念を隠すため）。
//
// 進め方: 先に下まで読んで「どのテストがOK/NGになるか」を予想してから実行する。

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class StubMockDrill {

    // ======================================================================
    // テスト対象が依存するもの（本物はDBアクセスと監査ログ）
    // ======================================================================

    record Vehicle(String id, int displacement, int ageYears, boolean reduced) {}

    /** DBの代わり。「値を返す」のが役目 → スタブ/フェイクで差し替える */
    interface VehicleRepository {
        Vehicle findById(String id);
    }

    /** 監査ログの代わり。「呼ばれたこと自体」が大事 → モックで差し替える */
    interface AuditLog {
        void record(String message);
    }

    // ======================================================================
    // テスト対象（SUT: System Under Test）
    // ======================================================================

    static class TaxService {
        private final VehicleRepository repository;
        private final AuditLog auditLog;

        TaxService(VehicleRepository repository, AuditLog auditLog) {
            this.repository = repository;
            this.auditLog = auditLog;
        }

        /**
         * 仕様:
         *   1. 排気量660cc以下は軽自動車 10,800円、それ以外は 39,500円
         *   2. 初度登録から13年以上は 15%重課
         *   3. 減免対象は半額
         *   4. 算出したら監査ログを1件残す
         */
        int calculate(String vehicleId) {
            Vehicle v = repository.findById(vehicleId);
            if (v == null) {
                auditLog.record("車両なし:" + vehicleId);
                throw new IllegalArgumentException("車両が見つかりません: " + vehicleId);
            }
            int tax = (v.displacement() <= 660) ? 10800 : 39500;
            if (v.ageYears() >= 13) {
                tax = (int) (tax * 1.15);   // ← ここに注目（あとで効いてくる）
            }
            if (v.reduced()) {
                tax = tax / 2;
            }
            auditLog.record("税額算出:" + vehicleId + "=" + tax);
            return tax;
        }
    }

    // ======================================================================
    // ① スタブ: 決まった値を返すだけ。「呼ばれる側」の代わり
    // ======================================================================
    static class StubRepository implements VehicleRepository {
        private final Vehicle fixed;

        StubRepository(Vehicle fixed) {
            this.fixed = fixed;
        }

        @Override
        public Vehicle findById(String id) {
            return fixed;   // idが何であっても必ず同じものを返す（＝これがスタブ）
        }
    }

    // ======================================================================
    // ② フェイク: 簡易だが「本当に動く」実装（day2のTodoアプリのMap実装がこれ）
    // ======================================================================
    static class FakeRepository implements VehicleRepository {
        private final Map<String, Vehicle> store = new HashMap<>();

        void save(Vehicle v) {
            store.put(v.id(), v);
        }

        @Override
        public Vehicle findById(String id) {
            return store.get(id);   // idで引ける＝本物のDBに近い振る舞い
        }
    }

    // ======================================================================
    // ③ モック: 呼ばれ方（回数・引数）を記録して、あとで検証する
    // ======================================================================
    static class MockAuditLog implements AuditLog {
        final List<String> calls = new ArrayList<>();

        @Override
        public void record(String message) {
            calls.add(message);   // 本来の処理はせず、記録だけする
        }

        int callCount() {
            return calls.size();
        }

        boolean wasCalledWith(String expected) {
            return calls.contains(expected);
        }
    }

    // ======================================================================
    // ④ ダミー: 引数を埋めるためだけ。中身も検証もしない
    // ======================================================================
    static class DummyAuditLog implements AuditLog {
        @Override
        public void record(String message) {
            // 何もしない
        }
    }

    // ======================================================================
    // 簡易テストランナー（JUnitの代わり。やっていることは同じ）
    // ======================================================================
    static int passed = 0;
    static int failed = 0;

    static void check(String name, Object expected, Object actual) {
        boolean ok = (expected == null) ? actual == null : expected.equals(actual);
        if (ok) {
            passed++;
            System.out.printf("  OK  %s  (期待=%s, 実際=%s)%n", name, expected, actual);
        } else {
            failed++;
            System.out.printf("  NG  %s  (期待=%s, 実際=%s)  ←★%n", name, expected, actual);
        }
    }

    static void checkTrue(String name, boolean actual) {
        check(name, true, actual);
    }

    // ======================================================================
    public static void main(String[] args) {

        System.out.println("=== ① スタブを使ったテスト（状態の検証: 戻り値が正しいか） ===");

        // 軽自動車・新しい・減免なし → 10,800円
        TaxService s1 = new TaxService(
                new StubRepository(new Vehicle("A-001", 660, 3, false)),
                new DummyAuditLog());   // ログは今回の関心事ではないのでダミー
        check("Q1 軽自動車", 10800, s1.calculate("A-001"));

        // 普通車・新しい・減免なし → 39,500円
        TaxService s2 = new TaxService(
                new StubRepository(new Vehicle("B-001", 1500, 3, false)),
                new DummyAuditLog());
        check("Q2 普通車", 39500, s2.calculate("B-001"));

        // 普通車・13年以上 → 39,500 × 1.15 = 45,425円（仕様どおりならこうなるはず）
        TaxService s3 = new TaxService(
                new StubRepository(new Vehicle("C-001", 1500, 15, false)),
                new DummyAuditLog());
        check("Q3 普通車・重課", 45425, s3.calculate("C-001"));

        // 普通車・減免あり → 39,500 ÷ 2 = 19,750円
        TaxService s4 = new TaxService(
                new StubRepository(new Vehicle("D-001", 1500, 3, true)),
                new DummyAuditLog());
        check("Q4 普通車・減免", 19750, s4.calculate("D-001"));

        System.out.println();
        System.out.println("=== ② フェイクを使ったテスト（idで引き分けられる＝本物に近い） ===");

        FakeRepository fake = new FakeRepository();
        fake.save(new Vehicle("A-001", 660, 3, false));
        fake.save(new Vehicle("B-001", 1500, 3, false));
        TaxService s5 = new TaxService(fake, new DummyAuditLog());

        check("Q5 フェイク:軽", 10800, s5.calculate("A-001"));
        check("Q5 フェイク:普通", 39500, s5.calculate("B-001"));
        // スタブでは「どのidでも同じ値」だったので、この区別はできなかった

        // 未登録のidは例外になるはず
        try {
            s5.calculate("Z-999");
            check("Q6 未登録idで例外", "IllegalArgumentException", "例外が出なかった");
        } catch (IllegalArgumentException e) {
            check("Q6 未登録idで例外", "IllegalArgumentException", e.getClass().getSimpleName());
        }

        System.out.println();
        System.out.println("=== ③ モックを使ったテスト（振る舞いの検証: 正しく呼んだか） ===");

        MockAuditLog mock = new MockAuditLog();
        TaxService s6 = new TaxService(
                new StubRepository(new Vehicle("A-001", 660, 3, false)),
                mock);
        int tax = s6.calculate("A-001");

        check("Q7 戻り値", 10800, tax);
        check("Q8 監査ログの呼び出し回数", 1, mock.callCount());
        checkTrue("Q9 監査ログの内容", mock.wasCalledWith("税額算出:A-001=10800"));

        // 車両が見つからないときも監査ログを残す仕様 → 呼ばれ方を検証できるのはモックだけ
        MockAuditLog mock2 = new MockAuditLog();
        TaxService s7 = new TaxService(new StubRepository(null), mock2);
        try {
            s7.calculate("Z-999");
        } catch (IllegalArgumentException ignored) {
        }
        check("Q10 異常時も監査ログ1件", 1, mock2.callCount());
        checkTrue("Q11 異常時のログ内容", mock2.wasCalledWith("車両なし:Z-999"));

        System.out.println();
        System.out.println("=== ④ 境界の組み合わせ（ここが本番） ===");

        // 普通車・13年以上・減免あり → 39,500 ×1.15 ÷2
        // 実数で計算すると 22,712.5 円。1円未満をどう扱うかは仕様に書かれていない。
        TaxService s8 = new TaxService(
                new StubRepository(new Vehicle("E-001", 1500, 15, true)),
                new DummyAuditLog());
        check("Q12 重課＋減免（期待値は切り上げと仮定）", 22713, s8.calculate("E-001"));

        System.out.println();
        System.out.printf("結果: OK %d件 / NG %d件%n", passed, failed);
        System.out.println();
        System.out.println("NGが出たものは、実装が間違っているのか、テストの期待値が勝手な仮定なのかを考える。");
        System.out.println("どちらとも言えないなら、それは『仕様が決まっていない』というサイン。");
        System.out.println("→ 答え合わせは hands-on-02-stub-mock-coverage.md の §3 へ");
    }
}
