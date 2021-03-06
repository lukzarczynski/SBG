package main.operator;

import main.model.OneMove;

import java.util.function.Predicate;

/**
 * Created by lukza on 28.12.2016.
 */
public class ExactlyTimes extends Operator {

    private final int x;

    public ExactlyTimes(int x) {
        super();
        this.x = x;
    }

    @Override
    public Predicate<OneMove> matches() {
        return move -> move.getMoves().size() == x;
    }

    @Override
    public String getDescription() {
        return "Exactly " + x + " Times";
    }

    public int getX() {
        return x;
    }
}
