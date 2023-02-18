import java.util.ArrayList;
import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.IntUnaryOperator;

import static java.util.Arrays.asList;

public class Records {
    public static void main(String[] args) {

        AList<Integer> ints = from(asList(1, 2, 3, 4, 5));
        AList<Integer> otherInts = from(asList(8, 9, 10));

        System.out.println("sum1 = " + sum(ints));
        System.out.println("mul1 = " + mul(ints));
        System.out.println("sum2 = " + foldr(ints, Integer::sum, 0));
        System.out.println("mul2 = " + foldr(ints, (i1, i2) -> i1 * i2, 1));

        AList<String> mappedToString = map(i -> "'" + i.toString() + "'", ints);
        System.out.println("mappedToString = " + mappedToString);

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

        System.out.println("take(3, " + ints + ") = " + take(3, ints));

        AList<Integer> appended = append(ints, otherInts);
        System.out.println("append(ints, otherInts) = " + appended);

        List<Integer> to = new ArrayList<>();
        project(appended, to);
        System.out.println("to Java List: " + to);

        System.out.println("sublists = " + sublists(ints));

        // TODO list comprehension
        // TODO sorting
    }

    static <T> AList<T> from(List<T> list) {
        return list.isEmpty() ? new Empty<>() : new Cons<T>(list.get(0), from(list.subList(1, list.size())));
    }

    static <T> void project(AList<T> from, List<T> to) {
        switch (from) {
            case Cons<T> (T head, AList<T> tail) -> { to.add(head); project(tail, to); }
            case Empty<T> empty -> { }
            default -> throw new IllegalStateException();
        }
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

    static <T> T foldr(AList<T> list, BiFunction<T,T,T> f, T base) {
        switch (list) {
            case Cons<T> (T head, AList<T> tail) -> { return f.apply(head, foldr(tail, f, base)); }
            case Empty<T> empty -> { return base; }
            default -> throw new IllegalStateException();
        }
    }

    static <A,B> AList<B> map(Function<A,B> f, AList<A> list) {
        switch (list) {
            case Cons<A> (A head, AList<A> tail) -> { return new Cons<>(f.apply(head), map(f, tail)); }
            case Empty<A> empty -> { return new Empty<>(); }
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

    static <T> AList<T> append(AList<T> l1, AList<T> l2) {
        switch (l1) {
            case Cons<T> (T head, AList<T> tail) -> { return new Cons<>(head, append(tail, l2)); }
            case Empty<T> empty -> { return l2; }
            default -> throw new IllegalStateException();
        }
    }

    static <T> AList<AList<T>> sublists(AList<T> list) {
        switch (list) {
            case Cons<T> (T head, AList<T> tail) -> {
                var tailSubs = sublists(tail);
                return append(map(l -> new Cons<>(head, l), tailSubs), tailSubs);
            }
            case Empty<T> empty -> { return new Cons<>(new Empty<>(), new Empty<>()); }
            default -> throw new IllegalStateException();
        }
    }

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
    record Cons<T>(T head, AList<T> tail) implements AList<T> {
        private String listToString(AList<T> list) {
            switch(list) {
                case Cons<T> (T h, Empty<T> l) -> { return h.toString(); }
                case Cons<T> (T h, AList<T> l) -> {  return h + "," + listToString(l); }
                case Empty<T> empty -> { return ""; }
                default -> throw new IllegalStateException();
            }
        }
        @Override
        public String toString() {
            return "[" + listToString(this) + "]";
        }
    }
    record Empty<T>() implements AList<T> {
        @Override
        public String toString() {
            return "[]";
        }
    }

    sealed interface Expr permits Add, Mul, Val {}
    record Add(Expr e1, Expr e2) implements Expr {}
    record Mul(Expr e1, Expr e2) implements Expr {}
    record Val(Integer val) implements Expr {}
    record Tuple<A, B>(A a, B b) {}
}