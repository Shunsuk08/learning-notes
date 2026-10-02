// 実行: java CoverageDrill.java
//
// 目的: 「このテストで何ステップ通ったか（カバレッジ）」を、ツール無しで体感する。
//      JaCoCo等のツールが裏でやっているのは、結局これと同じ「通った印をつける」作業。
//
// 覚えること:
//   C0（命令網羅）= 文（ステップ）をどれだけ通ったか
//   C1（分岐網羅）= if の true側・false側をどれだけ通ったか
//   C2（条件網羅）= && や || でつないだ個々の条件を true/false 両方通ったか
//
// 一番大事な事実: 「C0が100%でも、C1は50%」ということが普通に起きる。
//                 ＝全ステップ通ったのにテストは不十分、という状態。

import java.util.LinkedHashSet;
import java.util.Set;

public class CoverageDrill {

    // 通った印を記録する場所（これがカバレッジ計測ツールの中身）
    static final Set<String> hitStatements = new LinkedHashSet<>();
    static final Set<String> hitBranches = new LinkedHashSet<>();
    static final Set<String> hitConditions = new LinkedHashSet<>();

    static void stmt(String id) { hitStatements.add(id); }
    static void branch(String id) { hitBranches.add(id); }
    static void cond(String id) { hitConditions.add(id); }

    // ======================================================================
    // テスト対象（減免の判定）
    //
    // 仕様: 65歳以上 かつ 障害者手帳ありの場合のみ、50%減免
    //
    // 計測のために、本来のコードに「通った印」を手で埋め込んである。
    // 元のコードはこれだけ:
    //
    //   int discountRate(int age, boolean hasCertificate) {   // S1
    //       int rate = 0;                                      // S2
    //       if (age >= 65 && hasCertificate) {                 // S3
    //           rate = 50;                                     // S4
    //       }
    //       return rate;                                       // S5
    //   }
    //
    //   文(ステップ): S2, S3, S4, S5 の4つ
    //   分岐: S3のtrue側 / false側 の2つ
    //   条件: age>=65 のtrue/false, hasCertificate のtrue/false の4つ
    // ======================================================================
    static int discountRate(int age, boolean hasCertificate) {
        stmt("S2");
        int rate = 0;

        stmt("S3");
        boolean c1 = age >= 65;
        boolean c2 = hasCertificate;
        cond("age>=65:" + c1);
        cond("cert:" + c2);

        if (c1 && c2) {
            branch("S3-true");
            stmt("S4");
            rate = 50;
        } else {
            branch("S3-false");
        }

        stmt("S5");
        return rate;
    }

    // ======================================================================
    static void reset() {
        hitStatements.clear();
        hitBranches.clear();
        hitConditions.clear();
    }

    static void report(String title) {
        int c0 = hitStatements.size();
        int c1 = hitBranches.size();
        int c2 = hitConditions.size();
        System.out.println(title);
        System.out.printf("  C0 命令網羅: %d/4 (%.0f%%)  通った文: %s%n",
                c0, c0 * 100.0 / 4, hitStatements);
        System.out.printf("  C1 分岐網羅: %d/2 (%.0f%%)  通った分岐: %s%n",
                c1, c1 * 100.0 / 2, hitBranches);
        System.out.printf("  C2 条件網羅: %d/4 (%.0f%%)  通った条件: %s%n",
                c2, c2 * 100.0 / 4, hitConditions);
        System.out.println();
    }

    public static void main(String[] args) {

        System.out.println("対象: discountRate(age, hasCertificate)");
        System.out.println("仕様: 65歳以上 かつ 手帳あり のときだけ 50%減免");
        System.out.println();
        System.out.println("※ 実行する前に、それぞれ何%になるか紙に予想してから読む");
        System.out.println("================================================================");
        System.out.println();

        // --- ケースA: 正常系1件だけテストした場合 ---
        reset();
        int r1 = discountRate(70, true);
        System.out.println("【ケースA】テスト1件だけ: discountRate(70, true) = " + r1);
        report("");

        // --- ケースB: 正常系＋異常系1件 ---
        reset();
        discountRate(70, true);
        discountRate(70, false);
        System.out.println("【ケースB】2件: (70,true) と (70,false)");
        report("");

        // --- ケースC: 条件を両方falseにするケースも足す ---
        reset();
        discountRate(70, true);
        discountRate(70, false);
        discountRate(60, true);
        System.out.println("【ケースC】3件: (70,true) (70,false) (60,true)");
        report("");

        System.out.println("================================================================");
        System.out.println("ここから読み取ること:");
        System.out.println();
        System.out.println("1. ケースAは C0が100%。つまり『全ステップ通った』。");
        System.out.println("   それでも C1は50%で、if の false側を一度も通っていない。");
        System.out.println("   → 『カバレッジ100%です』と言われたら、必ず『どのカバレッジですか？』と聞く。");
        System.out.println();
        System.out.println("2. ケースBで C1は100%になるが、C2はまだ75%。");
        System.out.println("   『年齢が65歳未満』のケースを一度も通していない。");
        System.out.println("   → 65歳未満の人の減免を間違えるバグがあっても、このテストでは見つからない。");
        System.out.println();
        System.out.println("3. 境界値（age=64, 65）は、このC0/C1/C2の数字には出てこない。");
        System.out.println("   カバレッジ100%でも『>=を>と書き間違えた』バグは見逃す。");
        System.out.println("   → カバレッジは『足りているかの下限チェック』であって、十分性の証明ではない。");
        System.out.println();
        System.out.println("課題: C2を100%にするには、あと何件どんなテストが必要か考えて、");
        System.out.println("      このファイルのmainに追記して実行し、100%になることを確かめる。");
    }
}
