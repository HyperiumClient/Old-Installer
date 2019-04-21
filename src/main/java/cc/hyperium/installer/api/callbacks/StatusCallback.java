package cc.hyperium.installer.api.callbacks;

public class StatusCallback extends AbstractCallback {
    private final Phrase phrase;
    private final String message;
    private final Object object;

    public StatusCallback(Phrase phrase, String message, Object object) {
        this.phrase = phrase;
        this.message = message;
        this.object = object;
    }

    @Override
    public String getMessage() {
        return message;
    }

    public Phrase getPhrase() {
        return phrase;
    }

    public Object getObject() {
        return object;
    }
}
