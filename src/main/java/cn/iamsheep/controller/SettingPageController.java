package cn.iamsheep.controller;

import cn.iamsheep.api.Factory;
import cn.iamsheep.api.UIHandler;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXDialog;
import com.jfoenix.controls.JFXDialogLayout;
import io.datafx.controller.ViewController;
import javafx.fxml.FXML;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Text;

import javax.annotation.PostConstruct;

/**
 * @author Magical Sheep
 */
@ViewController(value = "/page/SettingPage.fxml", title = "Setting")
public class SettingPageController implements UIHandler {

    @FXML
    private StackPane root;

    @PostConstruct
    public void init() {
        Factory.UIData.regPage(this);
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