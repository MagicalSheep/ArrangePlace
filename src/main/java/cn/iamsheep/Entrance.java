package cn.iamsheep;

import java.io.*;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Scanner;

public class Entrance {

    private static Group group = null;
    private static Scanner scanner = null;

    public static void main(String[] args) throws Exception {
        if (new File("D://11/data.ser").exists()) {
            System.out.println("\n座位表读取成功！\n");
            group = readPlace();
        } else {
            group = new Group(readFile());
            System.out.println("\n学生名单读取成功！");
            savePlace(group);
            System.out.println("座位表已保存至本地！\n");
        }
        scanner = new Scanner(System.in);
        String command;
        while (true) {
            System.out.println("请输入命令：\n");
            if (scanner.hasNext()) {
                command = scanner.nextLine();
                switch (command.trim()) {
                    case "build":
                        group.sync();
                        System.out.println("\n座位表刷新成功！");
                        savePlace(group);
                        System.out.println("座位表已保存至本地！\n");
                        System.out.println("当前的座位表：\n");
                        printPlace(group);
                        break;
                    case "showPlace":
                        System.out.println("\n当前的座位表：\n");
                        printPlace(group);
                        break;
                    case "close":
                        System.exit(0);
                    case "help":
                        System.out.println("\nshowPlace  --  显示当前座位表\n\nbuild  --  刷新座位表\n\nexchange  --  调换座位\n\nclose  --  退出程序\n");
                        break;
                    case "exchange":
                        exchange();
                        break;
                    default:
                        break;
                }
            }
        }
    }

    private static void exchange() throws Exception {
        System.out.println("\n请输入要调换座位的两个姓名（两个名字之间用空格分开）：\n");
        String nameOne = scanner.next();
        String nameTwo = scanner.next();
        if (getChineseSize(nameOne) == 2) nameOne = nameOne + "　";
        if (getChineseSize(nameTwo) == 2) nameTwo = nameTwo + "　";
        try {
            group.exchange(nameOne, nameTwo);
            System.out.println("\n座位调换成功！");
            savePlace(group);
            System.out.println("座位表已保存至本地！\n");
        } catch (Group.ExchangeException e) {
            System.out.println("\n" + e.getMessage() + "\n");
        }
    }


    /**
     * 获取内容中汉字个数
     *
     * @param content 内容
     * @return int
     */
    private static int getChineseSize(String content) {
        String regex = "[\u4e00-\u9fa5]";
        return content.length() - content.replaceAll(regex, "").length();
    }

    private static void printPlace(Group group) {
        String[][] place = group.getPlace();
        for (String[] strings : place) {
            for (int j = 0; j < strings.length; j++) {
                System.out.print(strings[j] + "　");
                if (((j + 1) % 3 == 0)) System.out.print("　　");
            }
            System.out.print("\n");
        }
        System.out.print("\n");
    }

    private static Group readPlace() throws Exception {
        FileInputStream fileIn = new FileInputStream("D://11/data.ser");
        ObjectInputStream in = new ObjectInputStream(fileIn);
        Group group = (Group) in.readObject();
        in.close();
        fileIn.close();
        return group;
    }

    private static void savePlace(Group group) throws Exception {
        FileOutputStream fileOut = new FileOutputStream("D://11/data.ser");
        ObjectOutputStream out = new ObjectOutputStream(fileOut);
        out.writeObject(group);
        out.close();
        fileOut.close();
    }

    private static ArrayList<String> readFile() throws Exception {
        File file = new File("D://11/name.txt");
        ArrayList<String> studentsName = new ArrayList<>();
        BufferedReader bufferedReader = new BufferedReader(new UnicodeReader(new FileInputStream(file), Charset.defaultCharset().name()));
        String line;
        while ((line = bufferedReader.readLine()) != null) {
            studentsName.add(line);
        }
        return studentsName;
    }

    static class UnicodeReader extends Reader {
        PushbackInputStream internalIn;
        InputStreamReader internalIn2 = null;
        String defaultEnc;

        private static final int BOM_SIZE = 4;


        UnicodeReader(InputStream in, String defaultEnc) {
            internalIn = new PushbackInputStream(in, BOM_SIZE);
            this.defaultEnc = defaultEnc;
        }

        UnicodeReader(InputStream in) {
            internalIn = new PushbackInputStream(in, BOM_SIZE);
        }

        public String getDefaultEncoding() {
            return defaultEnc;
        }


        public String getEncoding() {
            if (internalIn2 == null) return null;
            return internalIn2.getEncoding();
        }


        protected void init() throws IOException {
            if (internalIn2 != null) return;

            String encoding;
            byte bom[] = new byte[BOM_SIZE];
            int n, unread;
            n = internalIn.read(bom, 0, bom.length);

            if ((bom[0] == (byte) 0x00) && (bom[1] == (byte) 0x00) &&
                    (bom[2] == (byte) 0xFE) && (bom[3] == (byte) 0xFF)) {
                encoding = "UTF-32BE";
                unread = n - 4;
            } else if ((bom[0] == (byte) 0xFF) && (bom[1] == (byte) 0xFE) &&
                    (bom[2] == (byte) 0x00) && (bom[3] == (byte) 0x00)) {
                encoding = "UTF-32LE";
                unread = n - 4;
            } else if ((bom[0] == (byte) 0xEF) && (bom[1] == (byte) 0xBB) &&
                    (bom[2] == (byte) 0xBF)) {
                encoding = "UTF-8";
                unread = n - 3;
            } else if ((bom[0] == (byte) 0xFE) && (bom[1] == (byte) 0xFF)) {
                encoding = "UTF-16BE";
                unread = n - 2;
            } else if ((bom[0] == (byte) 0xFF) && (bom[1] == (byte) 0xFE)) {
                encoding = "UTF-16LE";
                unread = n - 2;
            } else {
                // Unicode BOM mark not found, unread all bytes
                encoding = defaultEnc;
                unread = n;
            }
            //System.out.println("read=" + n + ", unread=" + unread);

            if (unread > 0) internalIn.unread(bom, (n - unread), unread);

            // Use given encoding
            if (encoding == null) {
                internalIn2 = new InputStreamReader(internalIn);
            } else {
                internalIn2 = new InputStreamReader(internalIn, encoding);
            }
        }

        public void close() throws IOException {
            init();
            internalIn2.close();
        }

        public int read(char[] cbuf, int off, int len) throws IOException {
            init();
            return internalIn2.read(cbuf, off, len);
        }

    }
}
