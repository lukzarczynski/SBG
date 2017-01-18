package main.resolvers;

import main.MoveUtil;
import main.model.OneMove;
import main.ParamsAndEvaluators;
import main.operator.Operator;
import main.piececlass.PieceClass;
import org.apache.commons.lang3.tuple.Pair;

import java.util.HashMap;
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
        super(ParamsAndEvaluators.evaluateSimpleResolver(pieceClass, ops));
        this.pieceClass = pieceClass;
        this.operators = ops;
    }

    @Override
    public boolean isApplicable(Set<OneMove> moves, Pair<Integer, Integer> xy) {
        return pieceClass.matches(moves, operators, xy);
    }

    @Override
    public ResolveResult apply(Set<OneMove> moves, Pair<Integer, Integer> xy) {
        Set<OneMove> apply = pieceClass.apply(moves, operators, xy);
        return new ResolveResult(apply, MoveUtil.subtract(moves, apply));
    }

    @Override
    public String getDescription() {
        return String.format(" like %s but only:  %s ",
                pieceClass.getDescription(),
                operators.stream().map(Operator::getDescription).collect(Collectors.joining(", ")));
    }

    public boolean containsMove(OneMove oneMove, Pair<Integer, Integer> xy) {
        Set<OneMove> oneMoves = pieceClass.filterMoves(operators, xy);
        return oneMoves.contains(oneMove);
    }

    public boolean containsMovePrefix(OneMove oneMove, Pair<Integer, Integer> xy) {
        final String m = oneMove.toString();
        Set<OneMove> oneMoves = pieceClass.filterMoves(operators, xy);
        return oneMoves.stream().anyMatch(om -> m.startsWith(om.toString()));
    }

    public boolean isApplicableForPrefixes(Set<OneMove> moves, Pair<Integer, Integer> xy) {
        return pieceClass.matchesPrefix(moves, operators, xy);
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

    public PrefixResolveResult applyForPrefixes(Set<OneMove> moves, Pair<Integer, Integer> xy) {
        Map<OneMove, OneMove> oneMoves = pieceClass.applyPrefix(moves, operators, xy);

        final Map<OneMove, OneMove> result = new HashMap<>();
        oneMoves.forEach((k, v) -> {
            if (!v.equals(OneMove.EMPTY_MOVE)) {
                result.put(k, v);
            }
        });
        return new PrefixResolveResult(
                result,
                MoveUtil.subtract(moves, oneMoves.keySet()));
    }
}
