package main.operator;

import java.util.function.Predicate;

import main.model.OneMove;

/**
 * Created by lukasz on 06.12.16.
 */
public class None extends Operator {

    @Override
    public Predicate<OneMove> matches() {
        return move -> true;
    }

    @Override
    public String getDescription() {
        return "None";
    }
}
