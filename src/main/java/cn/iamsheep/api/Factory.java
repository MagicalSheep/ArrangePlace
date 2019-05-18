package cn.iamsheep.api;

import cn.iamsheep.ui.Frame;
import cn.iamsheep.model.SeatDiagram;
import cn.iamsheep.model.Student;
import cn.iamsheep.ui.HomePageController;
import cn.iamsheep.ui.SideMenuController;
import cn.iamsheep.util.SeatHandler;
import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * @author Magical Sheep
 */
public class Factory {

    private static Stage stage;
    private static HomePageController homePageController = null;
    private static SideMenuController sideMenuController = null;
    private static Frame frame = null;
    private static StringProperty consoleInfo;

    public static SeatHandler seatHandler;

    private Factory() {
    }

    static {
        consoleInfo = new SimpleStringProperty("");
        try {
            seatHandler = new SeatHandler();
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
            UI.showDialog("Exception", e.getMessage());
        }
    }

    public static class UIData {

        public final static double LRSpacing = 30; // 控件左右间距
        public final static double TBSpacing = 20; // 控件上下间距

        public static void regHomePage(HomePageController page) {
            homePageController = page;
        }

        public static void regSideMenu(SideMenuController controller){
            sideMenuController = controller;
        }

        public static void regFrame(Frame fr){
            frame = fr;
        }

        /**
         * 清除控制台页面内容
         */
        public static void clearConsoleInfo() {
            consoleInfo.setValue("");
        }

        public static void setStage(Stage stage1) {
            stage = stage1;
        }

        /**
         * 获取控制台消息缓存区的内容
         *
         * @return String
         */
        public static StringProperty getConsoleInfo() {
            return consoleInfo;
        }

        public static Stage getStage() {
            return stage;
        }

        /**
         * 计算当前窗口大小下的控件宽度
         *
         * @return 控件宽度
         */
        public static double getScreenWidth() {
            return stage.getWidth() - (2 * LRSpacing);
        }

        /**
         * 计算当前窗口大小下的控件高度
         *
         * @return 控件高度
         */
        public static double getScreenHeight() {
            if (stage.isFullScreen()) {
                return stage.getHeight() - Frame.getToolbarHeight() - (2 * TBSpacing);
            } else {
                return stage.getHeight() - Frame.getToolbarHeight() - (2 * TBSpacing) - 32;
            }
        }
    }

    public static class UI {

        public static HomePageController getHomePage() {
            return homePageController;
        }

        public static SideMenuController getSideMenu() {
            return sideMenuController;
        }

        public static Frame getFrame(){
            return frame;
        }

        /**
         * 向控制台页面输出单行字符串
         *
         * @param msg 输出内容
         */
        public static void println(String msg) {
            Platform.runLater(() -> consoleInfo.setValue(consoleInfo.getValue() + msg + "\n"));
        }

        /**
         * 向控制台页面输出字符串
         *
         * @param msg 输出内容
         */
        public static void print(String msg) {
            Platform.runLater(() -> consoleInfo.setValue(consoleInfo.getValue() + msg));
        }

        public static void print(SeatDiagram seatDiagram, boolean isAnimate) {
            new Thread(() -> {
                Student[][] place = seatDiagram.getSeat();
                for (Student[] students : place) {
                    for (int j = 0; j < students.length; j++) {
                        print(students[j].getName() + "　");
                        if (((j + 1) % 3 == 0)) print("　　");
                        if (isAnimate) {
                            try {
                                Thread.sleep(350);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                    print("\n");
                }
                print("\n");
            }).start();
        }

        /**
         * 向当前页面弹出Dialog
         *
         * @param heading 弹窗标题
         * @param body    弹窗内容
         */
        public static void showDialog(String heading, String body) {
            Platform.runLater(() -> homePageController.showDialog(heading, body));
        }

    }

}
