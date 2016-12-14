package main.operator;

import java.util.function.Predicate;
import main.Move;
import main.OneMove;

/**
 * Created by lukasz on 06.12.16.
 */
public class RestrictedToOne extends Operator {

    private static final Operator instance = new RestrictedToOne();

    public static Operator instance(int priority) {
        instance.priority = priority;
        return instance;
    }

    @Override
    public Predicate<OneMove> matches() {
        return move -> {
            final Integer dx = move.getMoves().stream().map(Move::getDx).reduce(0, Integer::sum);
            final Integer dy = move.getMoves().stream().map(Move::getDy).reduce(0, Integer::sum);
            return Math.abs(dx) <= 1 && Math.abs(dy) <= 1;
        };
    }

    @Override public String getDescription() {
        return "Restricted To One";
    }
}
