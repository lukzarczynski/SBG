package main;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Scanner;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;

/**
 * Created by lukasz on 26.11.16.
 */
public class Main {

    public static void main(String[] args) throws IOException {
        System.out.println("Scanning");

//        scanDirectory();

        System.out.println("Scanned");

        final File allMovesFile = new File("src/main/resources/allMoves.sbg1");

        final Set<Piece> pieces = loadPieces(allMovesFile);

        System.out.println("Loaded");

        StringBuilder parsed = new StringBuilder();
        StringBuilder failed = new StringBuilder();

        resolve(pieces, parsed, failed);

        File parsedFile = new File("src/main/resources/parsedMoves.sbg1");
        File failedFile = new File("src/main/resources/failedMoves.sbg1");

        FileOutputStream parsedFOS = new FileOutputStream(parsedFile, false);
        FileOutputStream failedFOS = new FileOutputStream(failedFile, false);

        parsedFOS.write(parsed.toString().getBytes());
        failedFOS.write(failed.toString().getBytes());

        System.out.println("Finished");
    }

    public static Set<Piece> loadPieces(File allMovesFile) throws FileNotFoundException {
        final Set<Piece> pieces = new HashSet<>();

        final Scanner scanner = new Scanner(allMovesFile);
        while (scanner.hasNextLine()) {
            final String pieceString = scanner.nextLine();
            if (!pieceString.startsWith("//")) {
                pieces.add(Piece.parse(pieceString));
            }
        }
        return pieces;
    }

    public static void resolve(Set<Piece> pieces, StringBuilder parsed, StringBuilder failed) {
        final AtomicInteger index = new AtomicInteger(0);

        ExecutorService es = Executors.newFixedThreadPool(10);

        final List<Pair<Piece, ? extends Future<?>>> collect = pieces.stream()
                .map(pa -> Pair.of(pa, es.submit(() -> {
                    try {
                        final String resolve = PieceResolver.resolve(pa);
                        parsed.append("//").append(resolve).append("\n");
                        parsed.append(pa.toString()).append("\n");

//                        System.out.println(String.format("%s: Parsed %s\n%s%s",
//                                Thread.currentThread().getName(),
//                                index.getAndIncrement(),
//                                resolve, pa.toString()));
                    } catch (PieceResolverException e) {
                        failed.append(pa.toString()).append("\n");

//                        System.out.println(e.getMessage());
//                        System.out.println(String.format("%s: Failed %s",
//                                Thread.currentThread().getName(),
//                                index.getAndIncrement()));
                    }

                }))).collect(Collectors.toList());

        collect.forEach(f -> {
            try {
                f.getRight().get(1, TimeUnit.SECONDS);
            } catch (InterruptedException | ExecutionException | TimeoutException e) {
                failed.append(f.getLeft().toString()).append("\n");
            }
        });


        System.out.println("Resolved");
    }

    private static void scanDirectory() throws IOException {
        final File dir = new File("/home/lukasz/Documents/mgr/xxxya/ggp_sbgRAPP/Fitness^1");

        final Iterator<File> fileIterator = FileUtils.iterateFiles(dir, new String[]{"sbg"}, true);

        final File allMovesFile = new File("allMoves.sbg1");

        FileOutputStream allMovesOS = new FileOutputStream(allMovesFile, false);

        final List<String> pieces = new ArrayList<>();
        fileIterator
                .forEachRemaining(f -> {
                    Scanner scanner = null;
                    try {
                        scanner = new Scanner(f);
                    } catch (FileNotFoundException ignored) {
                        throw new RuntimeException();
                    }

                    Boolean p = false;
                    StringBuilder sb = new StringBuilder();
                    while (scanner.hasNext()) {
                        final String line = scanner.nextLine();

                        if (StringUtils.isBlank(line)) {
                            continue;
                        }
                        if (line.contains("<--PIECES-->")) {
                            p = true;
                            continue;
                        } else if (line.contains("<--GOALS-->")) {
                            p = false;
                            continue;
                        }

                        if (p && sb.length() == 0) {
                            sb.append(f.getParent()).append(f.getName());
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


        final Set<Piece> collect = pieces.stream().map(Piece::parse).collect(Collectors.toSet());

        final StringBuilder builder = new StringBuilder();
        collect.stream().map(Piece::toString).forEach(ps -> {
            builder.append(ps);
            builder.append("\n");
        });
        allMovesOS.write(builder.toString().getBytes());

    }

}
