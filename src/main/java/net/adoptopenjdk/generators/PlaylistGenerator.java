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
            } catch (IOException e) {
                throw new RuntimeException(e.getLocalizedMessage(), e);
            }
        };

        BiFunction<String, String, String> decorate = (template, param) -> template.replace("PARAM", param);

        Function<Path, String> loadFileFromGitModule = param -> {
            try {
                return new String(Files.readAllBytes(param), StandardCharsets.UTF_8);
            } catch (IOException e) {
                throw new RuntimeException(e.getLocalizedMessage(), e);
            }
        };

        Function<String, String> loadTemplateFromResources = param -> {
            try {
                ClassLoader classloader = Thread.currentThread().getContextClassLoader();
                File file = new File(classloader.getResource(param).getFile());
                return new String(Files.readAllBytes(file.toPath()), StandardCharsets.UTF_8);
            } catch (IOException e) {
                throw new RuntimeException(e.getLocalizedMessage(), e);
            }
        };

        Function<String, Stream<Path>> getFilesFromPath = param -> {
            try {
                return Files.walk(Paths.get(param));
            } catch (IOException e) {
                throw new RuntimeException(e.getLocalizedMessage(), e);
            }
        };

        Predicate<Path> containsAJCStressTestAnnotation = param -> loadFileFromGitModule.apply(param).contains("@JCStressTest");

        Function<Path, String> transformIntoPlaylistTestformat = param -> {
            final String testTemplateBlock = loadTemplateFromResources.apply("templates/test.txt");

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
            final String body = param
                .stream()
                .flatMap(getFilesFromPath)
                .filter(Files::isRegularFile)
                .filter(containsAJCStressTestAnnotation)
                .peek(System.out::println)
                .map(transformIntoPlaylistTestformat)
                .collect(Collectors.joining());

            final String headerTemplateBlock = loadTemplateFromResources.apply("./templates/header.txt");
            final String footerTemplateBlock = loadTemplateFromResources.apply("./templates/footer.txt");

            StringBuilder sb = new StringBuilder();
            sb.append(headerTemplateBlock);
            sb.append(body);
            sb.append(footerTemplateBlock);
            return sb.toString();
        };

        Function<String, String> write = body -> {
            final String fileName = "playlist.xml";
            try {
                FileWriter writer = new FileWriter(fileName);
                writer.write(body);
                writer.close();
            } catch (IOException e) {
                throw new RuntimeException(e.getLocalizedMessage(), e);
            }

            System.out.println("playlist generated for jcstress tests.");
            return "";
        };

        //Execution

        final List<String> paths = List.of(
            "./jcstress/tests-custom/src/main/java/org/openjdk/jcstress/tests",
            "./jcstress/jcstress-samples/src/main/java/org/openjdk/jcstress/samples"
        );
        showHeader.accept("AQA - Tests");
        generatePlaylist.andThen(write).apply(paths);
    }
}
