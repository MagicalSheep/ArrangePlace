package cn.iamsheep.ui;

import cn.iamsheep.api.Factory;
import cn.iamsheep.util.FileHandler;
import cn.iamsheep.util.SeatHandler;
import cn.iamsheep.util.UnicodeReader;
import com.jfoenix.controls.*;
import com.sun.istack.internal.Nullable;
import io.datafx.controller.ViewController;
import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Text;

import javax.annotation.PostConstruct;
import java.io.IOException;

import static cn.iamsheep.api.Factory.seatHandler;

/**
 * @author Magical Sheep
 */
@ViewController(value = "/page/HomePage.fxml", title = "Home")
public class HomePageController{

    @FXML
    private StackPane root;
    @FXML
    private JFXTextArea console;
    @FXML
    private BorderPane container;

    @PostConstruct
    public void init() {
        Factory.UIData.regHomePage(this);
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
    }

    public void showDialog(String heading, String body) {
        JFXButton ok = new JFXButton("确定");
        ok.setPrefSize(70, 35);
        JFXDialogLayout content = new JFXDialogLayout();
        content.setHeading(new Text(heading));
        content.setBody(new Text(body));
        content.setActions(ok);
        JFXDialog dialog = new JFXDialog(root, content, JFXDialog.DialogTransition.BOTTOM);
        dialog.show();
        dialog.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ENTER) dialog.close();
        });
        ok.setOnAction(event -> dialog.close());
    }

    public void resize() {
        int size = (int) Factory.UIData.getScreenWidth() / 45;
        console.setStyle("-fx-font-size:" + size + "px");
    }

    private void sync() {
        Factory.UIData.clearConsoleInfo();
        Factory.UI.print(seatHandler.getResultSeat());
    }

    /**
     * 请求输入密钥
     */
    private void showPasswordDialog(int mode, @Nullable String nameOne, @Nullable String nameTwo) {
        JFXButton ok = new JFXButton("确定");
        JFXPasswordField passwordField = new JFXPasswordField();
        ok.setPrefSize(70, 35);
        JFXDialogLayout content = new JFXDialogLayout();
        content.setHeading(new Text("执行密钥"));
        content.setBody(passwordField);
        content.setActions(ok);
        JFXDialog dialog = new JFXDialog(root, content, JFXDialog.DialogTransition.BOTTOM);
        dialog.show();
        passwordField.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ENTER) passwordAction(passwordField, mode, nameOne, nameTwo, dialog);
        });
        ok.setOnAction(event -> passwordAction(passwordField, mode, nameOne, nameTwo, dialog));
    }

    private void passwordAction(JFXPasswordField passwordField, int mode, String nameOne, String nameTwo, JFXDialog dialog) {
        if (passwordField.getText().equals("52304")) {
            if (mode == 1) {
                Platform.runLater(this::continueToSave);
            } else {
                Platform.runLater(() -> continueToExchange(nameOne, nameTwo));
            }
        } else {
            showDialog("提示", "密钥错误！");
        }
        dialog.close();
    }

    /**
     * 通过验证后继续执行更换座位
     */
    private void continueToExchange(String nameOne, String nameTwo) {
        try {
            seatHandler.adminExchange(nameOne, nameTwo);
            Platform.runLater(() -> showDialog("提示", "座位调换成功！"));
            FileHandler.saveSeatDiagram();
            sync();
        } catch (SeatHandler.ExchangeException e) {
            Platform.runLater(() -> showDialog("提示", e.getMessage()));
        } catch (Exception e) {
            Platform.runLater(() -> showDialog("Exception", e.getMessage()));
        }
    }

    /**
     * 通过密钥验证后继续保存
     */
    private void continueToSave() {
        try {
            FileHandler.saveSeatDiagram(seatHandler.getResultSeat());
            showDialog("提示","保存成功！");
            seatHandler.init();
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
            showDialog("Exception", e.getMessage());
        }
    }

    /**
     * 请求输入调换座位的姓名
     */
    void showExchangeDialog() {
        JFXButton ok = new JFXButton("确定");
        JFXTextField nameField = new JFXTextField();
        ok.setPrefSize(70, 35);
        JFXDialogLayout content = new JFXDialogLayout();
        content.setHeading(new Text("请输入姓名（两个名字之间用空格分开）"));
        content.setBody(nameField);
        content.setActions(ok);
        JFXDialog dialog = new JFXDialog(root, content, JFXDialog.DialogTransition.BOTTOM);
        dialog.show();
        nameField.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ENTER) {
                exchangeAction(nameField, dialog);
            }
        });
        ok.setOnAction(event -> exchangeAction(nameField, dialog));
    }

    private void exchangeAction(JFXTextField nameField, JFXDialog dialog){
        String nameOne = null;
        String nameTwo = null;
        try {
            String[] name = nameField.getText().split(" ");
            nameOne = name[0];
            nameTwo = name[1];
            if (UnicodeReader.getChineseSize(nameOne) == 2) nameOne = nameOne + "　";
            if (UnicodeReader.getChineseSize(nameTwo) == 2) nameTwo = nameTwo + "　";
            seatHandler.exchange(nameOne, nameTwo);
            Platform.runLater(() -> showDialog("提示", "座位调换成功！"));
            FileHandler.saveSeatDiagram();
            sync();
        } catch (SeatHandler.ExchangeException e) {
            showPasswordDialog(2, nameOne, nameTwo);
        } catch (Exception e) {
            e.printStackTrace();
            Platform.runLater(() -> showDialog("Exception", e.getMessage()));
        }
        dialog.close();
    }

    void showSaveDialog() {
        showPasswordDialog(1, null, null);
    }

    public HomePageController(){
        try{
            seatHandler = new SeatHandler();
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}