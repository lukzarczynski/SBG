package main.operator;

import java.util.Set;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import main.OneMove;

/**
 * Created by lukasz on 06.12.16.
 */
public abstract class Operator {

    public int priority = 0;

    public abstract Predicate<OneMove> matches();

    public Function<OneMove, OneMove> map() {
        return Function.identity();
    }

    public Set<OneMove> filter(Set<OneMove> moves) {
        return moves.stream().filter(matches()).collect(Collectors.toSet());
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
}
