package main;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.Scanner;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import main.tree.Node;
import main.tree.Root;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;

import static java.util.Objects.isNull;

/**
 * Created by lukasz on 26.11.16.
 */
public class Main {

    public static void main(String[] args) throws FileNotFoundException {

        final File dir = new File("/home/lukasz/Documents/mgr/xxxya/ggp_sbgRAPP/Fitness^1/Generation");

        final Iterator<File> fileIterator = FileUtils.iterateFiles(dir, new String[]{"sbg"}, true);

        final List<String> pieces = new ArrayList<>();
        fileIterator.forEachRemaining(f -> {
            System.out.println("Reading file " + f.getName());
            Scanner scanner = null;
            try {
                scanner = new Scanner(f);
            } catch (FileNotFoundException e) {
                throw new RuntimeException("DUPA");
            }

            Boolean p = false;
            StringBuilder sb = new StringBuilder();
            while (scanner.hasNext()) {
                final String line = scanner.nextLine();

                if (StringUtils.isBlank(line)) {
                    continue;
                }
//            System.out.println(line);
                if (line.contains("<--PIECES-->")) {
                    p = true;
                    continue;
                } else if (line.contains("<--GOALS-->")) {
                    p = false;
                    continue;
                }

                if (p && sb.length() == 0) {
                    sb.append(f.getName() + f.getParent());
                }

                if (p && !line.startsWith("/")) {
                    sb.append(line);
                    if (line.contains("&")) {
                        pieces.add(sb.toString());
                        sb = new StringBuilder();
                    }
                }
            }
        });


//        pieces.forEach(System.out::println);

        final List<Piece> collect = pieces.stream().map(Piece::parse).collect(Collectors.toList());

        System.out.println("PIECES: ");
        collect.stream().map(Piece::toString).forEach(System.out::println);
        System.out.println("PIECES END");


        final AtomicInteger failed = new AtomicInteger(0);

        collect.parallelStream().forEach(pa -> {
            Root root = new Root(pa);

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
//                System.out.println("Did not find matching");
                failed.getAndIncrement();
            } else {

                Node x = leaf;

                System.out.println("");
                System.out.println("");
                System.out.println("YEAH FOUND IT!");
                System.out.println("Piece: " + pa.toString());
                while (x.parent != null) {
                    System.out.println(x.getDescription());
                    x = x.parent;
                }
            }
        });

        System.out.println(String.format("Found: %s, failed: %s", collect.size() - failed.intValue(), failed.intValue()));

//            final Optional<PieceType> any = PieceType.PIECES.stream()
//                    .filter(x -> !x.getPiece().intersection(pa.getMoves()).isEmpty())
//                    .findAny();
//            if (any.isPresent()) {
//                System.out.println(String.format("Found match: %s : %s", pa.getName(), any.get().getDescription()));
//                System.out.println(String.format("main.Piece %s", join(pa.getMoves(), " + ")));
//                System.out.println(String.format("Intersection in: %s", join(
//                        any.get().getPiece().intersection(pa.getMoves()), " + ")));
//                System.out.println(String.format("Diff is %s", any.get().getPiece().subtract(pa.getMoves())
//                        .stream().map(Object::toString).collect(Collectors.joining(" + "))));
//                System.out.println(String.format("Diff is %s", pa
//                        .subtract(any.get().getPiece().getMoves())
//                        .stream().map(Object::toString).collect(Collectors.joining(" + "))));
//            }
//        });

    }

    public static String join(Collection<? extends Object> objects, String delimiter) {
        return objects.stream().map(Object::toString).collect(Collectors.joining(delimiter));
    }

}
