package net.adoptopenjdk.generators;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

import java.io.File;
import java.util.List;
import javax.xml.XMLConstants;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;
import org.junit.jupiter.api.Test;

class PlaylistValidatorTests {

    @Test
    void shouldBeAValidPlaylist() {
        //Given
        List<String> paths = List.of("./jcstress/tests-custom/src/main/java/org/openjdk/jcstress/tests");
        String xmlFilePath = "./playlist.xml";
        String xsdFilePath = "./src/main/schemas/playlist.xsd";

        //When
        PlaylistGenerator playlistGenerator = new PlaylistGenerator();
        playlistGenerator.generate(paths);

        boolean result = false;
        try {
            SchemaFactory factory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
            Schema schema = factory.newSchema(new File(xsdFilePath));
            Validator validator = schema.newValidator();
            validator.validate(new StreamSource(new File(xmlFilePath)));
            result = true;
        } catch (Exception e) {
            e.printStackTrace();
            result = false;
        }

        //Then
        assertThat(result).isTrue();
    }
}
