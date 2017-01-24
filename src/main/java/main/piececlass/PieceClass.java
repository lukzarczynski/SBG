package main.piececlass;

import main.MoveUtil;
import main.model.OneMove;
import main.operator.Operator;
import org.apache.commons.lang3.tuple.Pair;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Created by lukasz on 06.12.16.
 */
public abstract class PieceClass {

    protected Set<OneMove> moves = new HashSet<>();
    private Pair<Integer, Integer> xy;

    protected PieceClass(Pair<Integer, Integer> xy) {
        this.xy = xy;
    }

    public boolean isSubsetAndContains(Collection<OneMove> moves,
                                       Collection<Operator> operators,
                                       OneMove oneMove,
                                       Pair<Integer, Integer> xy) {
        final Set<OneMove> filteredMoves = filterMoves(operators, xy);
        return !filteredMoves.isEmpty()
                && filteredMoves.contains(oneMove)
                && MoveUtil.containsAll(moves, filteredMoves);

    }


    public boolean isSubsetWithVector(Collection<OneMove> moves, Set<Operator> operators, Pair<Integer, Integer> xy, Pair<Integer, Integer> vector) {
        final Set<OneMove> filteredMoves = filterWithVector(operators, xy, vector);
        return !filteredMoves.isEmpty()
                && MoveUtil.containsAll(moves, filteredMoves);
    }

    public boolean isSubsetAndContainsWithVector(Collection<OneMove> moves,
                                                 Collection<Operator> operators,
                                                 OneMove oneMove,
                                                 Pair<Integer, Integer> xy,
                                                 Pair<Integer, Integer> vector) {
        final Set<OneMove> filteredMoves = filterWithVector(operators, xy, vector);
        return !filteredMoves.isEmpty()
                && filteredMoves.contains(oneMove)
                && MoveUtil.containsAll(moves, filteredMoves);

    }

    /**
     * @param moves
     * @param operators
     * @param xy
     * @return all moves that did not match
     */
    public Collection<OneMove> getNotMatchingMoves(Collection<OneMove> moves,
                                                   Collection<Operator> operators,
                                                   Pair<Integer, Integer> xy) {
        final Set<OneMove> filteredMoves = filterMoves(operators, xy);
        return filteredMoves.isEmpty() ? moves : MoveUtil.subtract(moves, filteredMoves);
    }

    public Collection<OneMove> getNotMatchingMovesWithVector(Collection<OneMove> moves,
                                                             Collection<Operator> operators,
                                                             Pair<Integer, Integer> xy,
                                                             Pair<Integer, Integer> vector) {
        final Set<OneMove> filteredMoves = filterWithVector(operators, xy, vector);
        return filteredMoves.isEmpty() ? moves : MoveUtil.subtract(moves, filteredMoves);
    }

    /**
     * @param prefixes  key: move, value: prefix(move)
     * @param operators
     * @param xy        board size
     * @return key: prefix, value: set of suffixes
     */
    public Map<OneMove, Set<OneMove>> getMapOfMatchedPrefixesAndItsSuffixes(Map<OneMove, OneMove> prefixes,
                                                                            Set<Operator> operators,
                                                                            Pair<Integer, Integer> xy) {
        final Set<OneMove> filteredMoves = filterMoves(operators, xy);

        final Map<OneMove, Set<OneMove>> result = new HashMap<>();

        prefixes.forEach((move, prefix) -> {
            if (filteredMoves.contains(prefix)) {
                result.putIfAbsent(prefix, new HashSet<>());
                result.get(prefix).add(move.withoutPrefix(prefix));
            }
        });

        return result;

    }

    public abstract String getDescription();

    /**
     * @param op operators
     * @param xy board size
     * @return moves that matches all operators
     */
    public Set<OneMove> filterMoves(Collection<Operator> op, Pair<Integer, Integer> xy) {
        return moves.stream()
                .filter(m -> op.stream().allMatch(o -> o.matches().test(m)))
                .map(m -> {
                    OneMove r = m;
                    for (Operator o : op) {
                        r = o.map().apply(r);
                    }
                    return r;
                })
                .filter(om -> om.isValid(xy))
                .collect(Collectors.toSet());
    }

    public Set<OneMove> filterWithVector(Collection<Operator> op, Pair<Integer, Integer> xy, Pair<Integer, Integer> vector) {
        this.moves.forEach(m -> m.setVector(vector));

        final Set<OneMove> result = filterMoves(op, xy);

        final Pair<Integer, Integer> zero = Pair.of(0, 0);
        this.moves.forEach(m -> m.setVector(zero));
        return result;
    }

    public Pair<Integer, Integer> getXy() {
        return xy;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        PieceClass that = (PieceClass) o;

        return moves.equals(that.moves);
    }

    @Override
    public int hashCode() {
        return moves.hashCode();
    }

}
