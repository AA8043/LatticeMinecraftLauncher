package org.a8043.lmcl.step;

import javafx.scene.image.Image;

public interface Step {
    String getTitle();

    Image getIcon();

    void execute(StepStatus status, StepContext context) throws Exception;
}
