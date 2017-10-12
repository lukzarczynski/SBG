package main.operator;

import java.util.function.Predicate;

import main.model.Move;
import main.model.OneMove;

/**
 * Created by lukza on 24.01.2017.
 */
public class NotHorizontal extends Operator {

    @Override
    public Predicate<OneMove> matches() {
        return move -> {
            final Integer dy = move.getMoves().stream().map(Move::getDy).reduce(0, Integer::sum);
            return dy != 0;
        };
    }

    @Override
    public String getDescription() {
        return "Not Horizontal";
    }
}
