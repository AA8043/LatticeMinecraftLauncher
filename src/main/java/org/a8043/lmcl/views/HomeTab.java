package org.a8043.lmcl.views;

import cn.hutool.core.io.resource.ResourceUtil;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import lombok.Getter;
import org.a8043.cwaFX.I18n;
import org.a8043.cwaFX.annotations.bean.Bean;
import org.a8043.cwaFX.annotations.bean.Service;
import org.a8043.cwaFX.annotations.view.FxmlView;

@Bean
@FxmlView
@Service
public class HomeTab implements ViewTab {
    @Getter
    @FXML
    private AnchorPane content;
    @FXML
    private ImageView userImage;
    @FXML
    private Label userName;
    @FXML
    private Label gameName;
    @FXML
    private Label gameDescription;
    @FXML
    private ListView instancesList;

    @Override
    public String getTitle() {
        return I18n.get("tabs.home");
    }

    @Override
    public Image getIcon() {
        return new Image(ResourceUtil.getResource("icons/home.png").toExternalForm());
    }

    @FXML
    private void startGame() {

    }

    @FXML
    private void manageGame() {

    }

    @FXML
    private void manageUser() {

    }
}
