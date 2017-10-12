package main.operator;

import java.util.function.Predicate;

import main.model.Move;
import main.model.OneMove;

/**
 * Created by lukza on 28.12.2016.
 */
public class OnlyEven extends Operator {

    @Override
    public Predicate<OneMove> matches() {
        return move -> {
            final Integer dx = move.getMoves().stream().map(Move::getDx).reduce(0, Integer::sum);
            final Integer dy = move.getMoves().stream().map(Move::getDy).reduce(0, Integer::sum);
            return isEven(dx) && isEven(dy);
        };
    }

    private boolean isEven(Integer dx) {
        return dx % 2 == 0;
    }

    @Override
    public String getDescription() {
        return "Only even";
    }
}
