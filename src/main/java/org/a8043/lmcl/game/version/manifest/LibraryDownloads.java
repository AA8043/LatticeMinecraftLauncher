package org.a8043.lmcl.game.version.manifest;

import lombok.Getter;
import lombok.Setter;

import java.util.Map;

@Getter
@Setter
public class LibraryDownloads {
    private LibraryArtifact artifact;
    private Map<String, LibraryArtifact> classifiers;
}
