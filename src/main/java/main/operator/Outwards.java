package main.operator;

import main.model.OneMove;

import java.util.function.Predicate;

/**
 * means that distance to (0,0) at each point is not decreasing
 * <p>
 * Created by lukza on 21.01.2017.
 */
public class Outwards extends Operator {

    private OutwardsX ox = new OutwardsX();
    private OutwardsY oy = new OutwardsY();

    @Override
    public Predicate<OneMove> matches() {
        return ox.matches().and(oy.matches());
    }

    @Override
    public String getDescription() {
        return "Outwards";
    }
}
