package main.tree;

import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import main.MoveUtil;
import main.OneMove;
import main.operator.Operator;
import main.piececlass.PieceClass;
import org.apache.commons.lang3.tuple.Pair;

/**
 * Created by lukasz on 07.12.16.
 */
public class Resolver {

    protected PieceClass pieceClass;
    protected List<Operator> operators;
    protected boolean valid = true;
    protected int priority;

    public Resolver(PieceClass pieceClass, Operator... ops) {
        this.pieceClass = pieceClass;
        this.operators = Arrays.asList(ops);

        this.priority = this.operators.stream().map(o -> o.priority).reduce(0, Integer::sum);
        this.priority += (ops.length - 1) * 20;

        final Set<OneMove> allFilteredMoves = pieceClass.filterMoves(ops);
        valid = !allFilteredMoves.isEmpty();
        if (valid) {
            valid = this.operators.stream()
                    .noneMatch(op -> {
                        final List<Operator> allExceptOp = this.operators.stream()
                                .filter(a -> a != op)
                                .collect(Collectors.toList());
                        final Set<OneMove> oneMoves =
                                pieceClass.filterMoves(allExceptOp.toArray(new Operator[allExceptOp.size()]));
                        return oneMoves.size() == allFilteredMoves.size();
                    });
        }
    }

    public boolean matches(Set<OneMove> moves) {
        return pieceClass.matches(moves, operators);
    }

    public ResolveResult apply(Set<OneMove> moves) {
        Set<OneMove> apply = pieceClass.apply(moves, operators);
        return new ResolveResult(apply, MoveUtil.subtract(moves, apply));
    }


    public int getPriority() {
        return priority;
    }

    public boolean isValid() {
        return valid;
    }

    public String getDescription() {
        return String.format(" | %s : %s | ",
                pieceClass.getDescription(),
                operators.stream().map(Operator::getDescription).collect(Collectors.joining(", ")));
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Resolver resolver = (Resolver) o;

        if (!pieceClass.equals(resolver.pieceClass)) return false;
        return operators.equals(resolver.operators);
    }

    @Override
    public int hashCode() {
        int result = pieceClass.hashCode();
        result = 31 * result + operators.hashCode();
        return result;
    }
}
