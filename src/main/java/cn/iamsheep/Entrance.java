package cn.iamsheep;

import cn.iamsheep.api.Factory;
import cn.iamsheep.api.UIHandler;
import cn.iamsheep.controller.Frame;
import cn.iamsheep.util.Group;
import com.jfoenix.controls.JFXDecorator;
import io.datafx.controller.context.FXMLViewContext;
import io.datafx.controller.flow.Flow;
import io.datafx.controller.flow.container.DefaultFlowContainer;
import io.datafx.controller.flow.context.ViewFlowContext;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.File;

public class Entrance extends Application {

    @FXMLViewContext
    private ViewFlowContext flowContext;

    private Stage stage;
    private final static String namePath = "D://11/name.txt";

    public static void main(String[] args) throws Exception {
        if (new File("D://11/data.ser").exists()) {
            Factory.group = Factory.UIData.readPlace(namePath);
        } else {
            Factory.group = new Group(Factory.UIData.readFile(namePath));
            Factory.UIData.savePlace(Factory.group,"data.ser","D://11");
        }
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
            UIHandler page = Factory.UI.getCurrentPage();
            if (page != null) page.resize();
        });
        primaryStage.fullScreenProperty().addListener((observable, oldValue, newValue) -> {
            UIHandler page = Factory.UI.getCurrentPage();
            if (page != null) page.resize();
        });

        primaryStage.show();
    }


}
