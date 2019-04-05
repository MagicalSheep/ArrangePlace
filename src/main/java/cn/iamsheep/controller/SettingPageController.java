package cn.iamsheep.controller;

import cn.iamsheep.api.Factory;
import cn.iamsheep.api.UIHandler;
import cn.iamsheep.util.Mode;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXCheckBox;
import com.jfoenix.controls.JFXDialog;
import com.jfoenix.controls.JFXDialogLayout;
import io.datafx.controller.ViewController;
import javafx.fxml.FXML;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Text;

import javax.annotation.PostConstruct;


/**
 * @author Magical Sheep
 */
@ViewController(value = "/page/SettingPage.fxml", title = "Setting")
public class SettingPageController extends UIHandler {

    @FXML
    private JFXCheckBox nine;
    @FXML
    private JFXCheckBox group;

    @PostConstruct
    public void init() {
        Factory.UIData.regPage(this);
        switch (Factory.mode){
            case NINE:
                nine.setSelected(true);
                break;
            case GROUP:
                group.setSelected(true);
                break;
        }
        nine.addEventHandler(MouseEvent.MOUSE_CLICKED, event -> {
            group.setSelected(false);
            Factory.mode = Mode.NINE;
        });
        group.addEventHandler(MouseEvent.MOUSE_CLICKED, event -> {
            nine.setSelected(false);
            Factory.mode = Mode.GROUP;
        });
    }

    @Override
    public void resize() {

    }

    @Override
    public void release() {
    }

    @Override
    public void sync() {

    }
}