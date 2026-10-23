package org.a8043.lmcl.game.version.manifest;

import lombok.Value;

@Value
public class MavenName {
    String group;
    String artifact;
    String version;
    String classifier;

    public static MavenName parse(String name) {
        String[] p = name.split(":");
        if (p.length < 3) {
            throw new IllegalArgumentException("Invalid Maven name: " + name);
        }
        return new MavenName(p[0], p[1], p[2], p.length > 3 ? p[3] : null);
    }
}
