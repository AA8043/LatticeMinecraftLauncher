package org.a8043.lmcl.step;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.StringProperty;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class StepStatus {
    private final StringProperty tip;
    private final DoubleProperty progress;
    private final StringProperty failureReason;

    public void setTip(String tip) {
        this.tip.set(tip);
    }

    public void setProgress(double progress) {
        this.progress.set(progress);
    }

    public void setFailureReason(String failureReason) {
        this.failureReason.set(failureReason);
    }
}
