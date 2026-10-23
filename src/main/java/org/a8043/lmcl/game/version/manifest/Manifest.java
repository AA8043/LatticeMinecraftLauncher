package org.a8043.lmcl.game.version.manifest;

import cn.hutool.json.JSONObject;

import java.util.List;

public abstract class Manifest {
    public abstract String getId(JSONObject json);

    public abstract String getMainClass(JSONObject json);

    public abstract List<Library> getLibraries(JSONObject json);

    public abstract AssetIndex getAssetIndex(JSONObject json);

    public abstract int getJavaVersion(JSONObject json);

    public abstract List<String> buildArgs(JSONObject json,
                                           String userName, String versionName, String gameDir,
                                           String assetsDir, String assetsIndexName, String uuid,
                                           String accessToken, String clientId, String xuid,
                                           String userType, String versionType);
}
