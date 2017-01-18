package main.operator;

import main.model.Move;
import main.model.OneMove;

import java.util.function.Predicate;

/**
 * pionowa
 * <p>
 * Created by lukasz on 06.12.16.
 */
public class Orthogonal extends Operator {

    @Override
    public Predicate<OneMove> matches() {
        return move -> {
            final Integer dx = move.getMoves().stream().map(Move::getDx).reduce(0, Integer::sum);
            return dx == 0;
        };
    }

    @Override
    public String getDescription() {
        return "Orthogonal";
    }
}
