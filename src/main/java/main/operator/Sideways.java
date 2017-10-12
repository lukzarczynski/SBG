package main.operator;

import java.util.function.Predicate;

import main.model.Move;
import main.model.OneMove;

/**
 * Created by lzarczynski on 27.12.2016.
 */
public class Sideways extends Operator {

    @Override
    public Predicate<OneMove> matches() {
        return move -> {
            final Integer dx = move.getMoves().stream().map(Move::getDx).reduce(0, Integer::sum);
            return dx != 0;
        };
    }

    @Override
    public String getDescription() {
        return "Sideways";
    }
}
