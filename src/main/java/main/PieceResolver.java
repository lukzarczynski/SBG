package main;

import main.operator.Operator;
import main.piececlass.PieceCache;
import main.piececlass.PieceClass;
import main.piececlass.XYLeaper;
import main.piececlass.XYRider;
import main.resolvers.*;
import main.tree.ResolveResult;
import main.tree.Resolvers;
import org.apache.commons.lang3.tuple.Pair;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Created by lukasz on 25.12.16.
 */
public class PieceResolver {

    public static Pair<String, Integer> resolve(Piece piece) throws PieceResolverException {

        long start = System.currentTimeMillis();

        Set<OneMove> movesToInterpret = new HashSet<>(piece.getMoves());
        Map<OneMove, List<Resolver>> resultMap = new HashMap<>();


        List<OneMove> collect = piece.getMoves().stream()
                .sorted((m1, m2) -> Integer.compare(m2.getMoves().size(), m1.getMoves().size()))
                .collect(Collectors.toList());


        for (OneMove om : collect) {
            checkTimeout(start);

            if (resultMap.containsKey(om)) {
                continue;
            }

            Optional<Move> first = om.getMoves().stream().findFirst();
            if (!first.isPresent()) {
                continue;
            }

            XYLeaper xyLeaper = PieceCache
                    .getLeaper(Pair.of(Math.abs(first.get().getDx()), Math.abs(first.get().getDy())));
            XYRider xyRider = PieceCache
                    .getRider(Pair.of(Math.abs(first.get().getDx()), Math.abs(first.get().getDy())));


            for (Set<Operator> operators : Resolvers.getSortedOps()) {

                checkTimeout(start);
                if (tryPieceClass(piece, movesToInterpret, om, xyLeaper, operators, resultMap)) {
                    break;
                } else if (tryPieceClass(piece, movesToInterpret, om, xyRider, operators, resultMap)) {
                    break;
                } else if (tryCompositeClass(piece, movesToInterpret, om, xyLeaper, operators, resultMap)) {
                    break;
                } else if (tryCompositeClass(piece, movesToInterpret, om, xyRider, operators, resultMap)) {
                    break;
                }

            }

            throw new PieceResolverException("could not find description for piece " + om.toString());
        }


        if (resultMap.size() != piece.getMoves().size()) {
            throw new PieceResolverException("not all parsed");
        }
        return SetCover.getResult(resultMap);
//
//
//        return resultMap.entrySet().stream().map(e ->
//                String.format("m: %s, r: %s", e.getKey().toString(),
//                        e.getValue().stream().map(Resolver::getResult)
//                                .collect(Collectors.joining(" | ")))).collect(Collectors.joining("\n"));

    }

    private static void checkTimeout(long start) throws PieceResolverException {
        if (System.currentTimeMillis() - start > 5000) {
            System.out.println("TIMEOUT");
            throw new PieceResolverException("tIMEOUT");
        }
    }

    private static boolean tryCompositeClass(Piece piece, Set<OneMove> movesToInterpret,
                                             OneMove om, PieceClass xyLeaper,
                                             Set<Operator> operators,
                                             Map<OneMove, List<Resolver>> resultMap) {
        SimplePieceResolver firstResolver = new SimplePieceResolver(xyLeaper, operators);

        if (firstResolver.isApplicableForPrefixes(piece.getMoves())
                && firstResolver.containsMovePrefix(om)) {
            PrefixResolveResult prefixResolveResult = firstResolver.applyForPrefixes(piece.getMoves());

            Optional<Pair<Set<Operator>, Optional<SimplePieceResolver>>> best2 =
                    Resolvers.getSortedOps().stream()
                            .map(ops -> {
                                Optional<SimplePieceResolver> first =
                                        SimplePieceResolverSearcher.search(prefixResolveResult.getSuffixes(), ops)
                                                .stream()
                                                .sorted(Comparator.comparingInt(SimplePieceResolver::getValue))
                                                .findFirst();

                                return Pair.of(ops, first);

                            })
                            .filter(pair -> pair.getRight().isPresent())
                            .filter(pair -> {

                                SimpleCompositResolver comRes = new SimpleCompositResolver(
                                        firstResolver,
                                        pair.getRight().get());

                                ResolveResult apply = comRes.apply(piece.getMoves());

                                return apply.getParsed().contains(om);
                            })
                            .findFirst();

            if (best2.isPresent()) {
                SimpleCompositResolver comRes = new SimpleCompositResolver(
                        firstResolver,
                        best2.get().getRight().get());

                ResolveResult apply = comRes.apply(piece.getMoves());

                if (!apply.getParsed().contains(om)) {
                    return false;
                }

                apply.getParsed().forEach(ooo -> {
                    resultMap.putIfAbsent(ooo, new ArrayList<>());
                    resultMap.get(ooo).add(comRes);
                });
                movesToInterpret.removeAll(apply.getParsed());

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
                                         Map<OneMove, List<Resolver>> resultMap) {
        SimplePieceResolver resolver = new SimplePieceResolver(xyLeaper, operators);

        if (resolver.isApplicable(piece.getMoves()) && resolver.containsMove(om)) {
            ResolveResult apply = resolver.apply(piece.getMoves());
            apply.getParsed().forEach(pm -> {
                resultMap.putIfAbsent(pm, new ArrayList<>());
                resultMap.get(pm).add(resolver);
            });
            movesToInterpret.removeAll(apply.getParsed());
            return true;
        }
        return false;
    }

}


