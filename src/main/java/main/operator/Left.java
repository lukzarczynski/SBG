package main.operator;

import java.util.function.Predicate;
import main.Move;
import main.OneMove;

/**
 * Created by lukasz on 06.12.16.
 */
public class Left extends Operator {

    private static final Operator instance = new Left();

    public static Operator instance(int priority) {
        instance.priority = priority;
        return instance;
    }

    @Override
    public Predicate<OneMove> matches() {
        return move -> {
            final Integer dx = move.getMoves().stream().map(Move::getDx).reduce(0, Integer::sum);
            return dx < 0;
        };
    }

    @Override public String getDescription() {
        return "Left";
    }
}
