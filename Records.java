import java.util.function.BiFunction;

public class Records {
    public static void main(String[] args) {
        var r = new Records();

        var listOfInts = new Cons<>(5, new Cons<>(3, new Cons<>(2, new Nil<>())));
        var listOfStrings = new Cons<>("a", new Cons<>("b", new Cons<>("c", new Nil<>())));

        System.out.println("sum1 = " + r.sum(listOfInts));
        System.out.println("sum2 = " + r.foldr(listOfInts, Integer::sum, 0));

        System.out.println("mul1 = " + r.mul(listOfInts));
        System.out.println("mul2 = " + r.foldr(listOfInts, (i1, i2) -> i1 * i2, 1));

        System.out.println("concat1 = " + r.concat(listOfStrings));
        System.out.println("concat2 = " + r.foldr(listOfStrings, String::concat, ""));

        // TODO: calculator, compiler, dictaat
    }

    Integer sum(final AList<Integer> list) {
        switch (list) {
            case Cons<Integer> (Integer head, AList<Integer> tail) -> { return head + sum(tail); }
            case Nil<Integer> nil -> { return 0; }
            default -> throw new IllegalStateException();
        }
    }
    Integer mul(final AList<Integer> list) {
        switch (list) {
            case Cons<Integer> (Integer head, AList<Integer> tail) -> { return head * mul(tail); }
            case Nil<Integer> nil -> { return 1; }
            default -> throw new IllegalStateException();
        }
    }

    String concat(final AList<String> list) {
        switch (list) {
            case Cons<String> (String head, AList<String> tail) -> { return head + concat(tail); }
            case Nil<String> nil -> { return ""; }
            default -> throw new IllegalStateException();
        }
    }

    <T> T foldr(final AList<T> list, BiFunction<T, T, T> f, T base) {
        switch (list) {
            case Cons<T> (T head, AList<T> tail) -> { return f.apply(head, foldr(tail, f, base)); }
            case Nil<T> nil -> { return base; }

            // https://stackoverflow.com/questions/72703351/java-19-pattern-matching-compilation-error-the-switch-statement-does-not-cover
            default -> throw new IllegalStateException();
        }
    }

    // Deconstructing and recursion in Java 19
    // Haskell patronen vind ik krachtig en hoe staat het nu met Java daarin?
    // Records in algemeen: https://www.baeldung.com/java-record-vs-lombok
    // Specifieke toepassing en krachtig met recursie
    // Mocht je recursieve datastructuren tegenkomen denk dan ook aan recursieve methodes hierop
    // Geen instanceof maar switching en access tot de variabele
    // foldr als library methode

    sealed interface AList<T> permits Cons, Nil {}
    record Cons<T>(T head, AList<T> tail) implements AList<T> { }
    record Nil<T>() implements AList<T> { }
}