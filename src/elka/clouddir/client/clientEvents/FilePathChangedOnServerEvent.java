package elka.clouddir.client.clientEvents;

import elka.clouddir.shared.RenameInfo;

public class FilePathChangedOnServerEvent extends ClientEvent {
	
    private final RenameInfo renameInfo;

    public FilePathChangedOnServerEvent(RenameInfo renameInfo) {
        this.renameInfo = renameInfo;
    }

    public RenameInfo getRenameInfo() {
        return renameInfo;
    }
    
}
