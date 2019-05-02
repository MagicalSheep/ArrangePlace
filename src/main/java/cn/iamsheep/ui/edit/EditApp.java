package cn.iamsheep.ui.edit;

import com.jfoenix.controls.JFXDecorator;
import io.datafx.controller.context.FXMLViewContext;
import io.datafx.controller.flow.Flow;
import io.datafx.controller.flow.FlowException;
import io.datafx.controller.flow.container.DefaultFlowContainer;
import io.datafx.controller.flow.context.ViewFlowContext;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.File;

public class EditApp {

    static File file;

    private Stage stage;

    @FXMLViewContext
    private ViewFlowContext flowContext;

    public EditApp(File f) {
        file = f;
        stage = new Stage();
        flowContext = new ViewFlowContext();
        flowContext.register("Stage", stage);
        Flow flow = new Flow(EditFrame.class);
        DefaultFlowContainer container = new DefaultFlowContainer();
        try {
            flow.createHandler(flowContext).start(container);
        } catch (FlowException e) {
            e.printStackTrace();
        }
        JFXDecorator decorator = new JFXDecorator(stage, container.getView(), false, true, true);
        Scene scene = new Scene(decorator, 300, 500);
        scene.getStylesheets().add(getClass().getClassLoader().getResource("style/material-designs-style.css").toExternalForm());
        stage.setTitle("座位表程序");
        stage.setScene(scene);
        stage.setResizable(false);
        stage.show();
    }

}
