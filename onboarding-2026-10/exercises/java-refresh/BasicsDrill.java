import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

// 実行: java BasicsDrill.java
// 実行する前に、各問の出力を紙に予想して書く。実行して答え合わせし、外れた問は理由を1行で書く。
public class BasicsDrill {

    interface TaxCalculator {
        BigDecimal calc(BigDecimal base);
    }

    static class StandardCalculator implements TaxCalculator {
        public BigDecimal calc(BigDecimal base) {
            return base;
        }
    }

    static class EcoCalculator extends StandardCalculator {
        @Override
        public BigDecimal calc(BigDecimal base) {
            return base.multiply(new BigDecimal("0.75"));
        }
    }

    static class Plate {
        final String number;

        Plate(String number) {
            this.number = number;
        }

        @Override
        public boolean equals(Object o) {
            return o instanceof Plate && ((Plate) o).number.equals(number);
        }
        // hashCode を書いていない
    }

    static class Resource implements AutoCloseable {
        final String name;

        Resource(String name) {
            this.name = name;
            System.out.println("  open " + name);
        }

        @Override
        public void close() {
            System.out.println("  close " + name);
        }
    }

    static int parse(String s) {
        try {
            return Integer.parseInt(s);
        } catch (NumberFormatException e) {
            return -1;
        } finally {
            System.out.println("  finally " + s);
        }
    }

    static void addOne(List<String> list, String s) {
        list.add("1");
        s = s + "1";
    }

    public static void main(String[] args) {
        System.out.println("Q1 文字列の比較");
        String a = "abc";
        String b = new String("abc");
        System.out.println("  " + (a == b) + " " + a.equals(b));

        System.out.println("Q2 Integerの比較");
        Integer x1 = 127, y1 = 127, x2 = 128, y2 = 128;
        System.out.println("  " + (x1 == y1) + " " + (x2 == y2) + " " + x2.equals(y2));

        System.out.println("Q3 お金の計算");
        System.out.println("  " + (0.1 + 0.2));
        System.out.println("  " + new BigDecimal("0.1").add(new BigDecimal("0.2")));
        System.out.println("  " + new BigDecimal("1.0").equals(new BigDecimal("1.00"))
                + " " + (new BigDecimal("1.0").compareTo(new BigDecimal("1.00")) == 0));

        System.out.println("Q4 ポリモーフィズム");
        TaxCalculator c = new EcoCalculator();
        System.out.println("  " + c.calc(new BigDecimal("40000")));

        System.out.println("Q5 equalsだけ書いたクラスをSetに入れる");
        Set<Plate> plates = new HashSet<>();
        plates.add(new Plate("品川500あ1234"));
        plates.add(new Plate("品川500あ1234"));
        System.out.println("  " + plates.size() + " " + new Plate("X").equals(new Plate("X")));

        System.out.println("Q6 try-with-resources の閉じる順番");
        try (Resource r1 = new Resource("A"); Resource r2 = new Resource("B")) {
            System.out.println("  body");
        }

        System.out.println("Q7 例外とfinally");
        System.out.println("  result " + parse("12"));
        System.out.println("  result " + parse("x"));

        System.out.println("Q8 引数の渡し方");
        List<String> list = new ArrayList<>();
        String s = "s";
        addOne(list, s);
        System.out.println("  " + list + " " + s);

        System.out.println("Q9 整数の割り算");
        System.out.println("  " + (7 / 2) + " " + (7 % 2) + " " + (7 / 2.0));

        System.out.println("Q10 nullの扱い");
        String owner = null;
        System.out.println("  " + "未登録".equals(owner) + " " + ("所有者:" + owner));
        try {
            System.out.println(owner.equals("未登録"));
        } catch (NullPointerException e) {
            System.out.println("  NullPointerException");
        }
    }
}
