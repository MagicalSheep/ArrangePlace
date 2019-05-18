package cn.iamsheep.ui;

import cn.iamsheep.api.Factory;
import cn.iamsheep.model.SeatDiagram;
import cn.iamsheep.util.FileHandler;
import com.jfoenix.controls.JFXDrawer;
import com.jfoenix.controls.JFXHamburger;
import com.jfoenix.controls.JFXToolbar;
import io.datafx.controller.ViewController;
import io.datafx.controller.flow.Flow;
import io.datafx.controller.flow.FlowHandler;
import io.datafx.controller.flow.context.FXMLViewFlowContext;
import io.datafx.controller.flow.context.ViewFlowContext;
import javafx.fxml.FXML;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.stage.FileChooser;
import javafx.util.Duration;

import javax.annotation.PostConstruct;

import java.io.File;
import java.io.IOException;

import static io.datafx.controller.flow.container.ContainerAnimations.SWIPE_LEFT;

/**
 * @author Magical Sheep
 */
@ViewController(value = "/page/Frame.fxml", title = "Main Frame")
public class Frame {

    @FXMLViewFlowContext
    private ViewFlowContext context;

    @FXML
    private StackPane root;
    @FXML
    private StackPane openBurger;
    @FXML
    private StackPane exchangeBurger;
    @FXML
    private StackPane saveBurger;
    @FXML
    private StackPane optionsBurger;
    @FXML
    private JFXHamburger options;
    @FXML
    private JFXDrawer drawer;
    @FXML
    private ImageView save;
    @FXML
    private ImageView open;
    @FXML
    private ImageView exchange;
    @FXML
    private static JFXToolbar toolbar;

    private SideMenuController sideMenuController;

    @PostConstruct
    public void init() throws Exception {
        Factory.UIData.regFrame(this);
        Flow exhibitionArea = new Flow(HomePageController.class);
        final FlowHandler flowHandler = exhibitionArea.createHandler(context);
        context.register("ContentFlowHandler", flowHandler);
        context.register("ContentFlow", exhibitionArea);
        final Duration containerAnimationDuration = Duration.millis(300);
        drawer.setContent(flowHandler.start(new ExtendedAnimatedFlowContainer(containerAnimationDuration, SWIPE_LEFT)));
        context.register("ContentPane", drawer.getContent().get(0));

        Flow sideMenuFlow = new Flow(SideMenuController.class);
        final FlowHandler sideMenuFlowHandler = sideMenuFlow.createHandler(context);
        drawer.setSidePane(sideMenuFlowHandler.start(new ExtendedAnimatedFlowContainer(containerAnimationDuration, SWIPE_LEFT)));
        sideMenuController = Factory.UI.getSideMenu();

        Image saveImage = new Image(String.valueOf(getClass().getClassLoader().getResource("icon/save.png")));
        Image openImage = new Image(String.valueOf(getClass().getClassLoader().getResource("icon/open.png")));
        Image exchangeImage = new Image(String.valueOf(getClass().getClassLoader().getResource("icon/exchange.png")));
        save.setImage(saveImage);
        open.setImage(openImage);
        exchange.setImage(exchangeImage);

        openBurger.setOnMouseClicked(event -> {
            File seatFile = chooseSeatDiagram();
            if (seatFile != null) {
                try {
                    SeatDiagram seatDiagram = FileHandler.openSeatDiagram(seatFile);
                    Factory.UIData.clearConsoleInfo();
                    Factory.UI.println("你正在查看历史座位表：\n");
                    Factory.UI.print(seatDiagram, false);
                } catch (IOException | ClassNotFoundException e) {
                    e.printStackTrace();
                    Factory.UI.showDialog("Exception", e.getMessage());
                }
            }
        });
        exchangeBurger.setOnMouseClicked(event -> {
            Factory.UIData.clearConsoleInfo();
            Factory.UI.print(Factory.seatHandler.getResultSeat(), false);
            Factory.UI.getHomePage().showExchangeDialog();
        });
        saveBurger.setOnMouseClicked(event -> {
            Factory.UIData.clearConsoleInfo();
            Factory.UI.print(Factory.seatHandler.getResultSeat(), false);
            Factory.UI.getHomePage().showSaveDialog();
        });
        optionsBurger.setOnMouseClicked(e -> {
            if (drawer.isClosed() || drawer.isClosing()) {
                Factory.UIData.clearConsoleInfo();
                Factory.UI.print(Factory.seatHandler.getResultSeat(), false);
                drawer.open();
            } else {
                drawer.close();
            }
        });
        context = new ViewFlowContext();
    }

    public static double getToolbarHeight(){
        return toolbar.getHeight();
    }

    public JFXDrawer getDrawer() {
        return drawer;
    }

    /**
     * 选择座位表文件
     *
     * @return 座位表文件
     */
    private File chooseSeatDiagram() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("选择座位表");
        fileChooser.setInitialDirectory(new File(FileHandler.root + "\\SeatData"));
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("座位表格式", "*.seat")
        );
        return fileChooser.showOpenDialog(Factory.UIData.getStage());
    }
}
