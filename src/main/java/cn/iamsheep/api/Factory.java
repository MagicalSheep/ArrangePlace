package cn.iamsheep.api;

import cn.iamsheep.controller.Frame;
import cn.iamsheep.util.Group;
import cn.iamsheep.util.Mode;
import cn.iamsheep.util.Student;
import cn.iamsheep.util.UnicodeReader;
import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.stage.Stage;

import java.io.*;
import java.nio.charset.Charset;
import java.util.ArrayList;

/**
 * @author Magical Sheep
 */
public class Factory {

    private static Stage stage;
    private static UIHandler currentPage = null;
    private static StringProperty consoleInfo;
    private static StringProperty testConsoleInfo;

    public static Mode mode = Mode.NINE;

    public static Group group = null;

    private Factory() {
    }

    static {
        consoleInfo = new SimpleStringProperty("");
        testConsoleInfo = new SimpleStringProperty("");
    }

    public static class UIData {

        public final static double LRSpacing = 30; // 控件左右间距
        public final static double TBSpacing = 20; // 控件上下间距

        /**
         * 获取内容中汉字个数
         *
         * @param content 内容
         * @return int
         */
        public static int getChineseSize(String content) {
            String regex = "[\u4e00-\u9fa5]";
            return content.length() - content.replaceAll(regex, "").length();
        }

        /**
         * 注册页面
         *
         * @param page 当前页面
         */
        public static void regPage(UIHandler page) {
            if (currentPage != null) currentPage.release();
            currentPage = page;
        }

        /**
         * 清除控制台页面内容
         */
        public static void clearConsoleInfo() {
            consoleInfo.setValue("");
        }

        /**
         * 清除测试控制台页面内容
         */
        public static void clearTestConsoleInfo() {
            testConsoleInfo.setValue("");
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

        /**
         * 获取测试控制台消息缓存区的内容
         *
         * @return
         */
        public static StringProperty getTestConsoleInfo() {
            return testConsoleInfo;
        }

        public static Stage getStage() {
            return stage;
        }

        /**
         * 从序列化文件中获取Group对象
         *
         * @param pathname 文件路径
         * @return Group对象
         * @throws Exception
         */
        public static Group readPlace(String pathname) throws Exception {
            FileInputStream fileIn = new FileInputStream(pathname);
            ObjectInputStream in = new ObjectInputStream(fileIn);
            Group group = (Group) in.readObject();
            in.close();
            fileIn.close();
            return group;
        }

        /**
         * 将Group对象序列化至硬盘
         *
         * @param group    Group对象
         * @param fileName 文件名字
         * @param path     文件路径
         * @throws Exception
         */
        public static void savePlace(Group group, String fileName, String path) throws Exception {
            FileOutputStream fileOut = new FileOutputStream(path + "/" + fileName);
            ObjectOutputStream out = new ObjectOutputStream(fileOut);
            out.writeObject(group);
            out.close();
            fileOut.close();
        }

        /**
         * 从学生名单文件中读取学生信息
         *
         * @param pathname 文件路径
         * @return 学生对象集合
         * @throws Exception
         */
        public static ArrayList<Student> readFile(String pathname) throws Exception {
            File file = new File(pathname);
            ArrayList<Student> studentsList = new ArrayList<>();
            BufferedReader bufferedReader = new BufferedReader(new UnicodeReader(new FileInputStream(file), Charset.defaultCharset().name()));
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                studentsList.add(new Student(line, null));
            }
            return studentsList;
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

        /**
         * 获取当前页面的Controller对象
         *
         * @return UIHandler
         */
        public static UIHandler getCurrentPage() {
            return currentPage;
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
         * 向测试控制台页面输出单行字符串
         *
         * @param msg 输出内容
         */
        public static void testPrintln(String msg) {
            Platform.runLater(() -> testConsoleInfo.setValue(testConsoleInfo.getValue() + msg + "\n"));
        }

        /**
         * 向控制台页面输出字符串
         *
         * @param msg 输出内容
         */
        public static void print(String msg) {
            Platform.runLater(() -> consoleInfo.setValue(consoleInfo.getValue() + msg));
        }

        /**
         * 向测试控制台页面输出字符串
         *
         * @param msg 输出内容
         */
        public static void testPrint(String msg) {
            Platform.runLater(() -> testConsoleInfo.setValue(testConsoleInfo.getValue() + msg));
        }

        public static void print(Group group) {
            Student[][] place = group.getPlace();
            for (Student[] students : place) {
                for (int j = 0; j < place.length; j++) {
                    print(students[j].getName() + "　");
                    if (((j + 1) % 3 == 0)) print("　　");
                }
                print("\n");
            }
            print("\n");
        }

        public static void testPrint(Group group) {
            Student[][] place = group.getPlace();
            for (Student[] students : place) {
                for (int j = 0; j < place.length; j++) {
                    testPrint(students[j].getName() + "　");
                    if (((j + 1) % 3 == 0)) testPrint("　　");
                }
                testPrint("\n");
            }
        }

        /**
         * 向当前页面弹出Dialog
         *
         * @param heading 弹窗标题
         * @param body    弹窗内容
         */
        public static void showDialog(String heading, String body) {
            Platform.runLater(() -> currentPage.showDialog(heading, body));
        }

    }

}
