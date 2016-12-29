package main.piececlass;

import main.Move;
import main.OneMove;
import main.Piece;
import main.operator.Operator;
import org.apache.commons.lang3.tuple.Pair;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * Created by lukza on 28.12.2016.
 */
public class XYClassSearcher {

    private static final Map<Pair<Integer, Integer>, XYLeaper> leapersCache = new ConcurrentHashMap<>();
    private static final Map<Pair<Integer, Integer>, XYRider> ridersCache = new ConcurrentHashMap<>();

    public static Set<XYLeaper> findLeapers(Set<OneMove> moves, Piece piece, Set<Operator> operators) {
        return getCandidates(moves).stream()
                .map(XYClassSearcher::getLeaper)
                .filter(r -> r.matches(piece.getMoves(), operators))
                .collect(Collectors.toSet());
    }

    public static Set<XYRider> findRiders(Set<OneMove> moves, Piece piece, Set<Operator> operators) {
        return getCandidates(moves).stream()
                .map(XYClassSearcher::getRider)
                .filter(r -> r.matches(piece.getMoves(), operators))
                .collect(Collectors.toSet());
    }

    private static Set<Pair<Integer, Integer>> getCandidates(Set<OneMove> moves) {
        List<Move> firstMoves = moves.stream().map(om -> om.getMoves().get(0)).collect(Collectors.toList());

        return firstMoves.stream().map(m -> Pair.of(Math.abs(m.getDx()), Math.abs(m.getDy())))
                .collect(Collectors.toSet());
    }

    private static XYLeaper getLeaper(Pair<Integer, Integer> pair) {
        if (!leapersCache.containsKey(pair)) {
            XYLeaper value = new XYLeaper(pair.getLeft(), pair.getRight());
            leapersCache.put(pair, value);
            if (!Objects.equals(pair.getLeft(), pair.getRight())) {
                leapersCache.put(Pair.of(pair.getRight(), pair.getLeft()), value);
            }
        }
        return leapersCache.get(pair);
    }

    private static XYRider getRider(Pair<Integer, Integer> pair) {
        if (!ridersCache.containsKey(pair)) {
            XYRider value = new XYRider(pair.getLeft(), pair.getRight());
            ridersCache.put(pair, value);
        }
        return ridersCache.get(pair);
    }
}
