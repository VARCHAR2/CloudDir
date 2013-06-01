package elka.clouddir.shared;

import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.List;

import elka.clouddir.server.model.SharedEmptyFolder;
import elka.clouddir.server.model.SharedFile;

public class FileVisitor extends SimpleFileVisitor<Path>{
	List<SharedFile> files;
	List<SharedEmptyFolder> folders;
	Path lastVisitedDir;
	


	public FileVisitor(List<SharedFile> files, List<SharedEmptyFolder> folders) {
		super();
		this.files = files;
		this.folders = folders;
		this.lastVisitedDir = null;
	}

	@Override
	public FileVisitResult postVisitDirectory(Path dir, IOException exc)
			throws IOException {
		// check if the directory was empty
		if(lastVisitedDir != null){
			if(lastVisitedDir.compareTo(dir) == 0){
//				String relativePath = dir.toString();
//				long modified = Files.getLastModifiedTime(dir, LinkOption.NOFOLLOW_LINKS).toMillis();
//				String lastModifiedBy;
				//TODO add dir to folders
				//SharedEmptyFolder folder = new SharedEmptyFolder(dir, modified, lastModifiedBy)
			}
		}
		return FileVisitResult.CONTINUE;
	}

	@Override
	public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs)
			throws IOException {
		lastVisitedDir = dir;
		return FileVisitResult.CONTINUE;
	}

	@Override
	public FileVisitResult visitFile(Path dir, BasicFileAttributes attrs)
			throws IOException {
		lastVisitedDir = dir;
		//TODO add file to list
		return FileVisitResult.CONTINUE;
	}

	@Override
	public FileVisitResult visitFileFailed(Path dir, IOException exc)
			throws IOException {
		return FileVisitResult.CONTINUE;
	}

}
