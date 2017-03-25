package main.operator;

import main.ParamsAndEvaluators;
import main.model.OneMove;

import java.util.HashSet;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Predicate;

/**
 * Created by lukasz on 06.12.16.
 */
public abstract class Operator {

    public final int value;

    private boolean hasFunction = true;

    public Operator() {
        this.value = ParamsAndEvaluators.fo(this.getClass());
        this.map();
    }

    public abstract Predicate<OneMove> matches();

    public Function<OneMove, Set<OneMove>> map() {
        hasFunction = false;
        return om -> {
            final HashSet<OneMove> hashSet = new HashSet<>();
            hashSet.add(om);
            return hashSet;
        };
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

    public boolean isHasFunction() {
        return hasFunction;
    }

    protected Set<OneMove> setOf(OneMove m){
        final Set<OneMove> set = new HashSet<>();
        set.add(m);
        return set;
    }
}
