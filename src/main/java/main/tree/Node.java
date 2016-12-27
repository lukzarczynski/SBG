package main.tree;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import main.MoveUtil;
import main.OneMove;
import main.Piece;

/**
 * Created by lukasz on 06.12.16.
 */
public class Node {

    public final Long value;

    public final Set<Node> children = new HashSet<>();
    public final Node parent;
    public final Piece piece;
    public final Set<OneMove> movesToInterpret;
    public final boolean leaf;
    public final String description;
    public Resolver resolver;
    public final Set<Resolver> usedResolvers;

    Node(Piece piece) {
        this.piece = piece;
        this.movesToInterpret = piece.getMoves();
        this.leaf = movesToInterpret.isEmpty();
        this.description = "Root";
        parent = null;
        this.value = 0L;
        usedResolvers = new HashSet<>();
    }

    public Node(Node parent, Resolver resolver) {
        this.resolver = resolver;
        this.parent = parent;
        this.piece = parent.piece;
        final boolean match = resolver.matches(parent.piece.getMoves());
        if (match) {
            ResolveResult apply = resolver.apply(parent.piece.getMoves());
            this.movesToInterpret = MoveUtil.subtract(this.parent.movesToInterpret, apply.getParsed());
        } else {
            this.movesToInterpret = parent.movesToInterpret;
        }
        this.leaf = this.movesToInterpret.isEmpty();
        this.description = resolver.getDescription();
        this.value = parent.value + resolver.getPriority();
        usedResolvers = new HashSet<>();
        usedResolvers.addAll(parent.usedResolvers);
        usedResolvers.add(resolver);
    }

    public boolean expandNode(List<Resolver> resolvers, long maxValue) {
        List<Node> collect = resolvers.stream()
                .filter(r -> !usedResolvers.contains(r))
                .map(r -> new Node(this, r))
                .filter(n -> n.movesToInterpret.size() < this.movesToInterpret.size())
                .filter(n -> n.getValue() < maxValue)
                .collect(Collectors.toList());

        collect.forEach(children::add);

        return collect.isEmpty();
    }

    public String getDescription() {
        return description;
    }

    public boolean isLeaf() {
        return leaf;
    }

    public Long getValue() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Node node = (Node) o;

        return usedResolvers.equals(node.usedResolvers);
    }

    @Override
    public int hashCode() {
        return usedResolvers.hashCode();
    }
}
