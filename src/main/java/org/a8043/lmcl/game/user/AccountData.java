package org.a8043.lmcl.game.user;

import lombok.Value;

@Value
public class AccountData {
    String username;
    String uuid;
    String accessToken;
    String clientToken;
    String refreshToken;
    long expiresAt;
}
