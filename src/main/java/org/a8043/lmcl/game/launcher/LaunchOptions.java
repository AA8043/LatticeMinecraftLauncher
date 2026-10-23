package org.a8043.lmcl.game.launcher;

import lombok.Value;
import org.a8043.lmcl.game.user.AccountData;

@Value
public class LaunchOptions {
    AccountData accountData;
    int maxMemory, minMemory;
    int width, height;
    String versionType;
}
