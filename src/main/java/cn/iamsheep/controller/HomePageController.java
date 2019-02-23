package cn.iamsheep.controller;

import cn.iamsheep.api.Factory;
import cn.iamsheep.api.UIHandler;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXDialog;
import com.jfoenix.controls.JFXDialogLayout;
import com.jfoenix.controls.JFXTextArea;
import io.datafx.controller.ViewController;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Text;

import javax.annotation.PostConstruct;

/**
 * @author Magical Sheep
 */
@ViewController(value = "/page/HomePage.fxml", title = "Home")
public class HomePageController implements UIHandler {

    @FXML
    private StackPane root;
    @FXML
    private JFXTextArea console;
    @FXML
    private JFXButton sync;
    @FXML
    private BorderPane container;

    @PostConstruct
    public void init() {
        Factory.UIData.regPage(this);
        AnchorPane.setLeftAnchor(container, Factory.UIData.LRSpacing);
        AnchorPane.setRightAnchor(container, Factory.UIData.LRSpacing);
        AnchorPane.setTopAnchor(container, Factory.UIData.TBSpacing);
        AnchorPane.setBottomAnchor(container, Factory.UIData.TBSpacing);

        AnchorPane.setLeftAnchor(console,Factory.UIData.LRSpacing-15);
        AnchorPane.setRightAnchor(console, Factory.UIData.LRSpacing-15);
        AnchorPane.setTopAnchor(console, Factory.UIData.TBSpacing-15);
        AnchorPane.setBottomAnchor(console, Factory.UIData.TBSpacing-15);

        container.setMaxWidth(Factory.UIData.getScreenWidth());
        container.setMaxHeight(Factory.UIData.getScreenHeight());

        console.textProperty().bind(Factory.UIData.getConsoleInfo());
        Factory.UI.print(Factory.group);

        sync.addEventHandler(MouseEvent.MOUSE_CLICKED, event -> {
            try{
                Factory.group.sync();
                Factory.UIData.savePlace(Factory.group);
                Factory.UI.print(Factory.group);
            }catch (Exception e){
                showDialog("Exception",e.getMessage());
            }

        });
    }

    @Override
    public void resize() {

    }

    @Override
    public void release() {

    }

    @Override
    public void showDialog(String heading, String body) {
        JFXButton ok = new JFXButton("确定");
        ok.setPrefSize(70, 35);
        JFXDialogLayout content = new JFXDialogLayout();
        content.setHeading(new Text(heading));
        content.setBody(new Text(body));
        content.setActions(ok);
        JFXDialog dialog = new JFXDialog(root, content, JFXDialog.DialogTransition.BOTTOM);
        dialog.show();
        ok.setOnAction(event -> dialog.close());
    }
}

