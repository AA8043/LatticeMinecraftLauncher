package org.a8043.lmcl.game.directory;

import org.a8043.lmcl.game.WithInfo;
import org.a8043.lmcl.game.version.Version;

import java.util.List;

public abstract class Directory implements WithInfo {
    public abstract List<Version> getVersions() throws Exception;
}
