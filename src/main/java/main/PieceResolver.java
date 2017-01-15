package main;

import main.operator.Operator;
import main.piececlass.*;
import main.resolvers.*;
import org.apache.commons.lang3.tuple.Pair;

import java.util.*;
import java.util.stream.Collectors;

import static java.util.Objects.isNull;

enum PieceResolveType {

    SIMPLE,
    COMPOSIT,
    OTHER;

    public static PieceResolveType forPiece(OneMove om) {
        int types = 0;
        Pair<Integer, Integer> currPair = Pair.of(0, 0);

        for (Move m : om.getMoves()) {
            Pair<Integer, Integer> of = Pair.of(m.getDx(), m.getDy());
            if (!currPair.equals(of)) {
                types++;
            }
            currPair = of;
        }
        switch (types) {
            case 1:
                return SIMPLE;
            case 2:
                return COMPOSIT;
            default:
                return OTHER;
        }


    }


}

/**
 * Created by lukasz on 25.12.16.
 */
public class PieceResolver {

    public static Pair<String, Integer> resolve(Piece piece, Pair<Integer, Integer> xy) throws PieceResolverException {

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
                    .getLeaper(
                            Pair.of(Math.abs(first.get().getDx()), Math.abs(first.get().getDy())));
            XYYXLeaper xyyxLeaper = PieceCache
                    .getXYYXLeaper(Pair.of(Math.abs(first.get().getDx()), Math.abs(first.get().getDy())));
            XYRider xyRider = PieceCache
                    .getRider(Pair.of(Math.abs(first.get().getDx()), Math.abs(first.get().getDy())));


            PieceResolveType type = PieceResolveType.forPiece(om);


            for (Set<Operator> operators : Resolvers.getSortedOps()) {

                checkTimeout(start);

                if (type.equals(PieceResolveType.OTHER)) {
                    throw new PieceResolverException("Not implemented yet: " + om.toString());
                } else if (type.equals(PieceResolveType.SIMPLE)) {
                    if (tryPieceClass(piece, movesToInterpret, om, xyLeaper, operators, xy, resultMap)) {
                        break;
                    } else if (tryPieceClass(piece, movesToInterpret, om, xyyxLeaper, operators, xy, resultMap)) {
                        break;
                    } else if (tryPieceClass(piece, movesToInterpret, om, xyRider, operators, xy, resultMap)) {
                        break;
                    }
                } else {
                    if (tryCompositeClass(piece, movesToInterpret, om, xyLeaper, operators, xy, resultMap)) {
                        break;
                    } else if (tryCompositeClass(piece, movesToInterpret, om, xyyxLeaper, operators, xy, resultMap)) {
                        break;
                    } else if (tryCompositeClass(piece, movesToInterpret, om, xyRider, operators, xy, resultMap)) {
                        break;
                    }

                }
            }
            if (movesToInterpret.contains(om)) {
                throw new PieceResolverException("could not find description for piece " + om.toString());
            }

        }


        if (resultMap.size() != piece.getMoves().size()) {
            throw new PieceResolverException("not all parsed");
        }
        return SetCover.getResult(resultMap);

    }

    private static void checkTimeout(long start) throws PieceResolverException {
        if (System.currentTimeMillis() - start > 10000) {
            throw new PieceResolverException("TIMEOUT");
        }
    }

    private static boolean tryCompositeClass(Piece piece,
                                             Set<OneMove> movesToInterpret,
                                             OneMove om,
                                             PieceClass pieceClass,
                                             Set<Operator> operators,
                                             Pair<Integer, Integer> xy,
                                             Map<OneMove, List<Resolver>> resultMap) {
        SimplePieceResolver firstResolver = new SimplePieceResolver(pieceClass, operators, xy);

        if (firstResolver.isApplicableForPrefixes(piece.getMoves(), xy)
                && firstResolver.containsMovePrefix(om, xy)) {
            PrefixResolveResult prefixResolveResult = firstResolver.applyForPrefixes(piece.getMoves(), xy);

            OneMove omSuffix = prefixResolveResult.getMove().get(om);
            Move first = omSuffix.getMoves().stream().findFirst().get();
            Move firstOM = om.getMoves().stream().findFirst().get();


            XYLeaper xyLeaper = PieceCache
                    .getLeaper(Pair.of(Math.abs(first.getDx()), Math.abs(first.getDy())));
            XYYXLeaper xyyxLeaper = PieceCache
                    .getXYYXLeaper(Pair.of(Math.abs(first.getDx()), Math.abs(first.getDy())));
            XYRider xyRider = PieceCache
                    .getRider(Pair.of(Math.abs(first.getDx()), Math.abs(first.getDy())));

            Pair<Integer,Integer> newXY = Pair.of(xy.getLeft() - Math.abs(firstOM.getDx()), xy.getRight() - Math.abs(firstOM.getDy()));

            for (Set<Operator> suffixOps : Resolvers.getSortedOps()) {
                List<SimplePieceResolver> asd = Arrays.asList(
                        new SimplePieceResolver(xyRider, suffixOps, newXY),
                        new SimplePieceResolver(xyyxLeaper, suffixOps, newXY),
                        new SimplePieceResolver(xyLeaper, suffixOps, newXY)
                );

                SimplePieceResolver spr = asd.stream()
                        .filter(r -> r.isApplicable(prefixResolveResult.getSuffixes(), newXY))
                        .findFirst()
                        .orElse(null);
                if (isNull(spr)) {
                    continue;
                }

                SimpleCompositResolver comRes = new SimpleCompositResolver(firstResolver, spr);

                ResolveResult apply = comRes.apply(piece.getMoves(), xy);
                if (!apply.getParsed().contains(om)) {
                    continue;
                } else {
                    apply.getParsed().forEach(ooo -> {
                        resultMap.putIfAbsent(ooo, new ArrayList<>());
                        resultMap.get(ooo).add(comRes);
                    });
                    movesToInterpret.removeAll(apply.getParsed());

                    return true;
                }
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
        SimplePieceResolver resolver = new SimplePieceResolver(xyLeaper, operators, xy);

        if (resolver.isApplicable(piece.getMoves(), xy) && resolver.containsMove(om, xy)) {
            ResolveResult apply = resolver.apply(piece.getMoves(), xy);
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


