package org.a8043.lmcl.game.version.manifest;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LibraryArtifact {
    private String path;
    private String sha1;
    private long size;
    private String url;
}
