package main.piececlass;

import main.Move;
import main.OneMove;
import main.Piece;
import main.operator.Operator;
import org.apache.commons.lang3.tuple.Pair;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Created by lukza on 28.12.2016.
 */
public class XYClassSearcher {

    public static Set<XYLeaper> findLeapers(Set<OneMove> moves, Set<Operator> operators) {
        return getCandidates(moves).stream()
                .map(PieceCache::getLeaper)
                .filter(r -> r.matches(moves, operators))
                .collect(Collectors.toSet());
    }

    public static Set<XYRider> findRiders(Set<OneMove> moves, Set<Operator> operators) {
        return getCandidates(moves).stream()
                .map(PieceCache::getRider)
                .filter(r -> r.matches(moves, operators))
                .collect(Collectors.toSet());
    }

    public static Set<Pair<Integer, Integer>> getCandidates(Set<OneMove> moves) {
        List<Move> firstMoves = moves.stream()
                .filter(om  -> !om.getMoves().isEmpty())
                .map(om -> om.getMoves().get(0)).collect(Collectors.toList());

        return firstMoves.stream().map(m -> Pair.of(Math.abs(m.getDx()), Math.abs(m.getDy())))
                .collect(Collectors.toSet());
    }

    public static Set<XYLeaper> findLeapersForPrefix(Set<OneMove> moves, Set<Operator> operators) {
        return getCandidates(moves).stream()
                .map(PieceCache::getLeaper)
                .filter(r -> r.matchesPrefix(moves, operators))
                .collect(Collectors.toSet());
    }

    public static Set<XYRider> findRidersForPrefix(Set<OneMove> moves,  Set<Operator> operators) {
        return getCandidates(moves).stream()
                .map(PieceCache::getRider)
                .filter(r -> r.matchesPrefix(moves, operators))
                .collect(Collectors.toSet());
    }
}
