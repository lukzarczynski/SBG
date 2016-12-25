package main;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import main.tree.Node;
import main.tree.Root;

import static java.util.Objects.isNull;

/**
 * Created by lukasz on 25.12.16.
 */
public class PieceResolver {

    public static String resolve(Piece piece) throws PieceResolverException {
        final Root root = new Root(piece);

        List<Node> nodes = new ArrayList<>();
        nodes.add(root);
        int i = 0;

        Node leaf = null;
        while (i < nodes.size()) {
            Node n = nodes.get(i++);
            n.expandNode();
            final Set<Node> children = n.children;
            final Optional<Node> anyLeaf = children.stream()
                    .filter(Node::isLeaf)
                    .sorted((n1, n2) -> {
                        if (isNull(n1.resolver) || isNull(n2.resolver)) {
                            return 0;
                        }

                        return Integer.compare(n1.resolver.getPriority(), n2.resolver.getPriority());
                    })
                    .findFirst();
            if (anyLeaf.isPresent()) {
                leaf = anyLeaf.get();
                break;
            }
            nodes.addAll(children);
        }

        if (leaf == null) {
            throw new PieceResolverException();
        } else {

            StringBuilder description = new StringBuilder();
            Node x = leaf;
            while (x.parent != null) {
                description.append(x.getDescription());
                x = x.parent;
            }

            return description.toString();
        }
    }

}


class PieceResolverException extends Exception {

}
