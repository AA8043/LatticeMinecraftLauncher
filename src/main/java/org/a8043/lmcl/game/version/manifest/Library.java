package org.a8043.lmcl.game.version.manifest;

import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.Map;

@Getter
@Setter
public class Library {
    private String name;
    private LibraryDownloads downloads;
    private Map<String, String> natives;
    private List<Rule> rules;
    private Extract extract;

    public MavenName getMavenName() {
        return MavenName.parse(name);
    }

    public boolean isNativeCarrier(String os) {
        return natives != null && natives.containsKey(os);
    }

    public boolean isNativeClassifier() {
        String classifier = getMavenName().getClassifier();
        return classifier != null && PlatformClassifier.isPlatform(classifier);
    }

    public LibraryArtifact getClasspathArtifact() {
        if (isNativeClassifier()) {
            return null;
        }
        return downloads == null ? null : downloads.getArtifact();
    }

    public LibraryArtifact getNativeArtifact(String os) {
        if (isNativeCarrier(os)) {
            String classifier = natives.get(os);
            if (downloads == null || downloads.getClassifiers() == null) {
                return null;
            }
            return downloads.getClassifiers().get(classifier);
        }
        if (isNativeClassifier()) {
            return downloads == null ? null : downloads.getArtifact();
        }
        return null;
    }

    public boolean isAllowed(OsInfo currentOs, Map<String, Boolean> features) {
        if (rules == null || rules.isEmpty()) {
            return true;
        }
        boolean allowed = false;
        for (Rule rule : rules) {
            if (rule.matches(currentOs, features)) {
                allowed = "allow".equals(rule.getAction());
            }
        }
        return allowed;
    }
}
