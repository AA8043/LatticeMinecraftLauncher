package org.a8043.lmcl.game.version.manifest;

import cn.hutool.json.JSONObject;
import org.a8043.lmcl.game.launcher.LaunchOptions;

import java.util.List;

public abstract class Manifest {
    public abstract String getId(JSONObject json);

    public abstract String getMainClass(JSONObject json);

    public abstract List<Library> getLibraries(JSONObject json);

    public abstract AssetIndex getAssetIndex(JSONObject json);

    public abstract int getJavaVersion(JSONObject json);

    public abstract List<String> buildArgs(JSONObject json, LaunchOptions options);
}
