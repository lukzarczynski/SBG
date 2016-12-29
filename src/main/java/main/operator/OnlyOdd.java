package main.operator;

import main.OneMove;

import java.util.function.Predicate;

/**
 * Created by lukza on 28.12.2016.
 */
public class OnlyOdd extends Operator {

    @Override
    public Predicate<OneMove> matches() {
        return new OnlyEven().matches().negate();
    }

    @Override
    public String getDescription() {
        return "Only odd";
    }
}
