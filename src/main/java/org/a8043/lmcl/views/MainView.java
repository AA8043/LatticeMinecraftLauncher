package org.a8043.lmcl.views;

import javafx.fxml.FXML;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.image.ImageView;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.a8043.cwaFX.annotations.bean.Autowired;
import org.a8043.cwaFX.annotations.bean.Bean;
import org.a8043.cwaFX.annotations.bean.Initialize;
import org.a8043.cwaFX.annotations.bean.OnFXThread;
import org.a8043.cwaFX.annotations.view.FxmlView;

import java.util.List;

@Bean
@FxmlView
@Slf4j
public class MainView {
    @Autowired
    private List<ViewTab> viewTabServiceList;
    @FXML
    @Getter
    private TabPane pane;

    @Initialize
    @OnFXThread
    private void init() {
        log.info("MainView initialized with {} tabs.", viewTabServiceList.size());

        viewTabServiceList.forEach(vt -> {
            Tab tab = new Tab(vt.getTitle(), vt.getContent());
            tab.setGraphic(new ImageView(vt.getIcon()) {{
                setFitWidth(16);
                setFitHeight(16);
            }});
            pane.getTabs().add(tab);
        });
    }
}
