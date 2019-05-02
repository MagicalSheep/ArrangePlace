package cn.iamsheep;

import cn.iamsheep.api.Factory;
import cn.iamsheep.ui.Frame;
import cn.iamsheep.ui.HomePageController;
import cn.iamsheep.util.FileHandler;
import com.jfoenix.controls.JFXDecorator;
import io.datafx.controller.context.FXMLViewContext;
import io.datafx.controller.flow.Flow;
import io.datafx.controller.flow.container.DefaultFlowContainer;
import io.datafx.controller.flow.context.ViewFlowContext;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class Entrance extends Application {

    @FXMLViewContext
    private ViewFlowContext flowContext;

    private Stage stage;

    public static void main(String[] args) throws IOException {
        FileHandler.init();
        launch(args);
        System.exit(0);

    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        stage = primaryStage;
        Factory.UIData.setStage(stage);
        flowContext = new ViewFlowContext();
        flowContext.register("Stage", primaryStage);
        Flow flow = new Flow(Frame.class);
        DefaultFlowContainer container = new DefaultFlowContainer();
        flow.createHandler(flowContext).start(container);

        JFXDecorator decorator = new JFXDecorator(primaryStage, container.getView(), true, true, true);
        Scene scene = new Scene(decorator, 1100, 680);
        scene.getStylesheets().add(getClass().getClassLoader().getResource("style/material-designs-style.css").toExternalForm());

        primaryStage.setTitle("座位表程序");
        primaryStage.setScene(scene);
        primaryStage.setResizable(false);

        primaryStage.widthProperty().addListener((observable, oldValue, newValue) -> {
            HomePageController page = Factory.UI.getHomePage();
            if (page != null) page.resize();
        });
        primaryStage.fullScreenProperty().addListener((observable, oldValue, newValue) -> {
            HomePageController page = Factory.UI.getHomePage();
            if (page != null) page.resize();
        });

        primaryStage.show();
    }


}
