package cn.iamsheep;

import cn.iamsheep.api.Factory;
import cn.iamsheep.api.UIHandler;
import cn.iamsheep.controller.Frame;
import com.jfoenix.controls.JFXDecorator;
import io.datafx.controller.context.FXMLViewContext;
import io.datafx.controller.flow.Flow;
import io.datafx.controller.flow.container.DefaultFlowContainer;
import io.datafx.controller.flow.context.ViewFlowContext;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.*;

public class Entrance extends Application {

    @FXMLViewContext
    private ViewFlowContext flowContext;

    private Stage stage;
    private final static String namePath = "D://11/name.txt";

    public static void main(String[] args) throws Exception {
        if (new File("D://11/data.ser").exists()) {
            Factory.group = Factory.UIData.readPlace();
        } else {
            Factory.group = new Group(Factory.UIData.readFile(namePath));
            Factory.UIData.savePlace(Factory.group);
        }
        launch(args);
        System.exit(0);

    }

    private static void exchange() throws Exception {
//        System.out.println("\n请输入要调换座位的两个姓名（两个名字之间用空格分开）：\n");
//        String nameOne = scanner.next();
//        String nameTwo = scanner.next();
//        if (Factory.UIData.getChineseSize(nameOne) == 2) nameOne = nameOne + "　";
//        if (Factory.UIData.getChineseSize(nameTwo) == 2) nameTwo = nameTwo + "　";
//        try {
//            Factory.group.exchange(nameOne, nameTwo);
//            System.out.println("\n座位调换成功！");
//            Factory.UIData.savePlace(Factory.group);
//            System.out.println("座位表已保存至本地！\n");
//        } catch (Group.ExchangeException e) {
//            System.out.println("\n" + e.getMessage() + "\n");
//        }
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
