package main.resolvers;

import main.MoveUtil;
import main.ParamsAndEvaluators;
import main.model.OneMove;
import main.operator.ExactlyTimes;
import main.operator.MaxTimes;
import main.operator.MinTimes;
import main.operator.Operator;
import main.piececlass.PieceClass;
import main.piececlass.XYRider;
import org.apache.commons.lang3.tuple.Pair;

import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Created by lzarczynski on 30.12.2016.
 */
public class SimplePieceResolver extends Resolver {

    private PieceClass pieceClass;
    private Set<Operator> operators;

    public SimplePieceResolver(PieceClass pieceClass, Set<Operator> ops) {
        super(ParamsAndEvaluators.fko(pieceClass, ops));
        this.pieceClass = pieceClass;
        this.operators = ops;
    }

    public boolean isValid() {
        if (!(pieceClass instanceof XYRider)) {
            if (operators.stream().anyMatch(o -> o instanceof MaxTimes || o instanceof MinTimes || o instanceof ExactlyTimes)) {
                return false;
            }
        }
        return true;
    }

    public boolean isValidFor(Collection<OneMove> moves, OneMove oneMove, Pair<Integer, Integer> xy) {
        return pieceClass.isSubsetAndContains(moves, operators, oneMove, xy);
    }

    public boolean isValidForPart(Collection<OneMove> moves, Pair<Integer, Integer> xy, int part) {
        final Pair<Integer, Integer> vector = moves.stream().map(m -> m.getParts().get(part))
                .findAny().get().getVector();

        return pieceClass.isSubset(moves, operators, xy, part, vector);
    }

    public boolean isValidForWithVector(Collection<OneMove> moves, OneMove oneMove, Pair<Integer, Integer> xy, Pair<Integer, Integer> vector) {
        return pieceClass.isSubsetAndContainsWithVector(moves, operators, oneMove, xy, vector);
    }

    public boolean isValidForWithVector(Collection<OneMove> moves, Pair<Integer, Integer> xy, Pair<Integer, Integer> vector) {
        return pieceClass.isSubsetWithVector(moves, operators, xy, vector);
    }

    public ResolveResult resolve(Collection<OneMove> moves, Pair<Integer, Integer> xy) {
        final Collection<OneMove> notMatchingMoves = pieceClass.getNotMatchingMoves(moves, operators, xy);
        return new ResolveResult(notMatchingMoves,
                MoveUtil.subtract(moves, notMatchingMoves));
    }

    public ResolveResult resolveWithVector(Collection<OneMove> moves, Pair<Integer, Integer> xy, Pair<Integer, Integer> vector) {
        final Collection<OneMove> notMatchingMoves = pieceClass.getNotMatchingMovesWithVector(moves, operators, xy, vector);
        return new ResolveResult(notMatchingMoves,
                MoveUtil.subtract(moves, notMatchingMoves));
    }

    @Override
    public String getDescription() {
        return String.format(" %s but %s ",
                pieceClass.getDescription(),
                operators.stream().map(Operator::getDescription).collect(Collectors.joining(", ")));
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;

        SimplePieceResolver that = (SimplePieceResolver) o;

        return pieceClass.equals(that.pieceClass) && operators.equals(that.operators);
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + pieceClass.hashCode();
        result = 31 * result + operators.hashCode();
        return result;
    }

    public PieceClass getPieceClass() {
        return pieceClass;
    }

    public Set<Operator> getOperators() {
        return operators;
    }

    public Set<OneMove> filterForPart(Set<OneMove> moves, int part, Pair<Integer, Integer> xy) {
        return pieceClass.filterForPart(moves, part, operators, xy);
    }

    public PrefixResolveResult applyForPrefixes(Map<OneMove, OneMove> mapOfMovesAndItsPrefix, Pair<Integer, Integer> xy) {
        final Map<OneMove, Set<OneMove>> resultMap
                = pieceClass.getMapOfMatchedPrefixesAndItsSuffixes(mapOfMovesAndItsPrefix, operators, xy);

        return new PrefixResolveResult(resultMap);
    }

}
