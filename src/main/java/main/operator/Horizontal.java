package main.operator;

import main.Move;
import main.OneMove;

import java.util.function.Predicate;

/**
 * pozioma
 * <p>
 * Created by lukasz on 06.12.16.
 */
public class Horizontal extends Operator {

    @Override
    public Predicate<OneMove> matches() {
        return move -> {
            final Integer dy = move.getMoves().stream().map(Move::getDy).reduce(0, Integer::sum);
            return dy == 0;
        };
    }

    @Override
    public String getDescription() {
        return "Horizontal";
    }
}
