package elka.clouddir.server.serverevents;

import elka.clouddir.server.model.AbstractFileInfo;

/**
 * Zmieniono plik
 */
public class FileChangedEvent extends FileEvent {
    public FileChangedEvent(AbstractFileInfo metadata) {
        super(metadata);
    }
}
