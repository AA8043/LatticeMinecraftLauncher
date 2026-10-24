package org.a8043.lmcl.game.launcher;

import lombok.Value;
import org.a8043.lmcl.game.user.AccountData;

import java.util.Map;

@Value
public class LaunchOptions {
    AccountData accountData;

    int maxMemory, minMemory;
    String gameDirectory;

    int width, height;

    String assetsRoot;

    String quickPlayPath;
    String quickPlaySingleplayer;
    String quickPlayMultiplayer;
    String quickPlayRealms;

    Map<String, Boolean> features;

    String versionName;
    String versionType;
}
