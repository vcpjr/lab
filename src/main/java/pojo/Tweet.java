package pojo;

public class Tweet {
    private String message;

    public Tweet(String message) {
        this.message = message;
    }

    public final String getMessage() {
        return message;
    }
}
