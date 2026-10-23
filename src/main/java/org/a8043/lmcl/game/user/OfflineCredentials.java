package org.a8043.lmcl.game.user;

import lombok.EqualsAndHashCode;
import lombok.Value;

@EqualsAndHashCode(callSuper = true)
@Value
public class OfflineCredentials extends Credentials {
    String name;
}
