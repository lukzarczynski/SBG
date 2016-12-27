package main;

import java.util.*;
import java.util.stream.Collectors;

import main.tree.*;
import org.apache.commons.lang3.tuple.Pair;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;

/**
 * Created by lukasz on 25.12.16.
 */
public class PieceResolver {

    public static String resolve(Piece piece) throws PieceResolverException{

        Set<OneMove> movesToInterpret = piece.getMoves();
        Map<OneMove, List<Resolver>> asd = new HashMap<>();

        Resolvers.resolvers.forEach(r -> {
            boolean matches = r.matches(piece.getMoves());
            if (matches) {
                ResolveResult apply = r.apply(piece.getMoves());

                apply.getParsed().forEach(parsed -> {
                    asd.putIfAbsent(parsed, new ArrayList<>());
                    asd.get(parsed).add(r);
                });
            }
        });

        Set<OneMove> parsedMoves = asd.keySet();

        System.out.println();

        if(parsedMoves.size() == movesToInterpret.size()){
            return resolve2(piece, asd.values().stream().flatMap(Collection::stream).collect(Collectors.toList()));
        }
        throw new PieceResolverException(String.format("Could parse: %s out of %s", parsedMoves.size(), movesToInterpret.size()));
    }

    public static String resolve2(Piece piece, List<Resolver> resolvers) throws PieceResolverException {
        final Root root = new Root(piece);

        Set<Node> nodes = new HashSet<>();
        Set<Node> visited = new HashSet<>();
        nodes.add(root);
        int i = 0;
        Node leaf = null;
        while (i < nodes.size()) {
            Node n = nodes.stream()
                    .filter(c -> !visited.contains(c))
                    .filter(c -> !c.isLeaf())
                    .sorted(Comparator.comparingInt(a -> a.movesToInterpret.size()))
                    .findFirst().orElse(null);
            if (isNull(n)) {
                break;
            }

            visited.add(n);
            n.expandNode(resolvers, nonNull(leaf) ? leaf.getValue() : Long.MAX_VALUE);
            final Set<Node> children = n.children;
            nodes.addAll(children);


            leaf = nodes.stream()
                    .filter(Node::isLeaf)
                    .sorted(Comparator.comparing(Node::getValue))
                    .findFirst()
                    .orElse(null);

            Node bestMatch = nodes.stream()
                    .sorted((a, b) -> {
                        int compare = Integer.compare(a.movesToInterpret.size(), b.movesToInterpret.size());
                        if (compare == 0) {
                            return a.getValue().compareTo(b.getValue());
                        }
                        return compare;

                    })
//                    .sorted(Comparator.comparingInt(a -> a.movesToInterpret.size()))
                    .findFirst().orElse(root);

//            System.out.println(String.format("Best match: m: %s v: %s, nodes: %s, visited: %s",
//                    bestMatch.movesToInterpret.size(), bestMatch.getValue(),
//                    nodes.size(),
//                    visited.size()));
        }

        if (leaf == null) {
            Node bestMatch = nodes.stream()
                    .sorted(Comparator.comparingInt(a -> a.movesToInterpret.size()))
                    .findFirst().orElse(root);
            throw new PieceResolverException(getDescription(bestMatch));
        } else {
            return getDescription(leaf);
        }
    }

    private static String getDescription(Node leaf) {
        StringBuilder description = new StringBuilder();
        Node x = leaf;
        while (x.parent != null) {
            description.append(x.getDescription());
            x = x.parent;
        }

        return description.toString();
    }

}


