package org.a8043.lmcl.game.directory;

public interface DirectoryType<D extends Directory, I extends DirectoryCreateInfo> {
    String getTitle();

    D createDirectory(I info) throws Exception;
}
