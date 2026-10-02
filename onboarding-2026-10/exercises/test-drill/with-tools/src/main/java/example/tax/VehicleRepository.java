package example.tax;

/** DBアクセスの窓口。テストではスタブ／モックに差し替える */
public interface VehicleRepository {
    Vehicle findById(String id);
}
