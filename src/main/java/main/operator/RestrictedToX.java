package main.operator;

import main.Move;
import main.OneMove;

import java.util.function.Predicate;

/**
 * Created by lukza on 28.12.2016.
 */
public class RestrictedToX extends Operator {

    private final int x;

    public RestrictedToX(int x) {
        super(2 * x);
        this.x = x;
    }

    @Override
    public Predicate<OneMove> matches() {
        return move -> {
            final Integer dx = move.getMoves().stream().map(Move::getDx).reduce(0, Integer::sum);
            final Integer dy = move.getMoves().stream().map(Move::getDy).reduce(0, Integer::sum);
            return Math.abs(dx) <= x && Math.abs(dy) <= x;
        };
    }

    @Override
    public String getDescription() {
        return "Restricted To " + x;
    }
}
