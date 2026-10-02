package example.tax;

/** 監査ログ。戻り値が無いので「呼ばれたかどうか」でしか検証できない＝モック向き */
public interface AuditLog {
    void record(String message);
}
