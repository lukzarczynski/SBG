package main;

import main.model.Move;
import main.model.OneMove;
import main.model.Piece;
import main.operator.*;
import main.piececlass.*;
import main.resolvers.*;
import org.apache.commons.lang3.tuple.Pair;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.util.Objects.isNull;

/**
 * Created by lukasz on 25.12.16.
 */
public class PieceResolver {

    public static final Set<OneMove> failedMoves = new HashSet<>();
    public static int comp = 0;
    public static int simpl = 0;

    public static Pair<String, Integer> resolve(Piece piece, Pair<Integer, Integer> xy) {

        long start = System.currentTimeMillis();

        Set<OneMove> movesToInterpret = new HashSet<>(piece.getMoves());
        Map<OneMove, List<Resolver>> resultMap = new HashMap<>();


        final List<OneMove> sortedMoves = piece.getMoves().stream()
                .filter(om -> !om.getMoves().isEmpty())
                .sorted((m1, m2) -> Integer.compare(m2.getMoves().size(), m1.getMoves().size()))
                .collect(Collectors.toList());


        final Map<OneMove, OneMove> prefixesOnly = getPrefixes(piece);

        for (OneMove om : sortedMoves) {
            if (checkTimeout(start, ParamsAndEvaluators.PIECE_TIMEOUT_MS)) {
                break;
            }

            if (resultMap.containsKey(om)) {
                //move already resolved
                continue;
            }

            Move first = om.getFirst().get();

            final XYLeaper xyLeaper = PieceCache.getLeaper(first.getXY());
            final XYYXLeaper xyyxLeaper = PieceCache.getXYYXLeaper(first.getXY());
            final XYRider xyRider = PieceCache.getRider(first.getXY());

            PieceResolveType type = PieceResolveType.forPiece(om);

            for (Set<Operator> operators : Resolvers.getSortedOps()) {

                if (checkTimeout(start, ParamsAndEvaluators.MOVE_TIMEOUT_MS)) {
                    break;
                }

                if (type.equals(PieceResolveType.OTHER)) {
                    trySpecialCase(movesToInterpret, om, resultMap);
                } else if (type.equals(PieceResolveType.SIMPLE)) {
                    if (tryPieceClass(piece, movesToInterpret, om, xyLeaper, operators, xy, resultMap)) {
                        break;
                    } else if (xyyxLeaper.isValid() && tryPieceClass(piece, movesToInterpret, om, xyyxLeaper, operators, xy, resultMap)) {
                        break;
                    } else if (tryPieceClass(piece, movesToInterpret, om, xyRider, operators, xy, resultMap)) {
                        break;
                    }
                } else {
                    if (tryCompositeClass(movesToInterpret, om, xyLeaper, operators, xy, resultMap, prefixesOnly)) {
                        break;
                    } else if (xyyxLeaper.isValid()
                            && tryCompositeClass(movesToInterpret, om, xyyxLeaper, operators, xy, resultMap, prefixesOnly)) {
                        break;
                    } else if (tryCompositeClass(movesToInterpret, om, xyRider, operators, xy, resultMap, prefixesOnly)) {
                        break;
                    }
                }
            }
            if (movesToInterpret.contains(om)) {
                System.out.println("Could not resolve " + om.toString() + ", trying special case");
                PieceResolver.failedMoves.add(om);
                start = System.currentTimeMillis();
                trySpecialCase(movesToInterpret, om, resultMap);
            }

        }
        return SetCover.getResult(resultMap);

    }

    /**
     * (1,1,e)(2,2,p) -> <br/>
     * <p>
     * key: (1,1,e)(2,2,p)<br/>
     * value: (1,1,e)
     *
     * @param piece
     * @return map
     */
    private static Map<OneMove, OneMove> getPrefixes(Piece piece) {
        final Map<OneMove, OneMove> prefixesOnly = new HashMap<>();
        for (OneMove om : piece.getMoves()) {
            List<Move> moves = new ArrayList<>();
            Move first = null;
            boolean valid = false;
            for (Move m : om.getMoves()) {
                if (isNull(first)) {
                    first = m;
                }

                if (!Objects.equals(m.getDx(), first.getDx()) || !Objects.equals(m.getDy(), first.getDy())) {
                    valid = true;
                    break;
                }
                moves.add(m);
            }

            if (!moves.isEmpty() && valid) {
                OneMove prefixMove = new OneMove();
                prefixMove.setMoves(moves);
                prefixesOnly.put(om, prefixMove);
            }
        }
        return prefixesOnly;
    }

