package main.resolvers;

import main.model.OneMove;
import org.apache.commons.lang3.tuple.Pair;

import java.util.Collection;
import java.util.Set;

/**
 * Created by lukasz on 07.12.16.
 */
public abstract class Resolver {
    private int value;

    public Resolver(int value) {
        this.value = value;
    }

    public abstract String getDescription();

    public int getValue() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Resolver resolver = (Resolver) o;

        return value == resolver.value;
    }

    @Override
    public int hashCode() {
        int result = 1;
        result = 31 * result + value;
        return result;
    }
}
