package org.a8043.lmcl.game.version.manifest;

import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import org.a8043.lmcl.game.launcher.LaunchOptions;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class NewManifest extends BasicManifest {
    @Override
    public List<String> buildArgs(JSONObject json, LaunchOptions options) {
        List<String> args = new ArrayList<>();

        JSONObject arguments = json.getJSONObject("arguments");
        if (arguments == null) {
            return args;
        }

        JSONArray gameArgs = arguments.getJSONArray("game");
        if (gameArgs == null) {
            return args;
        }

        Map<String, String> placeholders = buildPlaceholders(options, json);
        OsInfo currentOs = currentOs();
        Map<String, Boolean> features = options.getFeatures();

        for (Object item : gameArgs) {
            if (item instanceof String) {
                args.add(replacePlaceholders((String) item, placeholders));
                continue;
            }

            if (item instanceof JSONObject argObject) {
                List<Rule> rules = parseRules(argObject.getJSONArray("rules"));

                if (!isAllowed(rules, currentOs, features)) {
                    continue;
                }

                args.addAll(parseArgumentValue(argObject.get("value"), placeholders));
            }
        }

        return args;
    }
}
