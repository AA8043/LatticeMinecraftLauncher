package org.a8043.lmcl.game.version;

import org.a8043.lmcl.game.WithInfo;
import org.a8043.lmcl.step.Step;

import java.util.List;

public abstract class Version implements WithInfo {
    public abstract List<Step> getSteps() throws Exception;
}
