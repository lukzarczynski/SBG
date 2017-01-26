package main.description;

import main.model.OneMove;
import main.operator.*;
import main.piececlass.PieceClass;
import main.piececlass.XYLeaper;
import main.piececlass.XYRider;
import main.piececlass.XYYXLeaper;
import main.resolvers.Resolver;
import main.resolvers.SimpleCompositeResolver;
import main.resolvers.SimplePieceResolver;
import main.resolvers.SpecialCaseResolver;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Created by lukza on 26.01.2017.
 */
public class ReparingRun {

    public static String getRepairedDescription(List<Resolver> result,
                                                Map<Resolver, Set<OneMove>> resolverListMap,
                                                Pair<Integer,Integer> boardSize) {

        StringBuilder description = new StringBuilder();

        final List<SimplePieceResolver> simplePieceResolvers =
                result.stream().filter(r -> r instanceof SimplePieceResolver)
                        .map(r -> (SimplePieceResolver) r)
                        .collect(Collectors.toList());


        final Map<Set<Operator>, List<SimplePieceResolver>> collect
                = simplePieceResolvers.stream().collect(Collectors.groupingBy(SimplePieceResolver::getOperators));

        List<SimpleCompositeResolver> compositeResolvers =
                result.stream().filter(r -> r instanceof SimpleCompositeResolver)
                        .map(r -> (SimpleCompositeResolver) r)
                        .collect(Collectors.toList());

        description.append(handleSimpleResolvers(collect));
        description.append(handleCompositeResolvers(compositeResolvers));


        List<SpecialCaseResolver> specialCaseResolvers =
                result.stream().filter(r -> r instanceof SpecialCaseResolver)
                        .map(r -> (SpecialCaseResolver) r)
                        .collect(Collectors.toList());

        description.append("\n");

        description.append(specialCaseResolvers.stream().map(Resolver::getDescription).collect(Collectors.joining(" || ")));

        return description.toString();
    }

    private static String handleSimpleResolvers(Map<Set<Operator>, List<SimplePieceResolver>> collect) {
        final StringBuilder builder = new StringBuilder();
        collect.forEach((ops, resolvers) -> {
            builder.append(handleSimpleResolver(ops, resolvers));
        });
        return builder.toString();
    }

    private static String handleSimpleResolver(Set<Operator> ops, List<SimplePieceResolver> resolvers) {
        final Set<SimplePieceResolver> riders = resolvers.stream()
                .filter(r -> r.getPieceClass() instanceof XYRider)
                .collect(Collectors.toSet());

        final Set<SimplePieceResolver> leapers = resolvers.stream()
                .filter(r -> r.getPieceClass() instanceof XYLeaper)
                .collect(Collectors.toSet());
        leapers.addAll(
                resolvers.stream()
                        .filter(r -> r.getPieceClass() instanceof XYYXLeaper)
                        .map(r -> {
                            final Pair<Integer, Integer> xy = r.getPieceClass().getXy();
                            SimplePieceResolver spr1 = new SimplePieceResolver(
                                    new XYLeaper(xy.getKey(), xy.getValue()), r.getOperators());
                            SimplePieceResolver spr2 = new SimplePieceResolver(
                                    new XYLeaper(xy.getValue(), xy.getKey()), r.getOperators());

                            return Arrays.asList(spr1, spr2);
                        }).flatMap(Collection::stream)
                        .collect(Collectors.toSet()));


        if (!riders.isEmpty()) {
            return "\n - " + describeRiders(riders.stream()
                    .map(r -> (XYRider) r.getPieceClass())
                    .collect(Collectors.toSet()), ops);
        }
        if (!leapers.isEmpty()) {
            return "\n - " + describeLeaper(leapers.stream()
                    .map(r -> (XYLeaper) r.getPieceClass())
                    .collect(Collectors.toSet()), ops);
        }

        return "";
    }

    private static String handleCompositeResolvers(List<SimpleCompositeResolver> simplePieceResolvers) {
        StringBuilder builder = new StringBuilder();

        Map<String, List<SimplePieceResolver>> collect = new HashMap<>();

        simplePieceResolvers.forEach(composite -> {
            final String firstPart
                    = handleSimpleResolver(composite.getResolver1().getOperators(), Arrays.asList(composite.getResolver1()));

            collect.putIfAbsent(firstPart, new ArrayList<>());
            collect.get(firstPart).add(composite.getResolver2());
        });


        collect.forEach((firstPart, Part) -> {

            builder.append("\n - ")
                    .append(firstPart).append(" and then ")
                    .append(handleSimpleResolvers(Part.stream().collect(Collectors.groupingBy(SimplePieceResolver::getOperators)))
                            .replaceAll("\n - ",""));
        });

        return builder.toString();

    }

