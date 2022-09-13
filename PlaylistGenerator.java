///usr/bin/env jbang "$0" "$@" ; exit $?

import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class PlaylistGenerator {

    public static void main(String... args) throws IOException {

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

        final String path = "./jcstress/tests-custom/src/main/java/org/openjdk/jcstress/tests";
        final String body = Files.walk(Paths.get(path))
                .filter(Files::isRegularFile)
                .filter(validateJCStressTestAnnotation)
                .map(Path::getFileName)
                .map(String::valueOf)
                .map(s -> s.replace(".java", ""))
                .map(s -> decorate.apply(testTemplateBlock, s))
                .collect(Collectors.joining());

        final String fileName = "playlist.xml";
        FileWriter myWriter = new FileWriter(fileName);
        myWriter.write(headerTemplateBlock);
        myWriter.write(body);
        myWriter.write(footerTemplateBlock);
        myWriter.close();
    }
}

