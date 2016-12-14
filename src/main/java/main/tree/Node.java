package main.tree;

import java.util.HashSet;
import java.util.Set;
import main.OneMove;
import main.Piece;

/**
 * Created by lukasz on 06.12.16.
 */
public class Node {

    public final Set<Node> children = new HashSet<>();
    public final Node parent;
    public final Piece piece;
    public final Set<OneMove> movesToInterpret;
    public final boolean leaf;
    public final boolean and = false;
    public final String description;
    public Resolver resolver;

    protected Node(Piece piece, Set<OneMove> movesToInterpret) {
        this.piece = piece;
        this.movesToInterpret = movesToInterpret;
        this.leaf = movesToInterpret.isEmpty();
        this.description = "Root";
        parent = null;
    }

    public Node(Node parent, Resolver resolver) {
        this.resolver = resolver;
        this.parent = parent;
        this.piece = parent.piece;
        final boolean match = resolver.matches(parent.movesToInterpret);
        this.movesToInterpret = match ? resolver.apply(parent.movesToInterpret) : parent.movesToInterpret;
        this.leaf = this.movesToInterpret.isEmpty();
        this.description = resolver.getDescription();
    }

    public void expandNode() {
        Resolvers.resolvers.stream()
                .map(r -> new Node(this, r))
                .filter(n -> n.movesToInterpret.size() < this.movesToInterpret.size())
                .forEach(children::add);
    }


    public String getDescription() {
        return description;
    }

    public boolean isLeaf() {
        return leaf;
    }
}
