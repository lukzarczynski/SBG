package main.operator;

import java.util.function.Predicate;
import main.Move;
import main.OneMove;

/**
 * Created by lukasz on 06.12.16.
 */
public class Backwards extends Operator {

    private static final Operator instance = new Backwards();

    public static Operator instance(int priority) {
        instance.priority = priority;
        return instance;
    }

    @Override
    public Predicate<OneMove> matches() {
        return move -> {
            final Integer dy = move.getMoves().stream().map(Move::getDy).reduce(0, Integer::sum);
            return dy < 0;
        };
    }

    @Override public String getDescription() {
        return "Backwards";
    }
}
