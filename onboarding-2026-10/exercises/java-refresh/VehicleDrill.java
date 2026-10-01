import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

// 実行: java VehicleDrill.java
// TODO の5メソッドを、for文を使わずに（ラムダ・Stream・Optionalで）書く。全部 OK になれば完了。
public class VehicleDrill {

    static class Vehicle {
        private final String id;
        private final String prefecture;
        private final String ownerName; // 未登録なら null
        private final BigDecimal taxAmount;
        private final boolean paid;

        Vehicle(String id, String prefecture, String ownerName, String taxAmount, boolean paid) {
            this.id = id;
            this.prefecture = prefecture;
            this.ownerName = ownerName;
            this.taxAmount = new BigDecimal(taxAmount);
            this.paid = paid;
        }

        String getId() { return id; }
        String getPrefecture() { return prefecture; }
        String getOwnerName() { return ownerName; }
        BigDecimal getTaxAmount() { return taxAmount; }
        boolean isPaid() { return paid; }
    }

    static final List<Vehicle> VEHICLES = List.of(
            new Vehicle("V001", "千葉", "佐藤", "30500", true),
            new Vehicle("V002", "千葉", "鈴木", "36000", false),
            new Vehicle("V003", "東京", null, "25000", false),
            new Vehicle("V004", "東京", "高橋", "43500", false),
            new Vehicle("V005", "埼玉", "田中", "30500", true),
            new Vehicle("V006", "千葉", "伊藤", "25000", false));

    static Optional<Vehicle> findById(String id) {
        return VEHICLES.stream().filter(v -> v.getId().equals(id)).findFirst();
    }

    // ---- 参考: 昔の書き方（これと同じ結果を返すように TODO を書く） ----

    static List<String> unpaidIdsOld() {
        List<String> result = new ArrayList<>();
        for (Vehicle v : VEHICLES) {
            if (!v.isPaid()) {
                result.add(v.getId());
            }
        }
        return result;
    }

    static BigDecimal unpaidTotalOld() {
        BigDecimal total = BigDecimal.ZERO;
        for (Vehicle v : VEHICLES) {
            if (!v.isPaid()) {
                total = total.add(v.getTaxAmount());
            }
        }
        return total;
    }

    static Map<String, Long> countByPrefectureOld() {
        Map<String, Long> result = new HashMap<>();
        for (Vehicle v : VEHICLES) {
            result.put(v.getPrefecture(), result.getOrDefault(v.getPrefecture(), 0L) + 1);
        }
        return result;
    }

    static String ownerNameOrUnknownOld(String id) {
        Optional<Vehicle> v = findById(id);
        if (v.isPresent()) {
            if (v.get().getOwnerName() != null) {
                return v.get().getOwnerName();
            }
        }
        return "不明";
    }

    // ---- TODO: ここから下を書く ----

    // Q1. 未納の車両IDの一覧（元の順番のまま）
    static List<String> unpaidIds() {
        return null; // TODO
    }

    // Q2. 未納の税額の合計（BigDecimal で。double を使わない）
    static BigDecimal unpaidTotal() {
        return null; // TODO
    }

    // Q3. 都道府県ごとの台数
    static Map<String, Long> countByPrefecture() {
        return null; // TODO
    }

    // Q4. IDから所有者名。車両が無い、または所有者名が null なら "不明"。isPresent()/get() を使わない
    static String ownerNameOrUnknown(String id) {
        return null; // TODO
    }

    // Q5. 未納の車両を「税額の高い順、同額ならIDの昇順」に並べたIDの一覧
    static List<String> unpaidIdsSortedByAmount() {
        return null; // TODO
    }

    // ---- 採点 ----

    public static void main(String[] args) {
        check("Q1", unpaidIdsOld(), unpaidIds());
        check("Q2", unpaidTotalOld(), unpaidTotal());
        check("Q3", countByPrefectureOld(), countByPrefecture());
        check("Q4a", "鈴木", ownerNameOrUnknown("V002"));
        check("Q4b", "不明", ownerNameOrUnknown("V003"));
        check("Q4c", "不明", ownerNameOrUnknown("V999"));
        check("Q5", List.of("V004", "V002", "V003", "V006"), unpaidIdsSortedByAmount());
    }

    static void check(String name, Object expected, Object actual) {
        boolean ok = expected.equals(actual);
        System.out.println((ok ? "OK " : "NG ") + name + (ok ? "" : "  期待=" + expected + " 実際=" + actual));
    }
}
