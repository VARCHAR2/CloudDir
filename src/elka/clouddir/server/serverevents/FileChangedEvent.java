package elka.clouddir.server.serverevents;

import elka.clouddir.server.model.AbstractFileInfo;

/**
 * Zmieniono plik
 */
public class FileChangedEvent extends ServerEvent {
    private final AbstractFileInfo metadata;

    public FileChangedEvent(AbstractFileInfo metadata) {
        this.metadata = metadata;
    }

    public AbstractFileInfo getMetadata() {
        return metadata;
    }
}
