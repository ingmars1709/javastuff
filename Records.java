import java.util.List;
import java.util.function.IntUnaryOperator;

import static java.util.Arrays.asList;

public class Records {
    public static void main(String[] args) {
        AList<Integer> listOfInts = create(asList(1,2,3,4,5));

        System.out.println("sum1 = " + sum(listOfInts));
        System.out.println("mul1 = " + mul(listOfInts));

        var expr = new Add(new Mul(new Val(3), new Val(4)),
                           new Add(new Val(8),
                                   new Mul(new Val(1), new Val(7)))
                          );

        Integer calculate = calc(expr);
        System.out.println("calculate = " + calculate);

        String prettyPrint = prettyprint(expr);
        System.out.println("pretty print = " + prettyPrint);

        Expr transformed = transform(expr, i -> i * 2);

        Integer transformedCalculated = calc(transformed);
        System.out.println("calculate = " + transformedCalculated);

        System.out.println("pretty print = " + prettyprint(transformed));
    }

    static <T> AList<T> create(List<T> list) {
        return list.isEmpty() ? new Empty<>() : new Cons<T>(list.get(0), create(list.subList(1, list.size())));
    }

    static Integer sum(AList<Integer> list) {
        switch (list) {
            case Cons<Integer> (Integer head, AList<Integer> tail) -> { return head + sum(tail); }
            case Empty<Integer> empty -> { return 0; }
            default -> throw new IllegalStateException();
        }
    }
    static Integer mul(AList<Integer> list) {
        switch (list) {
            case Cons<Integer> (Integer head, AList<Integer> tail) -> { return head * mul(tail); }
            case Empty<Integer> empty -> { return 1; }
            default -> throw new IllegalStateException();
        }
    }
    static <T> AList<T> take(Integer n, AList<T> l) {
        switch (l) {
            case Cons<T> (T head, AList<T> tail) -> { return n == 0 ? new Empty<T>() : new Cons<T>(head, take (n-1, tail)); }
            case Empty<T> empty -> { return new Empty<>(); }
            default -> throw new IllegalStateException();
        }
    }

    // foldr
    // perms
    // comprehensies


    static Integer calc(Expr e) {
        switch (e) {
            case Add (Expr e1, Expr e2) -> { return calc(e1) + calc(e2); }
            case Mul (Expr e1, Expr e2) -> { return calc(e1) * calc(e2); }
            case Val v -> { return (v.val); }
            default -> throw new IllegalStateException();
        }
    }
    static String prettyprint(Expr e) {
        switch (e) {
            case Add (Expr e1, Expr e2) -> { return prettyprint(e1) + "+" + prettyprint(e2); }
            case Mul (Expr e1, Expr e2) -> { return prettyprint(e1) + "*" + prettyprint(e2); }
            case Val v -> { return v.val.toString(); }
            default -> throw new IllegalStateException();
        }
    }
    static Expr transform(Expr e, IntUnaryOperator f) {
        switch (e) {
            case Add (Expr e1, Expr e2) -> { return new Add(transform(e1, f), transform(e2, f)); }
            case Mul (Expr e1, Expr e2) -> { return new Mul(transform(e1, f), transform(e2, f)); }
            case Val v -> { return new Val(f.applyAsInt(v.val)); }
            default -> throw new IllegalStateException();
        }
    }

    // Functioneel programmeren in Java. Niet alleen OO is een paradigma
    // Deconstructing and recursion in Java 19+
    // Haskell patronen vind ik krachtig en hoe staat het nu met Java daarin?
    // Mocht je recursieve datastructuren tegenkomen denk dan ook aan recursieve methodes hierop
    // Stream API is goed in Collections. Dit zijn arbitraire datastructuren en sterk afgeleid van Haskell
    // Non destructive
    // pure functions

    sealed interface AList<T> permits Cons, Empty {}
    record Cons<T>(T head, AList<T> tail) implements AList<T> { }
    record Empty<T>() implements AList<T> { }

    sealed interface Expr permits Add, Mul, Val {}
    record Add(Expr e1, Expr e2) implements Expr {}
    record Mul(Expr e1, Expr e2) implements Expr {}
    record Val(Integer val) implements Expr {}
}