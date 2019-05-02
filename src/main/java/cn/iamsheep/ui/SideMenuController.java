package cn.iamsheep.ui;

import cn.iamsheep.api.Factory;
import cn.iamsheep.ui.edit.EditApp;
import cn.iamsheep.util.FileHandler;
import cn.iamsheep.util.SeatHandler;
import com.jfoenix.controls.*;
import io.datafx.controller.ViewController;
import io.datafx.controller.flow.context.FXMLViewFlowContext;
import io.datafx.controller.flow.context.ViewFlowContext;
import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

import static cn.iamsheep.api.Factory.seatHandler;

import javax.annotation.PostConstruct;
import java.io.File;
import java.io.IOException;
import java.util.*;

/**
 * @author Magical Sheep
 */
@ViewController(value = "/page/SideMenu.fxml", title = "Menu")
public class SideMenuController {

    @FXMLViewFlowContext
    private ViewFlowContext context;
    @FXML
    private JFXButton build;
    @FXML
    private VBox propertiesPane;
    @FXML
    private JFXButton create;

    private Image settingImage;
    private Image deleteImage;

    private int selectedIndex = 0;
    private Map<Integer, File> itemMap = new HashMap<>();
    private List<JFXCheckBox> jfxCheckBoxes = new ArrayList<>();

    public SideMenuController() {
        try {
            seatHandler = new SeatHandler();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @PostConstruct
    public void init() {
        Factory.UIData.regSideMenu(this);
        Objects.requireNonNull(context, "context");
        settingImage = new Image(String.valueOf(getClass().getClassLoader().getResource("icon/setting.png")));
        deleteImage = new Image(String.valueOf(getClass().getClassLoader().getResource("icon/delete.png")));
        sync();
        build.addEventHandler(MouseEvent.MOUSE_CLICKED, event -> {
            Factory.UI.getFrame().getDrawer().close();
            new Thread(() -> {
                try {
                    seatHandler.sync(FileHandler.loadRuleProperties(itemMap.get(selectedIndex).getPath()));
                    Platform.runLater(this::refresh);
                } catch (Exception e) {
                    e.printStackTrace();
                    Platform.runLater(() -> Factory.UI.getHomePage().showDialog("Exception", e.getMessage()));
                }
            }).start();
        });
        create.addEventHandler(MouseEvent.MOUSE_CLICKED, event -> {
            try {
                int i = itemMap.size();
                while(new File(FileHandler.root + "\\Properties\\新建规则" + i + ".properties").exists()){
                    i++;
                }
                FileHandler.createProperties("新建规则" + i, "-1", "-1", "-1", "-1", "-1", "-1", "-1", "-1", "-1");
                sync();
            } catch (IOException e) {
                e.printStackTrace();
                Factory.UI.showDialog("Exception", e.getMessage());
            }
        });
    }

    private Node createPropertiesItem(String itemName, int index) {
        BorderPane borderPane = new BorderPane();
        HBox hBox = new HBox();
        HBox hbox = new HBox();

        StackPane settingPane = new StackPane();
        StackPane deletePane = new StackPane();

        hbox.setSpacing(10);
        JFXCheckBox jfxCheckBox = new JFXCheckBox();
        jfxCheckBoxes.add(jfxCheckBox);
        Label name = new Label(itemName);

        ImageView setting = new ImageView(settingImage);
        ImageView delete = new ImageView(deleteImage);
        settingPane.getChildren().add(setting);
        deletePane.getChildren().add(delete);

        setting.setFitWidth(20);
        setting.setFitHeight(20);
        delete.setFitWidth(20);
        delete.setFitHeight(20);

        hBox.getChildren().addAll(jfxCheckBox, name);
        hbox.getChildren().addAll(settingPane, deletePane);
        borderPane.setLeft(hBox);
        borderPane.setRight(hbox);
        if (index == 0) jfxCheckBox.setSelected(true);

        EventHandler<MouseEvent> EnterEventHandler = event -> borderPane.setCursor(Cursor.HAND);
        EventHandler<MouseEvent> ExitEventHandler = event -> borderPane.setCursor(Cursor.DEFAULT);
        name.addEventHandler(MouseEvent.MOUSE_ENTERED, EnterEventHandler);
        name.addEventHandler(MouseEvent.MOUSE_EXITED, ExitEventHandler);
        settingPane.addEventHandler(MouseEvent.MOUSE_ENTERED, EnterEventHandler);
        settingPane.addEventHandler(MouseEvent.MOUSE_EXITED, ExitEventHandler);
        deletePane.addEventHandler(MouseEvent.MOUSE_ENTERED, EnterEventHandler);
        deletePane.addEventHandler(MouseEvent.MOUSE_EXITED, ExitEventHandler);
        settingPane.addEventHandler(MouseEvent.MOUSE_CLICKED, event -> new EditApp(itemMap.get(index)));
        deletePane.addEventHandler(MouseEvent.MOUSE_CLICKED, event -> new Thread(() -> {
            if (itemMap.get(index).delete()) {
                Platform.runLater(this::sync);
            } else {
                Factory.UI.getFrame().getDrawer().close();
                Factory.UI.showDialog("提示", "配置文件删除失败！");
            }
        }).start());
        name.addEventHandler(MouseEvent.MOUSE_CLICKED, event -> {
            cancelAllSelected();
            jfxCheckBox.setSelected(true);
            selectedIndex = index;
        });
        jfxCheckBox.setOnMouseClicked(event -> {
            cancelAllSelected();
            jfxCheckBox.setSelected(true);
            selectedIndex = index;
        });
        return borderPane;
    }

    private void cancelAllSelected() {
        for (JFXCheckBox jfxCheckBox : jfxCheckBoxes) {
            jfxCheckBox.setSelected(false);
        }
    }

    public void sync() {
        selectedIndex = 0;
        itemMap.clear();
        jfxCheckBoxes.clear();
        propertiesPane.getChildren().clear();
        List<File> propertiesList = FileHandler.loadRuleProperties();
        for (int i = 0; i < propertiesList.size(); i++) {
            File file = propertiesList.get(i);
            itemMap.put(i, file);
            try {
                propertiesPane.getChildren().add(createPropertiesItem(FileHandler.getRulePropertiesName(file.getPath()), i));
            } catch (IOException e) {
                e.printStackTrace();
                Factory.UI.getHomePage().showDialog("Exception", e.getMessage());
            }
        }
    }

    private void refresh() {
        Factory.UIData.clearConsoleInfo();
        Factory.UI.print(seatHandler.getResultSeat());
    }
}
