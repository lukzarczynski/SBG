package main.operator;

import main.OneMove;

import java.util.function.Predicate;

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
