package example.tax;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

/**
 * Mockitoを使ったテスト。StubMockDrill.java で手書きしたものと、やっていることは同じ。
 *
 * 対応関係:
 *   手書きの StubRepository   → @Mock + when(...).thenReturn(...)   【値を返させる＝スタブ】
 *   手書きの MockAuditLog     → @Mock + verify(...)                 【呼ばれ方を見る＝モック】
 *
 * ★この時点では、わざとテストが足りない状態にしてある。
 *   mvn test の後に target/site/jacoco/index.html を開き、
 *   TaxService のどの行が赤・黄になっているかを確認してからテストを足すこと。
 */
@ExtendWith(MockitoExtension.class)
class TaxServiceTest {

    @Mock
    private VehicleRepository repository;   // DBの代わり（ニセモノが自動で作られる）

    @Mock
    private AuditLog auditLog;              // 監査ログの代わり

    @InjectMocks
    private TaxService taxService;          // 上の2つを渡して組み立ててくれる

    @Test
    @DisplayName("軽自動車(660cc以下)は10,800円")
    void 軽自動車() {
        // Arrange: 「findByIdが呼ばれたら、この車両を返せ」とニセモノに仕込む＝スタブ化
        when(repository.findById("A-001"))
                .thenReturn(new Vehicle("A-001", 660, 3, false));

        // Act
        int tax = taxService.calculate("A-001");

        // Assert: 戻り値の検証（状態の検証）
        assertEquals(10800, tax);
    }

    @Test
    @DisplayName("普通車(661cc以上)は39,500円")
    void 普通車() {
        when(repository.findById("B-001"))
                .thenReturn(new Vehicle("B-001", 1500, 3, false));

        assertEquals(39500, taxService.calculate("B-001"));
    }

    @Test
    @DisplayName("算出に成功したら監査ログを1件残す")
    void 監査ログが残る() {
        when(repository.findById("A-001"))
                .thenReturn(new Vehicle("A-001", 660, 3, false));

        taxService.calculate("A-001");

        // 呼ばれ方の検証（振る舞いの検証）。これはモックにしかできない
        verify(auditLog).record("税額算出:A-001=10800");

        // 「呼ばれていないこと」も検証できる（正常時にエラーログは出ない）
        verify(auditLog, never()).record("車両なし:A-001");
    }

    // ------------------------------------------------------------------
    // ★ここから下は自分で書く（§4の課題）
    //
    // TODO 1: 13年以上の重課（ageYears=15）のテスト
    // TODO 2: 減免あり（reduced=true）のテスト
    // TODO 3: 車両が見つからない場合に IllegalArgumentException が出ること
    //         （assertThrows を使う）＋ そのとき監査ログに "車両なし:..." が残ること
    //
    // 書く前に jacoco のレポートで赤い行を確認し、
    // 書いた後にもう一度 mvn test してレポートが緑になることを確かめる。
    // ------------------------------------------------------------------
}
