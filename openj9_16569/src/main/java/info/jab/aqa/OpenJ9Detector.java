package info.jab.aqa;

public class OpenJ9Detector {

    public boolean detect() {
        String impl = System.getProperty("java.vm.name");
        System.out.println("System.getProperty('java.vm.name')=" + impl + "\n");
        impl = impl.toLowerCase();
        if (impl.contains("ibm")) {
            return true;
        } else if (impl.contains("openj9")) {
            return true;
        }
        return false;
    }
}
