package org.a8043.lmcl.game.version.manifest;

import cn.hutool.core.io.resource.ResourceUtil;
import cn.hutool.json.JSONObject;
import org.a8043.lmcl.game.launcher.LaunchOptions;
import org.a8043.lmcl.game.user.AccountData;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class NewManifestTest {
    private Manifest manifest;
    private JSONObject json;

    private static final OsInfo WINDOWS = new OsInfo("windows", "x86_64", "10.0");
    private static final OsInfo OSX = new OsInfo("osx", "x86_64", "10.15");
    private static final OsInfo LINUX = new OsInfo("linux", "x86_64", "5.0");

    @BeforeAll
    void setUp() {
        manifest = new NewManifest();
        json = new JSONObject(ResourceUtil.readUtf8Str("manifest/new.json"));
    }

    @Test
    void getLibraries() {
        List<Library> libraries = manifest.getLibraries(json);

        assertNotNull(libraries);
        assertEquals(4, libraries.size());

        Library plain = libraries.getFirst();
        assertEquals("c:d:2", plain.getName());
        assertNull(plain.getDownloads());
        assertNull(plain.getRules());
        assertNull(plain.getNatives());
        assertNull(plain.getClasspathArtifact());

        Library artifactOnly = libraries.get(1);
        assertEquals("a:b:1", artifactOnly.getName());
        assertNotNull(artifactOnly.getDownloads());

        LibraryArtifact art = artifactOnly.getClasspathArtifact();
        assertNotNull(art);
        assertEquals("a/b/1/b-1.jar", art.getPath());
        assertEquals(1L, art.getSize());
        assertEquals("https://example.invalid/b-1.jar", art.getUrl());
        assertEquals("0000000000000000000000000000000000000000", art.getSha1());

        Library ruleLib = libraries.get(2);
        assertEquals("e:f:3", ruleLib.getName());
        assertTrue(ruleLib.isAllowed(WINDOWS, null));
        assertTrue(ruleLib.isAllowed(LINUX, null));
        assertFalse(ruleLib.isAllowed(OSX, null));

        Library osxOnly = libraries.get(3);
        assertEquals("org.lwjgl:lwjgl:3.3.1:natives-macos", osxOnly.getName());
        assertTrue(osxOnly.isAllowed(OSX, null));
        assertFalse(osxOnly.isAllowed(WINDOWS, null));
        assertFalse(osxOnly.isAllowed(LINUX, null));

        assertTrue(osxOnly.isNativeClassifier());
        assertNull(osxOnly.getClasspathArtifact());
        LibraryArtifact nativeArtifact = osxOnly.getNativeArtifact("osx");
        assertNotNull(nativeArtifact);
        assertEquals(
            "org/lwjgl/lwjgl/3.3.1/lwjgl-3.3.1-natives-macos.jar",
            nativeArtifact.getPath()
        );
    }

    @Test
    void buildArgs() {
        AccountData account = new AccountData(
            "Player",
            "msa",
            "00000000000000000000000000000000",
            "test-xuid",
            "test-access-token",
            "test-client-token",
            "test-refresh-token",
            0
        );

        Map<String, Boolean> features = Map.of(
            "is_demo_user", true,
            "has_custom_resolution", true,
            "has_quick_plays_support", true,
            "is_quick_play_singleplayer", false,
            "is_quick_play_multiplayer", false,
            "is_quick_play_realms", false
        );

        LaunchOptions options = new LaunchOptions(
            account,
            1024, 1024,
            "/tmp/my game",
            1280, 720,
            "/tmp/assets",
            "/tmp/quickplay.json", null, null, null,
            features,
            "test-version",
            "release"
        );

        List<String> args = manifest.buildArgs(json, options);

        assertNotNull(args);
        assertTrue(args.stream().noneMatch(a -> a.contains("${")));

        List<String> expected = List.of(
            "--username", "Player",
            "--version", "test-version",
            "--gameDir", "/tmp/my game",
            "--assetsDir", "/tmp/assets",
            "--assetIndex", "test-asset",
            "--uuid", "00000000000000000000000000000000",
            "--accessToken", "test-access-token",
            "--clientId", "",
            "--xuid", "test-xuid",
            "--userType", "msa",
            "--versionType", "release",
            "--demo",
            "--width", "1280",
            "--height", "720",
            "--quickPlayPath", "/tmp/quickplay.json"
        );

        assertEquals(expected, args);

        assertFalse(args.contains("--quickPlaySingleplayer"));
        assertFalse(args.contains("--quickPlayMultiplayer"));
        assertFalse(args.contains("--quickPlayRealms"));

        assertEquals(1, args.stream().filter("1280"::equals).count());
        assertEquals(1, args.stream().filter("/tmp/quickplay.json"::equals).count());

        for (int i = 0; i < 22; i += 2) {
            String key = args.get(i);
            assertTrue(key.startsWith("--"));
            assertTrue(i + 1 < args.size());
            String value = args.get(i + 1);
            assertFalse(value.startsWith("--"));
        }
    }
}
