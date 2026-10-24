package org.a8043.lmcl.step;

import javafx.beans.property.StringProperty;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class StepStatus {
    private final StringProperty tip;

    public void setTip(String tip) {
        this.tip.set(tip);
    }
}
