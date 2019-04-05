package cn.iamsheep.controller;

import cn.iamsheep.api.Factory;
import cn.iamsheep.api.UIHandler;
import cn.iamsheep.util.GroupHandler;
import com.jfoenix.controls.*;
import io.datafx.controller.ViewController;
import javafx.application.Platform;
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
public class HomePageController extends UIHandler {

    @FXML
    private JFXTextArea console;
    @FXML
    private JFXButton sync;
    @FXML
    private JFXButton backup;
    @FXML
    private JFXButton exchange;
    @FXML
    private BorderPane container;

    private String password = "";

    @PostConstruct
    public void init() {
        Factory.UIData.regPage(this);
        AnchorPane.setLeftAnchor(container, Factory.UIData.LRSpacing);
        AnchorPane.setRightAnchor(container, Factory.UIData.LRSpacing);
        AnchorPane.setTopAnchor(container, Factory.UIData.TBSpacing);
        AnchorPane.setBottomAnchor(container, Factory.UIData.TBSpacing);

        AnchorPane.setLeftAnchor(console, Factory.UIData.LRSpacing - 15);
        AnchorPane.setRightAnchor(console, Factory.UIData.LRSpacing - 15);
        AnchorPane.setTopAnchor(console, Factory.UIData.TBSpacing - 15);
        AnchorPane.setBottomAnchor(console, Factory.UIData.TBSpacing - 15);

        container.setMaxWidth(Factory.UIData.getScreenWidth());
        container.setMaxHeight(Factory.UIData.getScreenHeight());

        console.textProperty().bind(Factory.UIData.getConsoleInfo());

        resize();
        sync();

        sync.addEventHandler(MouseEvent.MOUSE_CLICKED, event -> showPasswordDialog());
        backup.addEventHandler(MouseEvent.MOUSE_CLICKED, event -> showDialog("提示", "功能未完成！"));
        exchange.addEventHandler(MouseEvent.MOUSE_CLICKED, event -> showExchangeDialog());
    }

    @Override
    public void resize() {
        int size = (int) Factory.UIData.getScreenWidth() / 55;
        console.setStyle("-fx-font-size:" + size + "px");
    }

    @Override
    public void release() {
    }

    @Override
    public void sync() {
        Factory.UIData.clearConsoleInfo();
        Factory.UI.print(Factory.group, true);
    }

    /**
     * 请求输入密钥
     */
    private void showPasswordDialog() {
        JFXButton ok = new JFXButton("确定");
        JFXPasswordField passwordField = new JFXPasswordField();
        ok.setPrefSize(70, 35);
        JFXDialogLayout content = new JFXDialogLayout();
        content.setHeading(new Text("执行密钥"));
        content.setBody(passwordField);
        content.setActions(ok);
        JFXDialog dialog = new JFXDialog(root, content, JFXDialog.DialogTransition.BOTTOM);
        dialog.show();
        ok.setOnAction(event -> {
            if (passwordField.getText().equals("52304")) {
                Platform.runLater(() -> continueToSync());
            } else {
                showDialog("提示", "密钥错误！");
            }
            dialog.close();
        });
    }

    /**
     * 通过密钥验证后继续执行生成
     */
    private void continueToSync() {
        password = "";
        try {
            new GroupHandler(Factory.group).sync(Factory.mode);
            Factory.UIData.savePlace(Factory.group,"data.ser","D://11");
            sync();
        } catch (Exception e) {
            showDialog("Exception", e.getMessage());
        }
    }

    /**
     * 请求输入调换座位的姓名
     */
    private void showExchangeDialog() {
        JFXButton ok = new JFXButton("确定");
        JFXTextField nameField = new JFXTextField();
        ok.setPrefSize(70, 35);
        JFXDialogLayout content = new JFXDialogLayout();
        content.setHeading(new Text("请输入姓名（两个名字之间用空格分开）"));
        content.setBody(nameField);
        content.setActions(ok);
        JFXDialog dialog = new JFXDialog(root, content, JFXDialog.DialogTransition.BOTTOM);
        dialog.show();
        ok.setOnAction(event -> {
            new Thread(() -> {
                try {
                    String[] name = nameField.getText().split(" ");
                    String nameOne = name[0];
                    String nameTwo = name[1];
                    if (Factory.UIData.getChineseSize(nameOne) == 2) nameOne = nameOne + "　";
                    if (Factory.UIData.getChineseSize(nameTwo) == 2) nameTwo = nameTwo + "　";
                    new GroupHandler(Factory.group).exchange(nameOne, nameTwo);
                    Platform.runLater(() -> showDialog("提示", "座位调换成功！"));
                    Factory.UIData.savePlace(Factory.group, "data.ser", "D://11");
                    sync();
                } catch (GroupHandler.ExchangeException e) {
                    Platform.runLater(() -> showDialog("提示", e.getMessage()));
                } catch (Exception e) {
                    Platform.runLater(() -> showDialog("Exception", e.getMessage()));
                }
            }).start();
            dialog.close();
        });
    }
}