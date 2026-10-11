package org.a8043.lmcl.views;

import javafx.scene.Node;
import javafx.scene.image.Image;

public interface ViewTab {
    String getTitle();

    Image getIcon();

    Node getContent();
}
