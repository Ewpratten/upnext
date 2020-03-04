package ca.retrylife.simplelogger;

public class SimpleLogger {

    public static void log(String component, String message) {
        System.out.printf("[%s] %s%n", component, message);
    }
}