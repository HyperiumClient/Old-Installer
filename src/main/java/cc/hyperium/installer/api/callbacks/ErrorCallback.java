package cc.hyperium.installer.api.callbacks;

/*
 * Created by Cubxity on 08/07/2018
 */
public class ErrorCallback extends AbstractCallback {
    private String message;
    private Exception error;
    private Phrase phrase;

    public ErrorCallback(Exception error, Phrase phrase, String message) {
        this.error = error;
        this.phrase = phrase;
        this.message = message;
    }

    public ErrorCallback(Exception error, Phrase phrase) {
        this(error, phrase, null);
    }

    @Override
    public String getMessage() {
        return message == null ? "Unexpected error: " + error.getMessage() + " in phrase " + phrase.toString() : message;
    }

    public Exception getError() {
        return error;
    }

    public Phrase getPhrase() {
        return phrase;
    }
}
