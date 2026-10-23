package org.a8043.lmcl.game.version.manifest;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class OsInfo {
    private final String name;
    private final String arch;
    private final String version;
}
