package elka.clouddir.server.serverevents;

import elka.clouddir.server.model.AbstractFileInfo;

/**
 * Klasa bazowa dla zdarzeń typu FileChangedEvent, FileDeletedEvent itp.
 */
public abstract class FileEvent extends ServerEvent{
    private final AbstractFileInfo metadata;

    protected FileEvent(AbstractFileInfo metadata) {
        this.metadata = metadata;
    }

    public AbstractFileInfo getMetadata() {
        return metadata;
    }
}
