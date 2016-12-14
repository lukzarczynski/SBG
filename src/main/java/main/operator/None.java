package main.operator;

import java.util.function.Predicate;
import main.OneMove;

/**
 * Created by lukasz on 06.12.16.
 */
public class None extends Operator {

    private static final Operator instance = new None();

    public static Operator instance(int priority) {
        instance.priority = priority;
        return instance;
    }


    @Override
    public Predicate<OneMove> matches() {
        return move -> true;
    }

    @Override public String getDescription() {
        return "None";
    }
}
