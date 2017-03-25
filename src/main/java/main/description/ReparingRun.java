package main.description;

import main.SuperResolver;
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

    public static String getRepairedDescription(List<Resolver> result) {

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

        List<SuperResolver> superResolvers =
                result.stream().filter(r -> r instanceof SuperResolver)
                        .map(r -> (SuperResolver) r)
                        .collect(Collectors.toList());

        description.append(handleSimpleResolvers(collect));
        description.append(handleCompositeResolvers(compositeResolvers));

        final Map<Integer, List<SuperResolver>> bySize
                = superResolvers.stream().collect(Collectors.groupingBy(r -> r.getResolvers().size()));
        bySize.forEach((s, resolvers) -> {
            if (s == 1) {
                description.append(handleSimpleResolvers(resolvers.stream()
                        .map(SuperResolver::getResolvers)
                        .map(l -> l.get(0))
                        .collect(Collectors.toList())));

            } else {
                description.append(handleSuperResolvers(resolvers, s));
            }
        });

        List<SpecialCaseResolver> specialCaseResolvers =
                result.stream().filter(r -> r instanceof SpecialCaseResolver)
                        .map(r -> (SpecialCaseResolver) r)
                        .collect(Collectors.toList());

        description.append("\n");

        description.append(specialCaseResolvers.stream().map(Resolver::getDescription).collect(Collectors.joining(" || ")));

        return description.toString();
    }

    private static String handleSuperResolvers(List<SuperResolver> resolvers, int size) {
        StringBuilder builder = new StringBuilder();

        Map<String, List<SimplePieceResolver>> collect = new HashMap<>();

        resolvers.forEach(r -> {
            List<String> parts = new ArrayList<>();
            for (int i = 0; i < size - 1; i++) {
                final SimplePieceResolver spr = r.getResolvers().get(i);
                final String part =
                        handleSimpleResolver(spr.getOperators(), Collections.singletonList(spr));
                parts.add(part);
            }
            final String prefix = parts.stream().collect(Collectors.joining(" and then "));
            collect.putIfAbsent(prefix, new ArrayList<>());
            collect.get(prefix).add(r.getResolvers().get(size - 1));
        });

        collect.forEach((firstPart, Part) -> {

            builder.append("\n - ")
                    .append(firstPart).append(" and then ")
                    .append(handleSimpleResolvers(Part.stream()
                            .collect(Collectors.groupingBy(SimplePieceResolver::getOperators)))
                            .replaceAll("\n - ", ""));
        });

        return builder.toString();
    }

    private static String handleSimpleResolvers(Collection<SimplePieceResolver> resolvers) {

        final Map<Set<Operator>, List<SimplePieceResolver>> collect
                = resolvers.stream().collect(Collectors.groupingBy(SimplePieceResolver::getOperators));

        return handleSimpleResolvers(collect);
    }

    private static String handleSimpleResolvers(Map<Set<Operator>, List<SimplePieceResolver>> collect) {
        final StringBuilder builder = new StringBuilder();
        collect.forEach((ops, resolvers) -> builder.append(handleSimpleResolver(ops, resolvers)));
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

        String res = "";
        if (!riders.isEmpty()) {
            res = res + "\n - " + describeRiders(riders.stream()
                    .map(r -> (XYRider) r.getPieceClass())
                    .collect(Collectors.toSet()), ops);
        }
        if (!leapers.isEmpty()) {
            res = res + "\n - " + describeLeaper(leapers.stream()
                    .map(r -> (XYLeaper) r.getPieceClass())
                    .collect(Collectors.toSet()), ops);
        }

        return res;
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
                    .append(handleSimpleResolvers(Part.stream()
                            .collect(Collectors.groupingBy(SimplePieceResolver::getOperators)))
                            .replaceAll("\n - ", ""));
        });

        return builder.toString();

    }

    private static String describeRiders(Set<XYRider> rider, Set<Operator> operators) {
        StringBuilder builder = new StringBuilder();
        builder.append("Rides");

        builder.append(getTimesIfPresent(operators));
        builder.append(getCapturingIfPresent(operators));


        final Map<Pair<Integer, Integer>, List<Pair<Integer, Integer>>> collectXys = new HashMap<>();


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
                collectXys.putIfAbsent(normalizePair(r.getXy()), new ArrayList<>());
                collectXys.get(normalizePair(r.getXy())).add(r.getXy());
                return "";
            }
        })
                .filter(StringUtils::isNotBlank)
                .collect(Collectors.toList());


        List<String> vectors = new ArrayList<>();
        collectXys.forEach((k, v) -> {
            if (v.size() > 2) {
                System.out.println("??????????????????????????????????");
            }
            if (v.size() == 2) {
                vectors.add(String.format("%s/%s", v.get(0).toString(), v.get(1).toString()));
            } else {
                vectors.add(v.get(0).toString());
            }
        });

        if (!vectors.isEmpty()) {
            collect.add(" over vector " + vectors.stream().collect(Collectors.joining(" or ")));
        }

        String xc = "";
        if (hasInstance(operators, Outwards.class)) {
            xc += " outwards";
        } else if (hasInstance(operators, OutwardsX.class)) {
            xc += " horizontally outwards";
        } else if (hasInstance(operators, OutwardsY.class)) {
            xc += " vertically outwards";
        }
        String xx = "";

        if (StringUtils.isNotBlank(xc)) {
            xx += xc;
        }
        if (collect.containsAll(Arrays.asList(" diagonally", " vertically", " horizontally"))) {
            xx += " in every direction";
        } else if (collect.containsAll(Arrays.asList(" diagonally forward", " vertically forward"))) {
            xx += " diagonally or vertically forward";
        } else if (collect.containsAll(Arrays.asList(" diagonally backwards", " vertically backwards"))) {
            xx += " diagonally or vertically backwards";
        } else {
            xx += collect.stream().collect(Collectors.joining(" or "));
        }

        builder.append(xx);

        return builder.toString();

    }

    private static String getCapturingIfPresent(Set<Operator> operators) {
        StringBuilder builder = new StringBuilder();


        final boolean onlyCapture = hasInstance(operators, OnlyCapture.class);
        final boolean withoutCapture = hasInstance(operators, WithoutCapture.class);
        final boolean overOwn = hasInstance(operators, OverOwnPieceInstead.class);
        final boolean overEnemy = hasInstance(operators, OverEnemyPieceInstead.class);
        final boolean overOwnEndingNormal = hasInstance(operators, OverOwnPieceInsteadEndingNormally.class);
        final boolean overEnemyEndingNormal = hasInstance(operators, OverEnemyPieceInsteadEndingNormally.class);
        final boolean withOneOwnPiece = hasInstance(operators, WithOneOwnPiece.class);
        final boolean withOneEnemyPiece = hasInstance(operators, WithOneEnemyPiece.class);

        if (onlyCapture && !(overOwnEndingNormal || overEnemyEndingNormal || overOwn || overEnemy)) {
            builder.append(" capturing");
        } else if (onlyCapture && (overOwn || overOwnEndingNormal)) {
            builder.append(" capturing but riding only over own pieces");
        } else if (onlyCapture && (overEnemy || overEnemyEndingNormal)) {
            builder.append(" capturing but riding only over opponent pieces");
        } else if (withoutCapture && !(overOwnEndingNormal || overEnemyEndingNormal || overOwn || overEnemy)) {
            builder.append(" without capturing");
        } else if (withoutCapture && overOwn) {
            builder.append(" only over own pieces with self capture");
        } else if (withoutCapture && overOwnEndingNormal) {
            builder.append(" only over own pieces capturing self or staying on empty");
        } else if (withoutCapture && overEnemy) {
            builder.append(" only over opponent pieces with capturing");
        } else if (withoutCapture && overEnemyEndingNormal) {
            builder.append(" only over opponent pieces capturing or staying on empty");
        } else if (overOwn) {
            builder.append(" only over own pieces and capturing self or opponent");
        } else if (overOwnEndingNormal) {
            builder.append(" only over own pieces and capturing opponent or staying on empty");
        } else if (overEnemy) {
            builder.append(" only over opponent pieces capturing ");
        } else if (overEnemyEndingNormal) {
            builder.append(" only over opponent pieces and capturing opponent or staying on empty");
        }

        if (withOneEnemyPiece && withOneOwnPiece) {
            builder.append(" with one enemy and own piece on the way");
        } else if (withOneEnemyPiece) {
            builder.append(" with one enemy piece on the way");
        } else if (withOneOwnPiece) {
            builder.append(" with one own piece on the way");
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


        String xc = "";
        if (hasInstance(operators, Outwards.class)) {
            xc += " outwards";
        } else if (hasInstance(operators, OutwardsX.class)) {
            xc += " horizontally outwards";
        } else if (hasInstance(operators, OutwardsY.class)) {
            xc += " vertically outwards";
        }
        builder.append(xc);
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

        if (!xys.isEmpty()) {
            StringBuilder xxx = new StringBuilder();
            if (hasInstance(operators, Forward.class)) {
                xxx.append(" forward");
            } else if (hasInstance(operators, Backwards.class)) {
                xxx.append(" backwards");
            }

            xxx.append(" over vector ");

            final Map<Pair<Integer, Integer>, List<Pair<Integer, Integer>>> collect
                    = xys.stream().collect(Collectors.groupingBy(ReparingRun::normalizePair));

            List<String> vectors = new ArrayList<>();
            collect.forEach((k, v) -> {
                if (v.size() > 2) {
                    System.out.println("??????????????????????????????????");
                }
                if (v.size() == 2) {
                    vectors.add(String.format("%s/%s", v.get(0).toString(), v.get(1).toString()));
                } else {
                    vectors.add(v.get(0).toString());
                }
            });

            xxx.append(vectors.stream().collect(Collectors.joining(" or ")));

            ors.add(xxx.toString());
        }

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

    private static Pair<Integer, Integer> normalizePair(Pair<Integer, Integer> xy) {
        if (xy.getKey() > xy.getValue()) {
            return xy;
        }
        return Pair.of(xy.getValue(), xy.getKey());
    }


}
