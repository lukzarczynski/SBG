package main.piececlass;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import main.MoveUtil;
import main.Point;
import main.model.OneMove;
import main.operator.Operator;

/**
 * Created by lukasz on 06.12.16.
 */
public abstract class PieceClass {

    protected Set<OneMove> moves = new HashSet<>();
    private Point xy;

    protected PieceClass(Point xy) {
        this.xy = xy;
    }

    public boolean isSubsetAndContains(Collection<OneMove> moves,
                                       Collection<Operator> operators,
                                       OneMove oneMove,
                                       Point xy) {
        final Set<OneMove> filteredMoves = filterMoves(operators, xy);
        return !filteredMoves.isEmpty()
                && filteredMoves.contains(oneMove)
                && MoveUtil.containsAll(moves, filteredMoves);

    }

    public boolean isSubset(Collection<OneMove> moves,
                            Collection<Operator> operators,
                            Point xy,
                            int part, Point vector) {
        final Set<OneMove> filteredMoves = filterWithVector(operators, xy, vector);
        return !filteredMoves.isEmpty()
                && MoveUtil.containsAllForPart(moves, filteredMoves, part);

    }


    public boolean isSubsetWithVector(Collection<OneMove> moves, Set<Operator> operators, Point xy, Point vector) {
        final Set<OneMove> filteredMoves = filterWithVector(operators, xy, vector);
        return !filteredMoves.isEmpty()
                && MoveUtil.containsAll(moves, filteredMoves);
    }

    public boolean isSubsetAndContainsWithVector(Collection<OneMove> moves,
                                                 Collection<Operator> operators,
                                                 OneMove oneMove,
                                                 Point xy,
                                                 Point vector) {
        final Set<OneMove> filteredMoves = filterWithVector(operators, xy, vector);
        return !filteredMoves.isEmpty()
                && filteredMoves.contains(oneMove)
                && MoveUtil.containsAll(moves, filteredMoves);

    }

    /**
     * @return all moves that did not match
     */
    public Collection<OneMove> getNotMatchingMoves(Collection<OneMove> moves,
                                                   Collection<Operator> operators,
                                                   Point xy) {
        final Set<OneMove> filteredMoves = filterMoves(operators, xy);
        return filteredMoves.isEmpty() ? moves : MoveUtil.subtract(moves, filteredMoves);
    }

    public Collection<OneMove> getNotMatchingMovesWithVector(Collection<OneMove> moves,
                                                             Collection<Operator> operators,
                                                             Point xy,
                                                             Point vector) {
        final Set<OneMove> filteredMoves = filterWithVector(operators, xy, vector);
        return filteredMoves.isEmpty() ? moves : MoveUtil.subtract(moves, filteredMoves);
    }

    /**
     * @param prefixes key: move, value: prefix(move)
     * @param xy       board size
     * @return key: prefix, value: set of suffixes
     */
    public Map<OneMove, Set<OneMove>> getMapOfMatchedPrefixesAndItsSuffixes(Map<OneMove, OneMove> prefixes,
                                                                            Set<Operator> operators,
                                                                            Point xy) {
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

    public Set<OneMove> filterForPart(Set<OneMove> moves, int part, Set<Operator> operators,
                                      Point xy) {
        final Set<OneMove> filteredMoves = filterMoves(operators, xy);

        return moves.stream()
                .filter(m -> filteredMoves.contains(m.getParts().get(part)))
                .collect(Collectors.toSet());


    }

    public abstract String getDescription();

    /**
     * @param op operators
     * @param xy board size
     * @return moves that matches all operators
     */
    public Set<OneMove> filterMoves(Collection<Operator> op, Point xy) {
        return moves.stream()
                .filter(m -> op.stream().allMatch(o -> o.matches().test(m)))
                .map(m -> {
                    Set<OneMove> r = new HashSet<>();
                    r.add(m);
                    for (Operator o : op) {
                        if (o.isHasFunction()) {
                            r = r.stream()
                                    .map(c -> o.map().apply(c))
                                    .flatMap(Collection::stream)
                                    .collect(Collectors.toSet());
                        }
                    }
                    return r;
                })
                .flatMap(Collection::stream)
                .filter(om -> om.isValid(xy))
                .collect(Collectors.toSet());
    }

    public Set<OneMove> filterWithVector(Collection<Operator> op, Point xy, Point vector) {
        this.moves.forEach(m -> m.setVector(vector));

        final Set<OneMove> result = filterMoves(op, xy);

        final Point zero = Point.of(0, 0);
        this.moves.forEach(m -> m.setVector(zero));
        return result;
    }

    public Point getXy() {
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
