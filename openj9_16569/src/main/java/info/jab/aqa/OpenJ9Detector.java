package info.jab.aqa;

public class OpenJ9Detector {

    public boolean detect() {
        String jvmName = System.getProperty("java.vm.name");
        return jvmName.contains("OpenJ9");
    }

}
