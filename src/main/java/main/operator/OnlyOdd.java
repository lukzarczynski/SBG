package main.operator;

import java.util.function.Predicate;

import main.model.OneMove;

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
