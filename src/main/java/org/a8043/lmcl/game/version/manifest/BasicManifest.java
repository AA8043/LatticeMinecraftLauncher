package org.a8043.lmcl.game.version.manifest;

import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import org.a8043.lmcl.game.launcher.LaunchOptions;
import org.a8043.lmcl.game.user.AccountData;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public abstract class BasicManifest extends Manifest {
    protected static final Pattern PLACEHOLDER = Pattern.compile("\\$\\{([^}]+)}");

    @Override
    public String getId(JSONObject json) {
        return json.getStr("id");
    }

    @Override
    public int getJavaVersion(JSONObject json) {
        return json.getJSONObject("javaVersion").getInt("majorVersion");
    }

    @Override
    public String getMainClass(JSONObject json) {
        return json.getStr("mainClass");
    }

    @Override
    public List<Library> getLibraries(JSONObject json) {
        List<Library> libraries = new ArrayList<>();
        JSONArray arr = json.getJSONArray("libraries");
        if (arr == null) {
            return libraries;
        }

        for (int i = 0; i < arr.size(); i++) {
            JSONObject o = arr.getJSONObject(i);
            if (o == null) {
                continue;
            }

            Library library = new Library();
            library.setName(o.getStr("name"));

            JSONObject downloadsJson = o.getJSONObject("downloads");
            if (downloadsJson != null) {
                LibraryDownloads downloads = new LibraryDownloads();

                JSONObject artifactJson = downloadsJson.getJSONObject("artifact");
                if (artifactJson != null) {
                    downloads.setArtifact(parseArtifact(artifactJson));
                }

                JSONObject classifiersJson = downloadsJson.getJSONObject("classifiers");
                if (classifiersJson != null) {
                    Map<String, LibraryArtifact> classifiers = new LinkedHashMap<>();
                    for (String key : classifiersJson.keySet()) {
                        classifiers.put(key, parseArtifact(classifiersJson.getJSONObject(key)));
                    }
                    downloads.setClassifiers(classifiers);
                }

                library.setDownloads(downloads);
            }

            JSONObject nativesJson = o.getJSONObject("natives");
            if (nativesJson != null) {
                Map<String, String> natives = new LinkedHashMap<>();
                for (String key : nativesJson.keySet()) {
                    natives.put(key, nativesJson.getStr(key));
                }
                library.setNatives(natives);
            }

            library.setRules(parseRules(o.getJSONArray("rules")));

            JSONObject extractJson = o.getJSONObject("extract");
            if (extractJson != null) {
                Extract extract = new Extract();
                JSONArray exclude = extractJson.getJSONArray("exclude");
                if (exclude != null) {
                    List<String> excludeList = new ArrayList<>();
                    for (int j = 0; j < exclude.size(); j++) {
                        excludeList.add(exclude.getStr(j));
                    }
                    extract.setExclude(excludeList);
                }
                library.setExtract(extract);
            }

            libraries.add(library);
        }

        return libraries;
    }

    @Override
    public AssetIndex getAssetIndex(JSONObject json) {
        JSONObject assetIndexJson = json.getJSONObject("assetIndex");
        if (assetIndexJson == null) {
            return null;
        }

        return new AssetIndex(
            assetIndexJson.getStr("id"),
            assetIndexJson.getStr("sha1"),
            assetIndexJson.getLong("size"),
            assetIndexJson.getLong("totalSize")
        );
    }

    protected LibraryArtifact parseArtifact(JSONObject o) {
        if (o == null) {
            return null;
        }
        LibraryArtifact artifact = new LibraryArtifact();
        artifact.setPath(o.getStr("path"));
        artifact.setSha1(o.getStr("sha1"));
        Long size = o.getLong("size");
        artifact.setSize(size == null ? 0L : size);
        artifact.setUrl(o.getStr("url"));
        return artifact;
    }

    protected List<Rule> parseRules(JSONArray arr) {
        if (arr == null) {
            return null;
        }

        List<Rule> rules = new ArrayList<>();
        for (int i = 0; i < arr.size(); i++) {
            JSONObject o = arr.getJSONObject(i);
            if (o == null) {
                continue;
            }

            Rule rule = new Rule();
            rule.setAction(o.getStr("action"));

            JSONObject osJson = o.getJSONObject("os");
            if (osJson != null) {
                Rule.Os os = new Rule.Os();
                os.setName(osJson.getStr("name"));
                os.setArch(osJson.getStr("arch"));
                os.setVersion(osJson.getStr("version"));
                rule.setOs(os);
            }

            JSONObject featuresJson = o.getJSONObject("features");
            if (featuresJson != null) {
                Map<String, Boolean> features = new LinkedHashMap<>();
                for (String key : featuresJson.keySet()) {
                    features.put(key, Boolean.TRUE.equals(featuresJson.getBool(key)));
                }
                rule.setFeatures(features);
            }

            rules.add(rule);
        }
        return rules;
    }

    protected final boolean isAllowed(List<Rule> rules, OsInfo currentOs, Map<String, Boolean> features) {
        if (rules == null || rules.isEmpty()) {
            return true;
        }

        boolean allowed = false;
        for (Rule rule : rules) {
            if (rule.matches(currentOs, features)) {
                allowed = Objects.equals(rule.getAction(), "allow");
            }
        }
        return allowed;
    }

    protected final Map<String, String> buildPlaceholders(LaunchOptions options, JSONObject json) {
        Map<String, String> placeholders = new LinkedHashMap<>();

        AccountData accountData = options.getAccountData();
        put(placeholders, "auth_player_name", accountData.getUsername());
        put(placeholders, "auth_uuid", accountData.getUuid());
        put(placeholders, "auth_access_token", accountData.getAccessToken());
        put(placeholders, "clientid", "");// TODO
        put(placeholders, "auth_xuid", options.getAccountData().getXuid());
        put(placeholders, "user_type", options.getAccountData().getUserType());

        put(placeholders, "version_name", options.getVersionName());
        put(placeholders, "version_type", options.getVersionType());
        put(placeholders, "game_directory", options.getGameDirectory());
        put(placeholders, "assets_root", options.getAssetsRoot());
        put(placeholders, "assets_index_name", getAssetIndex(json).getId());

        put(placeholders, "resolution_width", options.getWidth());
        put(placeholders, "resolution_height", options.getHeight());

        put(placeholders, "quickPlayPath", options.getQuickPlayPath());
        put(placeholders, "quickPlaySingleplayer", options.getQuickPlaySingleplayer());
        put(placeholders, "quickPlayMultiplayer", options.getQuickPlayMultiplayer());
        put(placeholders, "quickPlayRealms", options.getQuickPlayRealms());

        return placeholders;
    }

    protected final OsInfo currentOs() {
        String osName = System.getProperty("os.name", "").toLowerCase(Locale.ROOT);
        if (osName.contains("win")) {
            osName = "windows";
        } else if (osName.contains("mac") || osName.contains("darwin")) {
            osName = "osx";
        } else if (osName.contains("linux")) {
            osName = "linux";
        }

        String arch = System.getProperty("os.arch", "").toLowerCase(Locale.ROOT);
        arch = switch (arch) {
            case "amd64" -> "x86_64";
            case "aarch64" -> "arm64";
            case "i386", "i686" -> "x86";
            default -> arch;
        };

        String version = System.getProperty("os.version", "");
        return new OsInfo(osName, arch, version);
    }

    protected final String replacePlaceholders(String value, Map<String, String> placeholders) {
        if (value == null) {
            return null;
        }

        Matcher matcher = PLACEHOLDER.matcher(value);
        StringBuilder sb = new StringBuilder();
        while (matcher.find()) {
            String key = matcher.group(1);
            String replacement = placeholders.get(key);
            matcher.appendReplacement(sb, Matcher.quoteReplacement(replacement == null ? "" : replacement));
        }
        matcher.appendTail(sb);
        return sb.toString();
    }

    protected final List<String> parseArgumentValue(Object value, Map<String, String> placeholders) {
        List<String> result = new ArrayList<>();
        switch (value) {
            case null -> {
                return result;
            }
            case String s -> {
                result.add(replacePlaceholders(s, placeholders));
                return result;
            }
            case JSONArray array -> {
                for (Object item : array) {
                    if (item != null) {
                        result.add(replacePlaceholders(String.valueOf(item), placeholders));
                    }
                }
                return result;
            }
            default -> {
            }
        }

        result.add(replacePlaceholders(String.valueOf(value), placeholders));
        return result;
    }

    private void put(Map<String, String> map, String key, Object value) {
        map.put(key, value == null ? "" : String.valueOf(value));
    }
}
