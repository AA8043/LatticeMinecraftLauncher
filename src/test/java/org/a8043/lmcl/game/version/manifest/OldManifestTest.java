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
class OldManifestTest {
    private Manifest manifest;
    private JSONObject json;

    private static final OsInfo WINDOWS = new OsInfo("windows", "x86_64", "10.0");
    private static final OsInfo OSX = new OsInfo("osx", "x86_64", "10.15");
    private static final OsInfo LINUX = new OsInfo("linux", "x86_64", "5.0");

    @BeforeAll
    void setUp() {
        manifest = new OldManifest();
        json = new JSONObject(ResourceUtil.readUtf8Str("manifest/old.json"));
    }

    @Test
    void getLibraries() {
        List<Library> libraries = manifest.getLibraries(json);

        assertNotNull(libraries);
        assertEquals(12, libraries.size());

        Library plain = libraries.get(1);
        assertEquals("c:d:2", plain.getName());
        assertNull(plain.getDownloads());
        assertNull(plain.getRules());
        assertNull(plain.getNatives());

        Library artifactOnly = libraries.getFirst();
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

        Library nativeLib = libraries.get(3);
        assertEquals("g:h:4", nativeLib.getName());
        assertNotNull(nativeLib.getExtract());
        assertTrue(nativeLib.isNativeCarrier("linux"));
        assertTrue(nativeLib.isNativeCarrier("windows"));
        assertTrue(nativeLib.isNativeCarrier("osx"));
        assertFalse(nativeLib.isNativeCarrier("freebsd"));
        assertFalse(nativeLib.isNativeClassifier());

        assertEquals("g/h/4/h-4.jar", nativeLib.getClasspathArtifact().getPath());

        assertEquals("g/h/4/h-4-natives-linux.jar",
            nativeLib.getNativeArtifact("linux").getPath());
        assertEquals("g/h/4/h-4-natives-windows.jar",
            nativeLib.getNativeArtifact("windows").getPath());
        assertEquals("g/h/4/h-4-natives-osx.jar",
            nativeLib.getNativeArtifact("osx").getPath());
        assertNull(nativeLib.getNativeArtifact("freebsd"));

        assertTrue(nativeLib.isAllowed(WINDOWS, null));
        assertTrue(nativeLib.isAllowed(LINUX, null));
        assertFalse(nativeLib.isAllowed(OSX, null));

        Library osxOnly = libraries.get(4);
        assertEquals("i:j:5", osxOnly.getName());
        assertFalse(osxOnly.isAllowed(WINDOWS, null));
        assertFalse(osxOnly.isAllowed(LINUX, null));
        assertTrue(osxOnly.isAllowed(OSX, null));

        Library nativeLimited = libraries.get(5);
        assertEquals("k:l:6", nativeLimited.getName());
        assertTrue(nativeLimited.isNativeCarrier("linux"));
        assertTrue(nativeLimited.isNativeCarrier("windows"));
        assertFalse(nativeLimited.isNativeCarrier("osx"));
        assertNull(nativeLimited.getClasspathArtifact());
        assertEquals("k/l/6/l-6-natives-linux.jar",
            nativeLimited.getNativeArtifact("linux").getPath());
        assertEquals("k/l/6/l-6-natives-windows.jar",
            nativeLimited.getNativeArtifact("windows").getPath());
        assertNull(nativeLimited.getNativeArtifact("osx"));

        Library withSources = libraries.get(6);
        assertEquals("m:n:7", withSources.getName());
        assertEquals("m/n/7/n-7.jar", withSources.getClasspathArtifact().getPath());
        assertEquals("m/n/7/n-7-sources.jar",
            withSources.getDownloads().getClassifiers().get("sources").getPath());

        Library osxNative = libraries.get(7);
        assertEquals("o:p:8", osxNative.getName());
        assertTrue(osxNative.isNativeCarrier("osx"));
        assertFalse(osxNative.isNativeCarrier("linux"));
        assertTrue(osxNative.isAllowed(OSX, null));
        assertFalse(osxNative.isAllowed(WINDOWS, null));
        assertEquals("o/p/8/p-8-natives-osx.jar",
            osxNative.getNativeArtifact("osx").getPath());

        Library optifine = libraries.get(8);
        assertEquals("optifine:OptiFine:test", optifine.getName());
        assertNull(optifine.getDownloads());
        assertNull(optifine.getClasspathArtifact());

        Library launchwrapper = libraries.get(9);
        assertEquals("net.minecraft:launchwrapper:test", launchwrapper.getName());
        assertNull(launchwrapper.getDownloads());

        Library forge = libraries.get(10);
        assertEquals("net.minecraftforge:forge:test", forge.getName());
        assertNotNull(forge.getDownloads());
        LibraryArtifact forgeArt = forge.getClasspathArtifact();
        assertNotNull(forgeArt);
        assertEquals("q/r/9/r-9.jar", forgeArt.getPath());
        assertEquals("", forgeArt.getUrl());
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

        LaunchOptions options = new LaunchOptions(
            account,
            4096, 1024,
            "/tmp/my game",
            0, 0,
            "/tmp/assets",
            null, null, null, null,
            Map.of("has_custom_resolution", false),
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
            "--userType", "msa",
            "--versionType", "release"
        );
        assertEquals(expected, args);

        assertEquals(1, args.stream().filter("/tmp/my game"::equals).count());

        for (int i = 0; i < args.size(); i += 2) {
            String key = args.get(i);
            assertTrue(key.startsWith("--"));
            assertTrue(i + 1 < args.size());
            String value = args.get(i + 1);
            assertFalse(value.startsWith("--"));
        }

        String template = json.getStr("minecraftArguments");
        assertNotNull(template);
        assertEquals(template.trim().split("\\s+").length, args.size());
    }
}
