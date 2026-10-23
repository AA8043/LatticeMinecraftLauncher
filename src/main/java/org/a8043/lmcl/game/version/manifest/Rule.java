package org.a8043.lmcl.game.version.manifest;

import lombok.Getter;
import lombok.Setter;

import java.util.Map;

@Getter
@Setter
public class Rule {
    private String action;
    private Os os;
    private Map<String, Boolean> features;

    public boolean matches(OsInfo current, Map<String, Boolean> feats) {
        if (os != null) {
            if (os.getName() != null && !os.getName().equals(current.getName())) {
                return false;
            }
            if (os.getArch() != null && !os.getArch().equals(current.getArch())) {
                return false;
            }
            if (os.getVersion() != null && !current.getVersion().matches(os.getVersion())) {
                return false;
            }
        }
        if (features != null) {
            for (Map.Entry<String, Boolean> e : features.entrySet()) {
                boolean actual = feats != null && feats.getOrDefault(e.getKey(), false);
                if (e.getValue() != actual) {
                    return false;
                }
            }
        }
        return true;
    }

    @Getter
    @Setter
    public static class Os {
        private String name;
        private String arch;
        private String version;
    }
}
