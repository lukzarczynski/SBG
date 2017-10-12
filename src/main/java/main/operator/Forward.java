package main.operator;

import java.util.function.Predicate;

import main.model.Move;
import main.model.OneMove;

/**
 * Created by lukasz on 06.12.16.
 */
public class Forward extends Operator {
    @Override
    public Predicate<OneMove> matches() {
        return move -> {
            final Integer dy = move.getMoves().stream().map(Move::getDy).reduce(0, Integer::sum);
            return dy > 0;
        };
    }

    @Override
    public String getDescription() {
        return "Forward";
    }
}
