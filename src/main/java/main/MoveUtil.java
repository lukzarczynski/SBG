package main;

import main.model.OneMove;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Created by lukasz on 06.12.16.
 */
public final class MoveUtil {

    /**
     * a contains all from b
     */
    public static boolean containsAll(Collection<OneMove> a, Collection<OneMove> b) {
        return b.stream().allMatch(a::contains);
    }

    public static Set<OneMove> sum(Collection<OneMove> a, Collection<OneMove> b) {
        final Set<OneMove> result = new HashSet<>();
        result.addAll(a);
        result.addAll(b);
        return result;
    }

    public static Set<OneMove> intersection(Collection<OneMove> a, Collection<OneMove> b) {
        return a.stream().filter(b::contains).collect(Collectors.toSet());
    }

    /**
     * a - b
     *
     * @return a - b
     */
    public static Set<OneMove> subtract(Collection<OneMove> a, Collection<OneMove> b) {
        return a.stream().filter(m -> !b.contains(m)).collect(Collectors.toSet());
    }

    public static boolean containsAllPrefixes(Collection<OneMove> moves, Collection<OneMove> b) {

        if (b.size() > moves.size()) {
            return false;
        }

        Set<String> collect = moves.stream().map(OneMove::toString).collect(Collectors.toSet());

        return b.stream().map(OneMove::toString)
                .allMatch(bm -> collect.stream().anyMatch(m -> m.startsWith(bm)));

    }
}
