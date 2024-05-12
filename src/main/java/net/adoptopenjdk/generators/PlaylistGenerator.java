package net.adoptopenjdk.generators;

import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class PlaylistGenerator {

    public static void main(String... args) {
        BiFunction<String, String, String> decorate = (template, param) -> template.replace("PARAM", param);

        Function<Path, String> loadContent = param -> {
            try {
                return new String(Files.readAllBytes(param), "UTF-8");
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        };

        Predicate<Path> validateJCStressTestAnnotation = param -> loadContent.apply(param).contains("@JCStressTest");

        final String headerTemplateBlock = loadContent.apply(Paths.get("./templates/header.txt"));
        final String footerTemplateBlock = loadContent.apply(Paths.get("./templates/footer.txt"));
        final String testTemplateBlock = loadContent.apply(Paths.get("./templates/test.txt"));

        Function<List<String>, String> generatePlaylistContents = param -> {
            return param
                .stream()
                .flatMap(path -> {
                    try {
                        return Files.walk(Paths.get(path));
                    } catch (IOException e) {
                        throw new RuntimeException("Katakroker");
                    }
                })
                .filter(Files::isRegularFile)
                .filter(validateJCStressTestAnnotation)
                .map(Path::getFileName)
                .map(String::valueOf)
                .map(s -> s.replace(".java", ""))
                .map(s -> decorate.apply(testTemplateBlock, s))
                .collect(Collectors.joining());
        };

        Function<String, String> writePlaylist = body -> {
            final String fileName = "playlist.xml";
            try {
                FileWriter myWriter = new FileWriter(fileName);
                myWriter.write(headerTemplateBlock);
                myWriter.write(body);
                myWriter.write(footerTemplateBlock);
                myWriter.close();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

            return "";
        };

        final List<String> paths = List.of(
            "./jcstress/tests-custom/src/main/java/org/openjdk/jcstress/tests",
            "./jcstress/jcstress-samples/src/main/java/org/openjdk/jcstress/samples"
        );

        generatePlaylistContents.andThen(writePlaylist).apply(paths);
    }
}
