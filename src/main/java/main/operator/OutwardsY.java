package main.operator;

import java.util.function.Predicate;

import main.model.Move;
import main.model.OneMove;

/**
 * means that distance to (0,0) at each point is not decreasing <p> Created by lukza on 21.01.2017.
 */
public class OutwardsY extends Operator {

    @Override
    public Predicate<OneMove> matches() {
        return om -> {
            int startY = om.getVector().getValue();
            int y = startY;
            for (Move m : om.getMoves()) {

                y += m.getDy();

                if (startY != 0) {
                    if (startY > 0 ? y < startY : y > startY) {
                        return false;
                    }
                }
            }
            return true;
        };
    }

    @Override
    public String getDescription() {
        return "Outwards Y";
    }
}
