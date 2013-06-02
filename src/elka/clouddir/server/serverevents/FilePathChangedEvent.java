package elka.clouddir.server.serverevents;

import elka.clouddir.server.communication.ClientCommunicationThread;
import elka.clouddir.server.model.AbstractFileInfo;
import elka.clouddir.shared.RenameInfo;

/**
 * Ścieżka pliku zmieniona.
 * @author Michał Toporowski
 */
public class FilePathChangedEvent extends CommunicationEvent {
    private final RenameInfo renameInfo;

    public FilePathChangedEvent(ClientCommunicationThread senderThread, RenameInfo renameInfo) {
        super(senderThread);
        this.renameInfo = renameInfo;
    }

    public RenameInfo getRenameInfo() {
        return renameInfo;
    }
}
