package main;

import main.model.Move;
import main.model.OneMove;
import main.model.Piece;
import main.operator.Operator;
import main.operator.Outwards;
import main.operator.OutwardsX;
import main.operator.OutwardsY;
import main.piececlass.*;
import main.resolvers.Resolver;
import main.resolvers.Resolvers;
import main.resolvers.SimplePieceResolver;
import main.resolvers.SpecialCaseResolver;
import org.apache.commons.lang3.tuple.Pair;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Created by lukasz on 25.12.16.
 */
public class PieceResolver {

    public static final Set<OneMove> failedMoves = new HashSet<>();

    public static Pair<String, Integer> resolve(Piece piece, Pair<Integer, Integer> xy) {

        long start = System.currentTimeMillis();

        piece.getMoves().forEach(OneMove::initializeParts);

        Set<OneMove> movesToInterpret = new HashSet<>(piece.getMoves());
        Map<OneMove, List<Resolver>> resultMap = new HashMap<>();


        final List<OneMove> sortedMoves = piece.getMoves().stream()
                .filter(om -> !om.getMoves().isEmpty())
                .sorted((m1, m2) -> Integer.compare(m2.getMoves().size(), m1.getMoves().size()))
                .collect(Collectors.toList());


        for (OneMove om : sortedMoves) {
            if (checkTimeout(start, ParamsAndEvaluators.PIECE_TIMEOUT_MS)) {
//                break;
            }

            if (resultMap.containsKey(om)) {
                //move already resolved
                continue;
            }

//
//            PieceResolveType type = PieceResolveType.forPiece(om);
//
//            if (type.equals(PieceResolveType.OTHER)) {
//                trySpecialCase(movesToInterpret, om, resultMap);
//            } else {
                tryMultipleBent(movesToInterpret, om, xy, resultMap);
//            }

            if (!resultMap.containsKey(om)) {
//                System.out.println("Could not resolve " + om.toString() + ", trying special case");
                PieceResolver.failedMoves.add(om);
                start = System.currentTimeMillis();
                trySpecialCase(movesToInterpret, om, resultMap);
            }

        }
        return SetCover.getResult(resultMap, xy);

    }


    private static boolean tryMultipleBent(Set<OneMove> moves,
                                           OneMove om,
                                           Pair<Integer, Integer> size,
                                           Map<OneMove, List<Resolver>> resultMap) {
        final HashMap<OneMove, Set<OneMove>> byPrefix = new HashMap<>();
        byPrefix.put(OneMove.EMPTY_MOVE, moves.stream().filter(m -> m.getParts().size() == om.getParts().size()).collect(Collectors.toSet()));
        return tryMultipleBent(om, 0, size, new ArrayList<>(), resultMap, byPrefix);

    }

    private static boolean tryMultipleBent(OneMove om,
                                           int part,
                                           Pair<Integer, Integer> size,
                                           List<SimplePieceResolver> resolvers,
                                           Map<OneMove, List<Resolver>> resultMap,
                                           Map<OneMove, Set<OneMove>> byPrefix) {

        final OneMove omPart = om.getParts().get(part);
        final Move first = omPart.getFirst().get();

        final XYLeaper xyLeaper = PieceCache.getLeaper(first.getXY());
        final XYYXLeaper xyyxLeaper = PieceCache.getXYYXLeaper(first.getXY());
        final XYRider xyRider = PieceCache.getRider(first.getXY());

        final List<PieceClass> classes = new ArrayList<>();
        classes.add(xyLeaper);
        if (xyyxLeaper.isValid()) {
            classes.add(xyyxLeaper);
        }
        classes.add(xyRider);
        List<Operator> additional = new ArrayList<>();
        if (part > 0) {
            additional = Arrays.asList(new Outwards(), new OutwardsY(), new OutwardsX());
        }

        for (Set<Operator> ops : Resolvers.getSortedOps()) {
            final List<SimplePieceResolver> res = new ArrayList<>();
            classes.forEach(c -> res.add(new SimplePieceResolver(c, ops)));
            if (part > 0) {
                additional.forEach(add ->
                        classes.forEach(c -> res.add(new SimplePieceResolver(c, Utils.sum(ops, add)))));
            }

            for (SimplePieceResolver r : res) {
                if (!r.isValid()) {
                    continue;
                }

                if (byPrefix.values().stream().allMatch(moves -> r.isValidForPart(moves, size, part))) {

                    final Map<OneMove, Set<OneMove>> groupedBYPrefix = byPrefix.values().stream()
                            .map(value -> r.filterForPart(value, part, size))
                            .flatMap(Collection::stream)
                            .collect(Collectors.groupingBy(oo -> {
                                List<Move> toJoin = new ArrayList<>();
                                for (int i = 0; i <= part; i++) {
                                    toJoin.addAll(oo.getParts().get(i).getMoves());
                                }
                                return OneMove.of(toJoin);
                            }, Collectors.toSet()));

                    if (groupedBYPrefix.values().stream().noneMatch(l -> l.contains(om))) {
                        continue;
                    }

                    if (om.getParts().size() - 1 == part) {
                        groupedBYPrefix.values().stream().flatMap(Collection::stream)
                                .distinct()
                                .forEach(o -> {
                                    resultMap.putIfAbsent(o, new ArrayList<>());
                                    resultMap.get(o).add(new SuperResolver(resolvers, r));
                                });
                        return true;
                    } else {
                        List<SimplePieceResolver> asd = new ArrayList<>(resolvers);
                        asd.add(r);
                        final boolean b = tryMultipleBent(om, part + 1, size,
                                asd,
                                resultMap,
                                groupedBYPrefix
                        );
                        if (b) {
                            return true;
                        }
                    }
                }
            }
        }
        return false;
    }


    private static boolean trySpecialCase(Set<OneMove> movesToInterpret,
                                          OneMove om,
                                          Map<OneMove, List<Resolver>> resultMap) {

        SpecialCaseResolver resolver = new SpecialCaseResolver(om);
        resultMap.putIfAbsent(om, new ArrayList<>());
        resultMap.get(om).add(resolver);
        movesToInterpret.remove(om);
        return true;
    }

    private static boolean checkTimeout(long start, int max) {
        if (System.currentTimeMillis() - start > max) {
            System.out.println("TIMEOUT");
            return true;
        }
        return false;
    }

}


