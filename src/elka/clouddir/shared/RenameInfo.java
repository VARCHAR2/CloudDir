package elka.clouddir.shared;

import java.io.Serializable;

/**
 * File rename data.
 */
public class RenameInfo implements Serializable {
    private final String oldPath;
    private final String newPath;

    public RenameInfo(String oldPath, String newPath) {
        this.oldPath = oldPath;
        this.newPath = newPath;
    }

    public String getOldPath() {
        return oldPath;
    }

    public String getNewPath() {
        return newPath;
    }
}
