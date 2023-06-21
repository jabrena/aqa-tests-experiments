package info.jab.aqa;

import static org.assertj.core.api.Assertions.assertThat;
import org.junit.jupiter.api.Test;

public class OpenJ9DetectorTests {

    @Test
    public void shouldDetect() {

        OpenJ9Detector openJ9Detector = new OpenJ9Detector();

        assertThat(openJ9Detector.detect()).isTrue();
    }
}
