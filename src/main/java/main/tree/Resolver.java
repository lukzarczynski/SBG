package main.tree;

import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import main.OneMove;
import main.operator.Operator;
import main.piececlass.PieceClass;

/**
 * Created by lukasz on 07.12.16.
 */
public class Resolver {

    protected PieceClass pieceClass;
    protected List<Operator> operators;
    protected boolean valid = true;
    protected int priority;

    public Resolver() {
    }

    public Resolver(PieceClass pieceClass, Operator... ops) {
        this.pieceClass = pieceClass;
        this.operators = Arrays.asList(ops);

        this.priority = this.operators.stream().map(o -> o.priority).reduce(0, Integer::sum);
        this.priority += (ops.length - 1) * 20;

        final Set<OneMove> allFilteredMoves = pieceClass.filterMoves(ops);
        valid = !allFilteredMoves.isEmpty();
        if (valid) {
            valid = this.operators.stream()
                    .anyMatch(op -> {
                        final List<Operator> collect = this.operators.stream()
                                .filter(a -> a != op)
                                .collect(Collectors.toList());
                        pieceClass.filterMoves(collect.toArray(new Operator[collect.size()]));
                        final Set<OneMove> oneMoves = pieceClass.filterMoves(new Operator[]{op});
                        return oneMoves.size() == allFilteredMoves.size();
                    });
        }
    }


    public boolean matches(Set<OneMove> moves) {
        return pieceClass.matches(moves, operators);
    }

    public Set<OneMove> apply(Set<OneMove> moves) {
        return pieceClass.apply(moves, operators);
    }


    public int getPriority() {
        return priority;
    }

    public boolean isValid() {
        return valid;
    }


    public String getDescription() {
        return String.format("like %s with ops: %s",
                             pieceClass.getDescription(),
                             operators.stream().map(Operator::getDescription).collect(Collectors.joining(", ")));

    }
}
