package cn.iamsheep.util;

import cn.iamsheep.api.Factory;
import cn.iamsheep.model.SeatDiagram;
import cn.iamsheep.model.Student;
import cn.iamsheep.model.property.Position;

import java.io.*;
import java.nio.charset.Charset;
import java.text.SimpleDateFormat;
import java.util.*;

public class FileHandler {

    public static final String root = System.getProperty("user.dir"); // 程序运行目录

    /**
     * 从序列化文件中获取SeatDiagram对象
     *
     * @param i index
     * @return SeatDiagram对象
     * @throws IOException IOException
     * @throws ClassNotFoundException ClassNotFoundException
     */
    public static SeatDiagram openSeatDiagram(int i) throws IOException, ClassNotFoundException {
        List<File> seatDataList = getFileSortB(root + "\\SeatData");
        if (i >= seatDataList.size()) i = seatDataList.size() - 1;
        ObjectInputStream in = new ObjectInputStream(new FileInputStream(seatDataList.get(i)));
        SeatDiagram seatDiagram = (SeatDiagram) in.readObject();
        in.close();
        return seatDiagram;
    }

    public static SeatDiagram openSeatDiagram() throws IOException, ClassNotFoundException {
        return openSeatDiagram(0);
    }

    public static SeatDiagram openSeatDiagram(File seatFile) throws IOException, ClassNotFoundException{
        ObjectInputStream in = new ObjectInputStream(new FileInputStream(seatFile));
        SeatDiagram seatDiagram = (SeatDiagram) in.readObject();
        in.close();
        return seatDiagram;
    }

    /**
     * 将SeatDiagram对象序列化至硬盘
     *
     * @param seatDiagram SeatDiagram对象
     * @throws IOException IOException
     */
    public static void saveSeatDiagram(SeatDiagram seatDiagram) throws IOException {
        SimpleDateFormat fmt = new SimpleDateFormat("yyyy年MM月dd日HH时mm分ss秒");
        String fileName = fmt.format(new Date()) + ".seat";
        saveSeatDiagram(seatDiagram, fileName);
    }

