package org.a8043.lmcl.game;

import javafx.scene.image.Image;

public interface WithInfo {
    String getId() throws Exception;

    String getName() throws Exception;

    void setName(String name) throws Exception;

    String getDescription() throws Exception;

    void setDescription(String description) throws Exception;

    Image getIcon() throws Exception;

    void setIcon(Image icon) throws Exception;
}
