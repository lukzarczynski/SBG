package main.operator;

import java.util.Objects;
import java.util.function.Predicate;
import main.Move;
import main.OneMove;

/**
 * Przekątna
 * <p>
 * Created by lukasz on 06.12.16.
 */
public class Diagonal extends Operator {

    private static final Operator instance = new Diagonal();

    public static Operator instance(int priority) {
        instance.priority = priority;
        return instance;
    }

    @Override public Predicate<OneMove> matches() {
        return move -> {
            final Integer dx = move.getMoves().stream().map(Move::getDx).reduce(0, Integer::sum);
            final Integer dy = move.getMoves().stream().map(Move::getDy).reduce(0, Integer::sum);
            return Objects.equals(dx, dy);
        };
    }

    @Override public String getDescription() {
        return "Diagonal";
    }
}
