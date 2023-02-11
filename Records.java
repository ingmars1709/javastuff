import java.math.BigDecimal;
import java.util.List;
import java.util.function.BiFunction;
import java.util.function.IntUnaryOperator;

public class Records {
    public static void main(String[] args) {
        var r = new Records();

        var listOfInts = new Cons<>(5, new Cons<>(3, new Cons<>(2, new Nil<>())));

        System.out.println("sum1 = " + r.sum(listOfInts));
        System.out.println("sum2 = " + r.foldr(listOfInts, Integer::sum, 0));

        System.out.println("mul1 = " + r.mul(listOfInts));
        System.out.println("mul2 = " + r.foldr(listOfInts, (i1, i2) -> i1 * i2, 1));

        var expr = new Add(
                        new Mul(new Val(3), new Val(4)),
                        new Add(new Val(8),
                                new Mul(new Val(1), new Val(7))));

        Integer calculate = r.calc(expr);
        System.out.println("calculate = " + calculate);

        String prettyPrint = r.prettyprint(expr);
        System.out.println("pretty print = " + prettyPrint);

        Expr transformed = r.transform(expr, i -> i * 2);

        Integer transformedCalculated = r.calc(transformed);
        System.out.println("calculate = " + transformedCalculated);

        System.out.println("pretty print = " + r.prettyprint(transformed));
    }
    Integer sum(AList<Integer> list) {
        switch (list) {
            case Cons<Integer> (Integer head, AList<Integer> tail) -> { return head + sum(tail); }
            case Nil<Integer> nil -> { return 0; }
            default -> throw new IllegalStateException();
        }
    }
    Integer mul(AList<Integer> list) {
        switch (list) {
            case Cons<Integer> (Integer head, AList<Integer> tail) -> { return head * mul(tail); }
            case Nil<Integer> nil -> { return 1; }
            default -> throw new IllegalStateException();
        }
    }

    <T> T foldr(AList<T> list, BiFunction<T, T, T> f, T base) {
        switch (list) {
            case Cons<T> (T head, AList<T> tail) -> { return f.apply(head, foldr(tail, f, base)); }
            case Nil<T> nil -> { return base; }
            // https://stackoverflow.com/questions/72703351/java-19-pattern-matching-compilation-error-the-switch-statement-does-not-cover
            default -> throw new IllegalStateException();
        }
    }

    Integer calc(Expr e) {
        switch (e) {
            case Add (Expr e1, Expr e2) -> { return calc(e1) + calc(e2); }
            case Mul (Expr e1, Expr e2) -> { return calc(e1) * calc(e2); }
            case Val v -> { return (v.val); }
            default -> throw new IllegalStateException();
        }
    }

    String prettyprint(Expr e) {
        switch (e) {
            case Add (Expr e1, Expr e2) -> { return prettyprint(e1) + "+" + prettyprint(e2); }
            case Mul (Expr e1, Expr e2) -> { return prettyprint(e1) + "*" + prettyprint(e2); }
            case Val v -> { return v.val.toString(); }
            default -> throw new IllegalStateException();
        }
    }
    Expr transform(Expr e, IntUnaryOperator f) {
        switch (e) {
            case Add (Expr e1, Expr e2) -> { return new Add(transform(e1, f), transform(e2, f)); }
            case Mul (Expr e1, Expr e2) -> { return new Mul(transform(e1, f), transform(e2, f)); }
            case Val v -> { return new Val(f.applyAsInt(v.val)); }
            default -> throw new IllegalStateException();
        }
    }

    // Deconstructing and recursion in Java 19
    // Haskell patronen vind ik krachtig en hoe staat het nu met Java daarin?
    // Records in algemeen: https://www.baeldung.com/java-record-vs-lombok
    // Specifieke toepassing en krachtig met recursie
    // Mocht je recursieve datastructuren tegenkomen denk dan ook aan recursieve methodes hierop
    // Geen instanceof maar switching en access tot de variabele
    // Stream API is goed in Collections. Dit zijn arbitraire datastructuren


    sealed interface AList<T> permits Cons, Nil {}
    record Cons<T>(T head, AList<T> tail) implements AList<T> { }
    record Nil<T>() implements AList<T> { }

    sealed interface Expr permits Add, Mul, Val {}
    record Add(Expr e1, Expr e2) implements Expr {}
    record Mul(Expr e1, Expr e2) implements Expr {}
    record Val(Integer val) implements Expr {}
}