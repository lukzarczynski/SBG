package main.piececlass;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import main.OneMove;
import main.operator.None;
import main.operator.Operator;

/**
 * Created by lukasz on 06.12.16.
 */
public abstract class PieceClass {

    protected Set<OneMove> moves = new HashSet<>();

    public boolean matches(Set<OneMove> moves) {
        return matches(moves, None.instance(0));
    }

    public boolean matches(Set<OneMove> moves, Collection<Operator> operators) {
        return matches(moves, operators.toArray(new Operator[operators.size()]));
    }

    public Set<OneMove> apply(Set<OneMove> moves, Collection<Operator> operators) {
        return apply(moves, operators.toArray(new Operator[operators.size()]));
    }

    public abstract boolean matches(Set<OneMove> moves, Operator... op);

    public abstract Set<OneMove> apply(Set<OneMove> moves, Operator... op);

    public abstract String getDescription();

    public Set<OneMove> getMoves() {
        return moves;
    }

    public Set<OneMove> filterMoves(Operator[] op) {
        return filterMoves(Arrays.asList(op));
    }

    public Set<OneMove> filterMoves(List<Operator> op) {
        return moves.stream()
                .filter(m -> op.stream().allMatch(o -> o.matches().test(m)))
                .map(m -> {
                    OneMove r = m;
                    for (Operator o : op) {
                        r = o.map().apply(r);
                    }
                    return r;
                })
                .collect(Collectors.toSet());
    }

}
