package main.operator;

import main.model.Move;
import main.model.OneMove;

import java.util.Objects;
import java.util.function.Predicate;

/**
 * Przekątna
 * <p>
 * Created by lukasz on 06.12.16.
 */
public class Diagonal extends Operator {

    @Override
    public Predicate<OneMove> matches() {
        return move -> {
            final Integer dx = move.getMoves().stream().map(Move::getDx).reduce(0, Integer::sum);
            final Integer dy = move.getMoves().stream().map(Move::getDy).reduce(0, Integer::sum);
            return Objects.equals(dx, dy);
        };
    }

    @Override
    public String getDescription() {
        return "Diagonal";
    }
}
