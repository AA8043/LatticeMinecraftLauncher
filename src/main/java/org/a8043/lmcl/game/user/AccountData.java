package org.a8043.lmcl.game.user;

import lombok.Value;

@Value
public class AccountData {
    String username;
    String userType;
    String uuid;
    String xuid;
    String accessToken;
    String clientToken;
    String refreshToken;
    long expiresAt;
}
