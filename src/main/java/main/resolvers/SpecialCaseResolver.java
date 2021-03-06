package main.resolvers;

import main.ParamsAndEvaluators;
import main.model.Move;
import main.model.OneMove;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Created by lzarczynski on 30.12.2016.
 */
public class SpecialCaseResolver extends Resolver {

    private final OneMove om;
    private final String description;

    public SpecialCaseResolver(OneMove om) {
        super(ParamsAndEvaluators.fsc(om));
        this.om = om;

        List<String> conditions = new ArrayList<>();
        int x = 0;
        int y = 0;
        for (Move m : om.getMoves().subList(0, om.getMoves().size() - 1)) {
            x += m.getDx();
            y += m.getDy();
            conditions.add(String.format("(%s,%s,%s)", x, y, m.getMoveType().getCode()));
        }

        Move last = om.getMoves().get(om.getMoves().size() - 1);
        x += last.getDx();
        y += last.getDy();

        description = String.format("Moves to (%s,%s,%s) with conditions: %s",
                x, y, last.getMoveType().getCode(),
                conditions.stream().collect(Collectors.joining(",")));

    }



    @Override
    public String getDescription() {
        return description;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;

        SpecialCaseResolver that = (SpecialCaseResolver) o;

        return om.equals(that.om);
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + om.hashCode();
        return result;
    }
}
