package main.operator;

import main.model.OneMove;
import main.ParamsAndEvaluators;

import java.util.function.Function;
import java.util.function.Predicate;

/**
 * Created by lukasz on 06.12.16.
 */
public abstract class Operator {

    public final int value;

    public Operator() {
        this.value = ParamsAndEvaluators.getOperatorValueForClass(this.getClass());
    }

    protected Operator(int value) {
        this.value = value;
    }

    public abstract Predicate<OneMove> matches();

    public Function<OneMove, OneMove> map() {
        return Function.identity();
    }

    public abstract String getDescription();

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Operator operator = (Operator) o;

        return getDescription() == operator.getDescription();
    }

    @Override
    public int hashCode() {
        return getDescription().hashCode();
    }

    public int getValue() {
        return value;
    }

}
