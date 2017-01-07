package main;

import main.resolvers.Resolver;
import org.apache.commons.lang3.tuple.Pair;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Created by lukza on 31.12.2016.
 */
public class SetCover {

    public static Pair<String, Integer> getResult(Map<OneMove, List<Resolver>> map) {
        final Set<OneMove> movesToDescribe = new HashSet<>(map.keySet());
        final Map<Resolver, Set<OneMove>> resolverListMap = new HashMap<>();
        final List<Resolver> result = new ArrayList<>();

        map.forEach((k, v) ->
                v.forEach(r -> {
                    resolverListMap.putIfAbsent(r, new HashSet<>());
                    resolverListMap.get(r).add(k);
                }));

//        final List<Map.Entry<OneMove, List<Resolver>>> collect =
//                map.entrySet().stream().sorted(Comparator.comparingInt(e -> e.getValue().size())).collect(Collectors.toList());

        while (!movesToDescribe.isEmpty()) {

            Resolver best = resolverListMap.entrySet()
                    .stream()
                    .sorted((r1, r2) -> Integer.compare(r2.getValue().size(), r1.getValue().size()))
                    .map(Map.Entry::getKey).findFirst().get();

            result.add(best);
            Set<OneMove> toRemove = resolverListMap.get(best);
            movesToDescribe.removeAll(toRemove);
            resolverListMap.forEach((k, v) -> {
                v.removeAll(toRemove);
            });

        }


//        for (Map.Entry<OneMove, List<Resolver>> entry : collect) {
//            if (movesToDescribe.contains(entry.getKey())) {
//                final Resolver resolver = entry.getValue().stream()
//                        .sorted(Comparator.comparingInt(Resolver::getValue))
//                        .findFirst().get();
//                result.add(resolver);
//                movesToDescribe.removeAll(resolverListMap.get(resolver));
//            }
//        }

        return Pair.of(result.stream()
                        .sorted(Comparator.comparingInt(Resolver::getValue))
                        .map(Resolver::getDescription)
                        .collect(Collectors.joining(" | \n |  ")),
                result.stream().map(Resolver::getValue).reduce(0, Integer::sum));
    }


}
