package main.tree;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import main.operator.Backwards;
import main.operator.Diagonal;
import main.operator.Forward;
import main.operator.Horizontal;
import main.operator.Left;
import main.operator.None;
import main.operator.OnlyCapture;
import main.operator.Operator;
import main.operator.Orthogonal;
import main.operator.OverEnemyPieceInstead;
import main.operator.RestrictedToOne;
import main.operator.Right;
import main.operator.WithoutCapture;
import main.piececlass.XYLeaper;
import main.piececlass.XYRider;

/**
 * Created by lukasz on 10.12.16.
 */
public final class Resolvers {

    public static final List<Resolver> resolvers;
    public static final List<Operator[]> ops;

    static {
        ops = newArrayList(
                new Operator[]{None.instance(0)},
                new Operator[]{Forward.instance(1)},
                new Operator[]{Backwards.instance(1)},
                new Operator[]{Left.instance(1)},
                new Operator[]{Right.instance(1)},
                new Operator[]{Diagonal.instance(2)},
                new Operator[]{Horizontal.instance(2)},
                new Operator[]{Orthogonal.instance(2)},
                new Operator[]{OnlyCapture.instance(3)},
                new Operator[]{WithoutCapture.instance(3)},
                new Operator[]{RestrictedToOne.instance(6)},
                new Operator[]{OverEnemyPieceInstead.instance(8)}
        );
        final int singleOps = ops.size();
        for (int i = 1; i < singleOps; i++) {
            Operator o1 = ops.get(i)[0];
            for (int j = i + 1; j < singleOps; j++) {
                Operator o2 = ops.get(j)[0];
                ops.add(new Operator[]{o1, o2});
            }
        }

        final List<XYRider> riders = Arrays.asList(new XYRider(1, 1), new XYRider(0, 1));
        final List<XYLeaper> leapers = Arrays.asList(new XYLeaper(2, 1),
                                                     new XYLeaper(3, 1),
                                                     new XYLeaper(2, 2),
                                                     new XYLeaper(1, 1),
                                                     new XYLeaper(2, 0),
                                                     new XYLeaper(3, 0)
        );


        resolvers = Stream.concat(
                Stream.concat(riders.stream(), leapers.stream())
                        .map(pc -> ops.stream().map(o -> new Resolver(pc, o)))
                        .flatMap(Function.identity()),
                ops.stream().map(FirstMoveResolver::new))
                .filter(Resolver::isValid)
                .sorted(Comparator.comparing(Resolver::getPriority))
                .collect(Collectors.toList());
    }

    private static List<Operator[]> newArrayList(Operator[]... ops) {
        List<Operator[]> l = new ArrayList<>();
        Collections.addAll(l, ops);
        return l;
    }
}
