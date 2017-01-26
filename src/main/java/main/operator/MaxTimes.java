package main.operator;

import main.model.OneMove;

import java.util.function.Predicate;

/**
 * Created by lukza on 28.12.2016.
 */
public class MaxTimes extends Operator {

    private final int x;

    public MaxTimes(int x) {
        super();
        this.x = x;
    }

    @Override
    public Predicate<OneMove> matches() {
//        return move -> {
//            final Integer dx = move.getMoves().stream().map(m -> m.getDx() * m.getPower()).reduce
//                (0,
//                Integer::sum);
//            final Integer dy = move.getMoves().stream().map(m -> m.getDy() * m.getPower()).reduce
//                (0,
//                Integer::sum);
//            return Math.abs(dx) <= x && Math.abs(dy) <= x;
//        };
        return move -> move.getMoves().size() <= x;
    }

    @Override
    public String getDescription() {
        return "Max " + x + " MaxTimes";
    }

    public int getX() {
        return x;
    }
}
