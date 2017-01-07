package main.resolvers;

import main.MoveUtil;
import main.OneMove;
import main.operator.Operator;
import main.piececlass.PieceClass;
import main.tree.ResolveResult;
import org.apache.commons.lang3.tuple.Pair;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Created by lzarczynski on 30.12.2016.
 */
public class SimplePieceResolver extends Resolver {

    protected PieceClass pieceClass;
    protected Set<Operator> operators;

    public SimplePieceResolver(PieceClass pieceClass, Set<Operator> ops) {
        super(ops.stream().map(o -> o.value).reduce(0, Integer::sum) + ((ops.size() - 1) * 20));
        this.pieceClass = pieceClass;
        this.operators = ops;

        final Set<OneMove> allFilteredMoves = pieceClass.filterMoves(ops);
        valid = !allFilteredMoves.isEmpty();
        if (valid) {
            valid = this.operators.stream()
                    .noneMatch(op -> {
                        final List<Operator> allExceptOp = this.operators.stream()
                                .filter(a -> a != op)
                                .collect(Collectors.toList());
                        final Set<OneMove> oneMoves =
                                pieceClass.filterMoves(allExceptOp);
                        return oneMoves.size() == allFilteredMoves.size();
                    });
        }
    }

    @Override
    public boolean isApplicable(Set<OneMove> moves) {
        return pieceClass.matches(moves, operators);
    }

    @Override
    public ResolveResult apply(Set<OneMove> moves) {
        Set<OneMove> apply = pieceClass.apply(moves, operators);
        return new ResolveResult(apply, MoveUtil.subtract(moves, apply));
    }

    @Override
    public String getDescription() {
        return String.format(" like %s but only:  %s ",
                pieceClass.getDescription(),
                operators.stream().map(Operator::getDescription).collect(Collectors.joining(", ")));
    }

    @Override
    public boolean containsMove(OneMove oneMove) {
        Set<OneMove> oneMoves = pieceClass.filterMoves(operators);
        return oneMoves.contains(oneMove);
    }

    @Override
    public boolean containsMovePrefix(OneMove oneMove) {
        final String m = oneMove.toString();
        Set<OneMove> oneMoves = pieceClass.filterMoves(operators);
        return oneMoves.stream().anyMatch(om -> m.startsWith(om.toString()));
    }

    public boolean isApplicableForPrefixes(Set<OneMove> moves) {
        return pieceClass.matchesPrefix(moves, operators);
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

    public PrefixResolveResult applyForPrefixes(Set<OneMove> moves) {
        Map<OneMove, OneMove> oneMoves = pieceClass.applyPrefix(moves, operators);
        oneMoves.values().remove(OneMove.EMPTY_MOVE);
        return new PrefixResolveResult(
                oneMoves.entrySet().stream()
                        .filter(e -> !e.getValue().equals(OneMove.EMPTY_MOVE))
                        .map(e -> Pair.of(
                                e.getKey(),
                                e.getValue()))
                        .collect(Collectors.toSet()),
                MoveUtil.subtract(moves, oneMoves.keySet()));
    }
}