    private static String describeRiders(Set<XYRider> rider, Set<Operator> operators) {
        StringBuilder builder = new StringBuilder();
        builder.append("Rides");

        builder.append(getTimesIfPresent(operators));
        builder.append(getCapturingIfPresent(operators));

        if (hasInstance(operators, OverOwnPieceInstead.class)) {
            builder.append(" over own pieces");
        } else if (hasInstance(operators, OverEnemyPieceInstead.class)) {
            builder.append(" over opponent pieces");
        }

        final List<String> collect = rider.stream().map(r -> {
            if (r.getXy().equals(Pair.of(0, 1))) {
                if (hasInstance(operators, Forward.class)) {
                    return " vertically forward";
                } else if (hasInstance(operators, Backwards.class)) {
                    return " vertically backwards";
                } else {
                    return " vertically";
                }
            } else if (r.getXy().equals(Pair.of(1, 0))) {
                return " horizontally";
            } else if (r.getXy().equals(Pair.of(1, 1))) {
                if (hasInstance(operators, Forward.class)) {
                    return " diagonally forward";
                } else if (hasInstance(operators, Backwards.class)) {
                    return " diagonally backwards";
                } else {
                    return " diagonally";
                }
            } else {
                return " over vector " + r.getXy().toString();
            }
        }).collect(Collectors.toList());

        final String xx;
        if (collect.containsAll(Arrays.asList(" diagonally", " vertically", " horizontally"))) {
            xx = " like queen";
        } else if (collect.containsAll(Arrays.asList(" diagonally forward", " vertically forward"))) {
            xx = " diagonally or vertically forward";
        } else if (collect.containsAll(Arrays.asList(" diagonally backwards", " vertically backwards"))) {
            xx = " diagonally or vertically backwards";
        } else {
            xx = collect.stream().collect(Collectors.joining(" or "));
        }
        builder.append(xx);

        return builder.toString();

    }

    private static String getCapturingIfPresent(Set<Operator> operators) {
        StringBuilder builder = new StringBuilder();
        if (hasInstance(operators, OnlyCapture.class)) {
            builder.append(" capturing");
        } else if (hasInstance(operators, WithoutCapture.class)) {
            builder.append(" without capturing");
        }
        return builder.toString();
    }

    private static String getTimesIfPresent(Set<Operator> operators) {
        StringBuilder builder = new StringBuilder();
        if (hasInstance(operators, MaxTimes.class)) {
            final MaxTimes times = getInstance(operators, MaxTimes.class);
            builder.append(" max ").append(times.getX()).append(" times");
        } else if (hasInstance(operators, MinTimes.class)) {
            final MinTimes times = getInstance(operators, MinTimes.class);
            builder.append(" minimum ").append(times.getX()).append(" times");
        } else if (hasInstance(operators, ExactlyTimes.class)) {
            final ExactlyTimes times = getInstance(operators, ExactlyTimes.class);
            builder.append(" ").append(times.getX()).append(" times");
        }
        return builder.toString();
    }

    private static String describeLeaper(Set<XYLeaper> leaper, Set<Operator> operators) {
        StringBuilder builder = new StringBuilder();

        if (hasInstance(operators, OnlyCapture.class)) {
            builder.append(" Captures");
        } else if (hasInstance(operators, WithoutCapture.class)
                && !hasInstance(operators, OverOwnPieceInstead.class)
                ) {
            builder.append(" Moves");
        } else if (hasInstance(operators, OverOwnPieceInstead.class)) {
            builder.append(" Captures self");
            if (!hasInstance(operators, WithoutCapture.class)) {
                builder.append(" or opponent");
            }
        } else {
            builder.append(" Leaps");
        }

        builder.append(describeLeapXY(leaper, operators));

        return builder.toString();
    }

    private static String describeLeapXY(Set<XYLeaper> leaper, Set<Operator> operators) {
        StringBuilder builder = new StringBuilder();
        final Set<Pair<Integer, Integer>> xys = leaper.stream().map(PieceClass::getXy).collect(Collectors.toSet());

        List<String> ors = new ArrayList<>();
        ors.add(tryValueOfXys(xys, 1, operators));
        ors.add(tryValueOfXys(xys, 2, operators));
        ors.add(tryValueOfXys(xys, 3, operators));
        ors.add(tryValueOfXys(xys, 4, operators));

        xys.forEach(xy -> {
            String xxx = "";
            if (hasInstance(operators, Forward.class)) {
                xxx += " forward";
            } else if (hasInstance(operators, Backwards.class)) {
                xxx += " backwards";
            }

            xxx += " over vector " + xy.toString();
            ors.add(xxx);
        });

        builder.append(ors.stream().filter(StringUtils::isNotBlank)
                .collect(Collectors.joining(" or ")));

        return builder.toString();
    }

    private static String tryValueOfXys(Set<Pair<Integer, Integer>> xys, int v, Set<Operator> operators) {

        StringBuilder builder = new StringBuilder();
        boolean valid = false;

        builder.append(" ").append(v);
        if (hasInstance(operators, Forward.class)) {
            builder.append(" forward");
        } else if (hasInstance(operators, Backwards.class)) {
            builder.append(" backwards");
        }

        final List<Pair<Integer, Integer>> all = Arrays.asList(Pair.of(0, v), Pair.of(v, 0), Pair.of(v, v));
        if (xys.containsAll(all)) {
            builder.append(" in every direction");
            xys.removeAll(all);
            valid = true;
        } else {
            boolean addOr = false;
            if (xys.contains(Pair.of(0, v))) {
                addOr = true;
                builder.append(" vertically");
                xys.remove(Pair.of(0, v));
                valid = true;
            }
            if (xys.contains(Pair.of(v, 0))) {
                if (addOr) {
                    builder.append(" or");
                }
                addOr = true;
                builder.append(" horizontally");
                xys.remove(Pair.of(v, 0));
                valid = true;

            }

            if (xys.contains(Pair.of(v, v))) {
                if (addOr) {
                    builder.append(" or");
                }
                builder.append(" diagonally");
                xys.remove(Pair.of(v, v));
                valid = true;

            }
        }

        if (valid) {
            return builder.toString();
        }
        return "";
    }

    private static boolean hasInstance(Set<Operator> operators, Class<? extends Operator> clazz) {
        return operators.stream().anyMatch(o -> o.getClass().equals(clazz));
    }

    private static <T extends Operator> T getInstance(Set<Operator> operators, Class<T> clazz) {
        return operators.stream().filter(o -> o.getClass().equals(clazz)).findAny().map(o -> (T) o).get();
    }


}
