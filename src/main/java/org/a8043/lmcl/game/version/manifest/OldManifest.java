package org.a8043.lmcl.game.version.manifest;

import cn.hutool.json.JSONObject;
import org.a8043.lmcl.game.launcher.LaunchOptions;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class OldManifest extends BasicManifest {
    @Override
    public List<String> buildArgs(JSONObject json, LaunchOptions options) {
        List<String> args = new ArrayList<>();

        String minecraftArguments = json.getStr("minecraftArguments");
        if (minecraftArguments == null || minecraftArguments.trim().isEmpty()) {
            return args;
        }

        Map<String, String> placeholders = buildPlaceholders(options, json);

        String[] tokens = minecraftArguments.trim().split("\\s+");
        for (String token : tokens) {
            if (!token.isEmpty()) {
                args.add(replacePlaceholders(token, placeholders));
            }
        }

        return args;
    }
}
