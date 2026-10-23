package org.a8043.lmcl.game.version.manifest;

import lombok.Value;

@Value
public class AssetIndex {
    String id;
    String sha1;
    long size;
    long resourcesSize;
}
