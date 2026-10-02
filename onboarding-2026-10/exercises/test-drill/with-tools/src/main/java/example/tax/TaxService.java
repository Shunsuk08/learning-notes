package example.tax;

/**
 * 自動車税の算出（練習用の架空の仕様）。
 *
 * 仕様:
 *   1. 排気量660cc以下は軽自動車 10,800円、それ以外は 39,500円
 *   2. 初度登録から13年以上は 15%重課
 *   3. 減免対象は半額
 *   4. 算出したら監査ログを1件残す
 *   5. 車両が見つからない場合は IllegalArgumentException。その場合も監査ログを残す
 */
public class TaxService {

    private static final int LIGHT_CAR_TAX = 10800;
    private static final int STANDARD_CAR_TAX = 39500;
    private static final int LIGHT_CAR_DISPLACEMENT = 660;
    private static final int HEAVY_TAX_AGE = 13;

    private final VehicleRepository repository;
    private final AuditLog auditLog;

    public TaxService(VehicleRepository repository, AuditLog auditLog) {
        this.repository = repository;
        this.auditLog = auditLog;
    }

    public int calculate(String vehicleId) {
        Vehicle vehicle = repository.findById(vehicleId);
        if (vehicle == null) {
            auditLog.record("車両なし:" + vehicleId);
            throw new IllegalArgumentException("車両が見つかりません: " + vehicleId);
        }

        int tax = (vehicle.displacement() <= LIGHT_CAR_DISPLACEMENT)
                ? LIGHT_CAR_TAX
                : STANDARD_CAR_TAX;

        if (vehicle.ageYears() >= HEAVY_TAX_AGE) {
            // 金額を double で計算している。ここは改造課題の対象（§5）
            tax = (int) (tax * 1.15);
        }

        if (vehicle.reduced()) {
            tax = tax / 2;
        }

        auditLog.record("税額算出:" + vehicleId + "=" + tax);
        return tax;
    }
}