    private static boolean tryCompositeClass(Set<OneMove> movesToInterpret,
                                             OneMove om,
                                             PieceClass pieceClass,
                                             Set<Operator> operators,
                                             Pair<Integer, Integer> xy,
                                             Map<OneMove, List<Resolver>> resultMap,
                                             Map<OneMove, OneMove> allPrefixesMap) {

        final SimplePieceResolver firstResolver = new SimplePieceResolver(pieceClass, operators);

        if (!firstResolver.isValid()) {
            return false;
        }

        comp++;
        final OneMove omPrefix = allPrefixesMap.get(om);
        final OneMove omWithoutPrefix = om.withoutPrefix(omPrefix);

        if (firstResolver.isValidFor(allPrefixesMap.values(), omPrefix, xy)) {

            final PrefixResolveResult prefixResolveResult = firstResolver.applyForPrefixes(allPrefixesMap, xy);

            final Move anyFirstMoveOfSuffix = omWithoutPrefix.getFirst().get();

            final XYLeaper xyLeaper = PieceCache.getLeaper(anyFirstMoveOfSuffix.getXY());
            final XYYXLeaper xyyxLeaper = PieceCache.getXYYXLeaper(anyFirstMoveOfSuffix.getXY());
            final XYRider xyRider = PieceCache.getRider(anyFirstMoveOfSuffix.getXY());

            final Set<Operator> outwardsSet = Utils.setOf(new Outwards());
            final Set<Operator> outwardXSet = Utils.setOf(new OutwardsX());
            final Set<Operator> outwardYSet = Utils.setOf(new OutwardsY());

            for (Set<Operator> suffixOps : Resolvers.getSortedOps()) {
                final List<SimplePieceResolver> listOfSuffixResolvers = Stream.of(
                        new SimplePieceResolver(xyyxLeaper, suffixOps),
                        new SimplePieceResolver(xyLeaper, suffixOps),
                        new SimplePieceResolver(xyRider, suffixOps),
                        new SimplePieceResolver(xyyxLeaper, Utils.sum(suffixOps, outwardsSet)),
                        new SimplePieceResolver(xyLeaper, Utils.sum(suffixOps, outwardsSet)),
                        new SimplePieceResolver(xyRider, Utils.sum(suffixOps, outwardsSet)),
                        new SimplePieceResolver(xyyxLeaper, Utils.sum(suffixOps, outwardXSet)),
                        new SimplePieceResolver(xyLeaper, Utils.sum(suffixOps, outwardXSet)),
                        new SimplePieceResolver(xyRider, Utils.sum(suffixOps, outwardXSet)),
                        new SimplePieceResolver(xyyxLeaper, Utils.sum(suffixOps, outwardYSet)),
                        new SimplePieceResolver(xyLeaper, Utils.sum(suffixOps, outwardYSet)),
                        new SimplePieceResolver(xyRider, Utils.sum(suffixOps, outwardYSet)))
                        .filter(SimplePieceResolver::isValid).collect(Collectors.toList());


                SimplePieceResolver tempSuffixResolver = null;
                for (SimplePieceResolver sufResolver : listOfSuffixResolvers) {

                    boolean valid = prefixResolveResult.getMap().entrySet().stream()
                            .allMatch(e -> {
                                final OneMove prefix = e.getKey();
                                final Set<OneMove> suffixes = e.getValue();
                                final Pair<Integer, Integer> prefixAsVector = Utils.asVector(prefix);
                                if (prefix.equals(omPrefix)) {
                                    return sufResolver.isValidForWithVector(suffixes, omWithoutPrefix, xy, prefixAsVector);
                                }
                                return sufResolver.isValidForWithVector(suffixes, xy, prefixAsVector);

                            });


                    if (valid) {
                        tempSuffixResolver = sufResolver;
                        break;
                    }
                }

                final SimplePieceResolver suffixResolver;
                if (isNull(tempSuffixResolver)) {
                    continue;
                } else {
                    suffixResolver = tempSuffixResolver;
                }

                final SimpleCompositeResolver comRes = new SimpleCompositeResolver(firstResolver, tempSuffixResolver);

                prefixResolveResult.getMap().forEach((prefix, suffixes) -> {
                    ResolveResult resolvedSuffixes = suffixResolver.resolveWithVector(suffixes,
                            xy,
                            Utils.asVector(prefix));

                    resolvedSuffixes.getParsed()
                            .forEach(suffix -> {
                                final OneMove resolvedMove = Utils.joinMoves(prefix, suffix);
                                resultMap.putIfAbsent(resolvedMove, new ArrayList<>());
                                resultMap.get(resolvedMove).add(comRes);
                                movesToInterpret.remove(resolvedMove);
                            });
                });

                return true;
            }
            return false;
        }
        return false;
    }

    private static boolean tryPieceClass(Piece piece,
                                         Set<OneMove> movesToInterpret,
                                         OneMove om,
                                         PieceClass xyLeaper,
                                         Set<Operator> operators,
                                         Pair<Integer, Integer> xy,
                                         Map<OneMove, List<Resolver>> resultMap) {
        SimplePieceResolver resolver = new SimplePieceResolver(xyLeaper, operators);

        if (!resolver.isValid()) {
            return false;
        }

        simpl++;
        if (resolver.isValidFor(piece.getMoves(), om, xy)) {
            final ResolveResult resolveResult = resolver.resolve(piece.getMoves(), xy);
            resolveResult.getParsed()
                    .forEach(pm -> {
                        resultMap.putIfAbsent(pm, new ArrayList<>());
                        resultMap.get(pm).add(resolver);
                    });
            movesToInterpret.removeAll(resolveResult.getParsed());
            return true;
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


