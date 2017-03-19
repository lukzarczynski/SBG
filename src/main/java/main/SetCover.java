package main;

import main.description.ReparingRun;
import main.model.OneMove;
import main.resolvers.Resolver;
import org.apache.commons.lang3.tuple.Pair;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Created by lukza on 31.12.2016.
 */
public class SetCover {

    private static final Comparator<Map.Entry<Resolver, Set<OneMove>>> resolverValueComparator = (r1, r2) -> Integer.compare(r2.getValue().size(), r1.getValue().size());


    public static Pair<String, Integer> getResult(Map<OneMove, List<Resolver>> map, Pair<Integer, Integer> xy) {

        return Pair.of(
                map.values().stream().flatMap(Collection::stream)
                        .map(Resolver::getDescription)
                        .distinct()
                        .collect(Collectors.joining(" \n ")),
                15);
    }

    public static Pair<String, Integer> getResult2(Map<OneMove, List<Resolver>> map, Pair<Integer, Integer> xy) {
        final Set<OneMove> movesToDescribe = new HashSet<>(map.keySet());
        final Map<Resolver, Set<OneMove>> resolverListMap = new HashMap<>();
        final List<Resolver> result = new ArrayList<>();

        map.forEach((k, v) ->
                v.forEach(r -> {
                    resolverListMap.putIfAbsent(r, new HashSet<>());
                    resolverListMap.get(r).add(k);
                }));


        while (!movesToDescribe.isEmpty()) {

            Resolver best = resolverListMap.entrySet()
                    .stream()
                    .sorted(resolverValueComparator)
                    .map(Map.Entry::getKey).findFirst().get();

            result.add(best);
            Set<OneMove> toRemove = resolverListMap.get(best);
            movesToDescribe.removeAll(toRemove);
            resolverListMap.forEach((k, v) -> {
                v.removeAll(toRemove);
            });

        }

        return Pair.of(ReparingRun.getRepairedDescription(result, resolverListMap, xy),
                ParamsAndEvaluators.fp(result));
    }


}
