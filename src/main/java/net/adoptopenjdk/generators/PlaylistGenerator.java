package net.adoptopenjdk.generators;

import com.github.lalyos.jfiglet.FigletFont;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class PlaylistGenerator {

    public static void main(String... args) {
        Consumer<String> showHeader = param -> {
            try {
                String asciiArt = FigletFont.convertOneLine(param);
                System.out.println(asciiArt);
            } catch (Exception e) {
                //Empty on purpose
            }
        };

        BiFunction<String, String, String> decorate = (template, param) -> template.replace("PARAM", param);

        Function<Path, String> loadContent = param -> {
            try {
                return new String(Files.readAllBytes(param), "UTF-8");
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        };

        Function<String, String> loadTemplate = param -> {
            try {
                ClassLoader classloader = Thread.currentThread().getContextClassLoader();
                File file = new File(classloader.getResource(param).getFile());
                return new String(Files.readAllBytes(file.toPath()), StandardCharsets.UTF_8);
            } catch (IOException e) {
                throw new RuntimeException(e.getLocalizedMessage(), e);
            }
        };

        Predicate<Path> validateJCStressTestAnnotation = param -> loadContent.apply(param).contains("@JCStressTest");

        Function<Path, String> transformIntoPlaylistTestformat = param -> {
            final String testTemplateBlock = loadTemplate.apply("templates/test.txt");

            return Stream
                .of(param)
                .map(Path::getFileName)
                .map(String::valueOf)
                .map(s -> s.replace(".java", ""))
                .map(s -> decorate.apply(testTemplateBlock, s))
                .findFirst()
                .orElse(null);
        };

        Function<List<String>, String> generatePlaylist = param -> {
            showHeader.accept("AQA - Tests");

            final String body = param
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
                .peek(System.out::println)
                .map(transformIntoPlaylistTestformat)
                .collect(Collectors.joining());

            final String headerTemplateBlock = loadTemplate.apply("./templates/header.txt");
            final String footerTemplateBlock = loadTemplate.apply("./templates/footer.txt");

            StringBuilder sb = new StringBuilder();
            sb.append(headerTemplateBlock);
            sb.append(body);
            sb.append(footerTemplateBlock);
            return sb.toString();
        };

        Function<String, String> write = body -> {
            final String fileName = "playlist.xml";
            try {
                FileWriter myWriter = new FileWriter(fileName);
                myWriter.write(body);
                myWriter.close();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

            System.out.println("playlist generated for jcstress tests.");
            return "";
        };

        final List<String> paths = List.of(
            "./jcstress/tests-custom/src/main/java/org/openjdk/jcstress/tests",
            "./jcstress/jcstress-samples/src/main/java/org/openjdk/jcstress/samples"
        );

        generatePlaylist.andThen(write).apply(paths);
    }
}
