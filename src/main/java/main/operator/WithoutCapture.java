package main.operator;

import java.util.function.Predicate;
import main.OneMove;

/**
 * moves that end only without capturing enemy piece
 * <p>
 * Created by lukasz on 06.12.16.
 */
public class WithoutCapture extends Operator {

    private static final Operator instance = new WithoutCapture();

    public static Operator instance(int priority) {
        instance.priority = priority;
        return instance;
    }

    @Override
    public Predicate<OneMove> matches() {
        return move -> !move.getMoves().get(move.getMoves().size() - 1).getPiece();
    }

    @Override public String getDescription() {
        return "Without Capture";
    }
}
