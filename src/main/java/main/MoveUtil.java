package main;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Created by lukasz on 06.12.16.
 */
public final class MoveUtil {

    /**
     * a contains all from b
     *
     * @param a
     * @param b
     * @return
     */
    public static boolean containsAll(Set<OneMove> a, Set<OneMove> b) {
        return b.stream().allMatch(a::contains);
    }

    public static Set<OneMove> sum(Set<OneMove> a, Set<OneMove> b) {
        final Set<OneMove> result = new HashSet<>();
        result.addAll(a);
        result.addAll(b);
        return result;
    }

    public static Set<OneMove> intersection(Set<OneMove> a, Set<OneMove> b) {
        return a.stream().filter(b::contains).collect(Collectors.toSet());
    }

    public static Set<OneMove> subtract(Set<OneMove> a, Set<OneMove> b) {
        return a.stream().filter(m -> !b.contains(m)).collect(Collectors.toSet());
    }
}
