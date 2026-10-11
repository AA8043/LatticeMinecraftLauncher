package org.a8043.lmcl;

import cn.hutool.core.io.resource.ResourceUtil;
import org.a8043.cwaFX.CwaFX;
import org.a8043.cwaFX.annotations.bean.Autowired;
import org.a8043.cwaFX.annotations.bean.Bean;
import org.a8043.cwaFX.annotations.bean.Initialize;
import org.a8043.cwaFX.annotations.bean.OnFXThread;
import org.a8043.cwaFX.navigation.PageRegistry;
import org.a8043.cwaFX.window.Window;
import org.a8043.cwaFX.window.WindowCreator;
import org.a8043.lmcl.views.MainView;

@Bean
public class Main {
    @Autowired
    private WindowCreator windows;
    @Autowired
    private MainView mainView;
    @Autowired
    private PageRegistry pageRegistry;

    public static void main(String[] args) {
        CwaFX.start(Main.class, args);
    }

    @Initialize
    @OnFXThread
    private void show() {
        windows.addStyle(ResourceUtil.getResource("styles/main.css").toExternalForm());

        Window window = windows.create("main", "appName", 1000, 600);
        pageRegistry.register("main1", mainView.getPane(), mainView);
        window.navigate("main1", mainView);
        window.show();
    }
}