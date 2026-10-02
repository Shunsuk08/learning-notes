import java.math.BigDecimal;
import java.util.*;

public class Verify {
    static void change(List<String> list, String s) { list.add("x"); s = s + "1"; }
    public static void main(String[] args) {
        Integer a = 100, b = 100, c = 200, d = 200;
        System.out.println("Integer100== " + (a == b) + " / Integer200== " + (c == d));
        System.out.println("(int)3.9 = " + (int) 3.9);
        System.out.println("0.1+0.2==0.3 " + (0.1 + 0.2 == 0.3));
        System.out.println("BigDecimal add = " + new BigDecimal("0.1").add(new BigDecimal("0.2")));
        System.out.println("BigDecimal 1.0 equals 1.00 = " + new BigDecimal("1.0").equals(new BigDecimal("1.00"))
            + " / compareTo = " + new BigDecimal("1.0").compareTo(new BigDecimal("1.00")));
        System.out.println("new String== " + (new String("a") == new String("a")));
        int n = 2;
        String kind = switch (n) { case 1 -> "A"; case 2 -> "B"; default -> "C"; };
        System.out.println("switch expr = " + kind);
        System.out.println("toList = " + List.of(1,2,3).stream().filter(x -> x > 1).toList());
        Map<String,Integer> m = new HashMap<>();
        System.out.println("map.get(missing) = " + m.get("none") + " / getOrDefault = " + m.getOrDefault("none", 0));
        String owner = null;
        System.out.println("\"未登録\".equals(null) = " + "未登録".equals(owner));
        try { owner.equals("未登録"); } catch (NullPointerException e) { System.out.println("owner.equals -> NPE"); }
        System.out.println("Optional map/orElse = " + Optional.<String>empty().map(String::length).map(String::valueOf).orElse("未登録"));
        List<String> list = new ArrayList<>(); String s = "s";
        change(list, s);
        System.out.println("pass-by-value: list=" + list + " s=" + s);
        System.out.println("AutoCloseable is super of Closeable: " + AutoCloseable.class.isAssignableFrom(java.io.Closeable.class));
        try (AutoCloseable r = () -> System.out.println("closed AutoCloseable")) { } catch (Exception e) { }
        int[] arr = new int[3];
        System.out.println("arr.length=" + arr.length + " list.size()=" + list.size());
    }
}
