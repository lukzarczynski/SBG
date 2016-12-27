package main.tree;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import main.Move;
import main.OneMove;
import main.operator.Operator;
import main.piececlass.XYLeaper;

/**
 * Created by lukasz on 07.12.16.
 */
public class FirstMoveResolver extends Resolver {

    protected boolean valid = true;
    protected int priority;

    public FirstMoveResolver(Operator... ops) {
        super(new XYLeaper(1, 1), ops);
        this.operators = Arrays.asList(ops);

        this.priority = this.operators.stream().map(o -> o.priority).reduce(0, Integer::sum);
        this.priority += (ops.length - 1) * 20;
        this.priority *= 2;

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

    private static List<Move> newArrayList(Move... ops) {
        List<Move> l = new ArrayList<>();
        Collections.addAll(l, ops);
        return l;
    }

    @Override
    public boolean matches(Set<OneMove> moves) {
        return pieceClass.matches(getOnlyFirstMoves(moves), operators);
    }

    @Override
    public ResolveResult apply(Set<OneMove> moves) {
        final Set<Move> oneMoves = pieceClass.filterMoves(operators).stream()
                .map(om -> om.getMoves().get(0))
                .collect(Collectors.toSet());
        return null;
//        return moves.stream()
//                .filter(m -> m.getMoves().size() > 1)
//                .filter(m -> {
//                    final Move move = m.getMoves().get(0);
//                    return oneMoves.contains(move);
//                })
//                .map(om -> OneMove.of(om.getMoves().subList(1, om.getMoves().size())))
//                .collect(Collectors.toSet());
    }

    @Override
    public int getPriority() {
        return priority;
    }

    @Override
    public boolean isValid() {
        return valid;
    }

    private Set<OneMove> getOnlyFirstMoves(Set<OneMove> moves) {
        return moves.stream()
                .filter(m -> m.getMoves().size() > 1)
                .map(m -> OneMove.of(newArrayList(m.getMoves().get(0))))
                .collect(Collectors.toSet());
    }

    @Override public String getDescription() {
        return super.getDescription() + "FIRST MOVE AND THEN";
    }
}