    public static void saveSeatDiagram(SeatDiagram seatDiagram, String fileName) throws IOException {
        ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(new File(root + "\\SeatData\\" + fileName)));
        out.writeObject(seatDiagram);
        out.close();
    }

    public static void saveSeatDiagram() throws IOException {
        List<File> list = getFileSortB(root + "\\SeatData");
        saveSeatDiagram(Factory.seatHandler.getResultSeat(),list.get(0).getName());
    }

    public static List<File> loadRuleProperties(){
        return getFileSortA(root + "\\Properties");
    }

    public static HashMap<String, Integer> loadRuleProperties(String path) throws IOException {
        HashMap<String, Integer> resultMap = new HashMap<>();
        Properties properties = new Properties();
        properties.load(new FileReader(path));
        Set<Map.Entry<Object, Object>> entrys = properties.entrySet();
        for (Map.Entry<Object, Object> entry : entrys) {
            if(entry.getKey().equals("PropertiesName")) continue;
            resultMap.put((String) entry.getKey(), Integer.valueOf((String) entry.getValue()));
        }
        return resultMap;
    }

    public static String getRulePropertiesName(String path) throws IOException {
        Properties properties = new Properties();
        properties.load(new FileReader(path));
        return (String) properties.get("PropertiesName");
    }

    /**
     * 初始化
     *
     * @throws IOException IOException
     */
    public static void init() throws IOException {
        File seatDataDirectory = new File(root + "\\SeatData");
        if (!seatDataDirectory.exists()) {
            if (!seatDataDirectory.mkdir()) throw new IOException("Create the seat data directory failed");
        }
        File propertiesDirectory = new File(root + "\\Properties");
        if (!propertiesDirectory.exists()) {
            if (!propertiesDirectory.mkdir()) throw new IOException("Create the properties directory failed");
        }
        if (propertiesDirectory.listFiles().length <= 0) {
            createProperties("默认规则", "-1", "-1", "-1", "1", "1", "-1", "1", "1", "-1", "1", "1", "-1");
        }
        if (seatDataDirectory.listFiles().length <= 0) {
            int[] groupColumn = {3, 4, 3};
            Position[] emptyPosition = {new Position(0, 9), new Position(5, 8)};
            SeatDiagram seatDiagram = new SeatDiagram(readFile(root + "\\name.txt"), 7, 10, 3, groupColumn, emptyPosition);
            saveSeatDiagram(seatDiagram);
        }
    }

    /**
     * 从学生名单文件中读取学生信息
     *
     * @param pathname 文件路径
     * @return 学生对象集合
     * @throws IOException IOException
     */
    public static ArrayList<Student> readFile(String pathname) throws IOException {
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
     * 获取目录下所有文件（按时间顺序）
     *
     * @param path 目录
     * @return List
     */
    private static List<File> getFileSortA(String path) {
        List<File> list = getFiles(path, new ArrayList<>());
        if (list.size() > 0) {
            list.sort((file, newFile) -> {
                if (file.lastModified() > newFile.lastModified()) {
                    return 1;
                } else if (file.lastModified() == newFile.lastModified()) {
                    return 0;
                } else {
                    return -1;
                }
            });
        }
        return list;
    }

    /**
     * 获取目录下所有文件（按时间倒序）
     *
     * @param path 目录
     * @return List
     */
    private static List<File> getFileSortB(String path) {
        List<File> list = getFiles(path, new ArrayList<>());
        if (list.size() > 0) {
            list.sort((file, newFile) -> {
                if (file.lastModified() < newFile.lastModified()) {
                    return 1;
                } else if (file.lastModified() == newFile.lastModified()) {
                    return 0;
                } else {
                    return -1;
                }
            });
        }
        return list;
    }

    /**
     * 获取目录下所有文件
     *
     * @param path 目录
     * @param files 存放的集合
     * @return List
     */
    private static List<File> getFiles(String path, List<File> files) {
        File realFile = new File(path);
        if (realFile.isDirectory()) {
            File[] subFiles = realFile.listFiles();
            for (File file : subFiles) {
                if (file.isDirectory()) {
                    getFiles(file.getAbsolutePath(), files);
                } else {
                    files.add(file);
                }
            }
        }
        return files;
    }

    /**
     * 生成配置文件
     *
     * @param propertiesName 配置文件名称
     * @param lastTimeSameGroup 上一次坐在这个组
     * @param previousTimeSameGroup 上上次坐在这个组
     * @param beforePreviousTimeSameGroup 上上上次坐在这个组
     * @param lastTimeNotThisGroup 上一次不在这个组
     * @param previousTimeNotThisGroup 上上次不在这个组
     * @param beforePreviousTimeNotThisGroup 上上上次不在这个组
     * @param lastTimeFrontRow 上一次在前排
     * @param previousTimeFrontRow 上上次在前排
     * @param beforePreviousTimeFrontRow 上上上次在前排
     * @param lastTimeBehindRow 上一次在后排
     * @param previousTimeBehindRow 上上次在后排
     * @param beforePreviousTimeBehindRow 上上上次在后排
     * @throws IOException IOException
     */
    public static void createProperties(String propertiesName, String lastTimeSameGroup, String previousTimeSameGroup, String beforePreviousTimeSameGroup, String lastTimeNotThisGroup, String previousTimeNotThisGroup, String beforePreviousTimeNotThisGroup, String lastTimeFrontRow, String previousTimeFrontRow, String beforePreviousTimeFrontRow, String lastTimeBehindRow, String previousTimeBehindRow, String beforePreviousTimeBehindRow) throws IOException {
        Properties properties = new Properties();
        properties.setProperty("PropertiesName", propertiesName);
        properties.setProperty("LastTimeSameGroup", lastTimeSameGroup);
        properties.setProperty("PreviousTimeSameGroup", previousTimeSameGroup);
        properties.setProperty("BeforePreviousTimeSameGroup", beforePreviousTimeSameGroup);
        properties.setProperty("LastTimeNotThisGroup", lastTimeNotThisGroup);
        properties.setProperty("PreviousTimeNotThisGroup", previousTimeNotThisGroup);
        properties.setProperty("BeforePreviousTimeNotThisGroup", beforePreviousTimeNotThisGroup);
        properties.setProperty("LastTimeFrontRow", lastTimeFrontRow);
        properties.setProperty("PreviousTimeFrontRow", previousTimeFrontRow);
        properties.setProperty("BeforePreviousTimeFrontRow", beforePreviousTimeFrontRow);
        properties.setProperty("LastTimeBehindRow", lastTimeBehindRow);
        properties.setProperty("PreviousTimeBehindRow", previousTimeBehindRow);
        properties.setProperty("BeforePreviousTimeBehindRow", beforePreviousTimeBehindRow);
        //使用Properties生成配置文件
        properties.store(new FileWriter(root + "\\Properties\\" + propertiesName + ".properties"), null);
    }
}
