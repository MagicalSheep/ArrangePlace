package cn.iamsheep;

import cn.iamsheep.util.Mode;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Random;

public class Group implements Serializable {
    private String[][] place; // 座位表
    private ArrayList<String> studentsName; // 学生名单

    public Group(ArrayList<String> studentsName) {
        this.studentsName = studentsName;
        this.place = new String[7][9];
        int index = 0;
        for (int i = 0; i < place.length; i++) {
            for (int j = 0; j < place[i].length; j++) {
                if (i == 5 && j == 8) {
                    place[i][j] = "　　　";
                    break;
                }
                if (index < studentsName.size()) {
                    place[i][j] = studentsName.get(index++);
                } else {
                    place[i][j] = "　　　";
                }
            }
        } // 初始化座位表
    }

    /**
     * 判断是否存在该学生
     *
     * @param name 学生名字
     * @return boolean
     */
    private boolean isExistStudent(String name) {
        for (String s : studentsName) {
            if (name.equals(s)) return true;
        }
        return false;
    }

    /**
     * 根据学生名字返回其在座位表中的坐标
     *
     * @param name 学生名字
     * @return Position
     */
    private Position getPositionFromName(String name) {
        for (int i = 0; i < place.length; i++) {
            for (int j = 0; j < place[i].length; j++) {
                if (place[i][j].equals(name)) return new Position(i, j);
            }
        }
        return null;
    }

    /**
     * 获取上一次该座位九宫格区域内所有的学生名单
     *
     * @param position 座位的坐标对象
     * @return ArrayList<String>
     */
    private ArrayList<String> getLastNameList(Position position) {
        int x = position.getX();
        int y = position.getY();
        ArrayList<String> result = new ArrayList<>();
        for (int i = (x - 1); i < (x + 1); i++) {
            for (int j = (y - 1); j < (y + 1); j++) {
                result.add(place[Math.abs(i)][Math.abs(j)]);
            }
        }
        return result;
    }

    /**
     * 获取上一次该组内所有的学生名单
     *
     * @param position 座位的坐标对象
     * @return ArrayList<String>
     */
    private ArrayList<String> getLastGroupNameList(Position position) {
        int x = position.getX();
        int y = position.getY();
        ArrayList<String> result = new ArrayList<>();
        for (int i = 0; i < place.length; i++) {
            for (int j = 0; j < place[i].length; j++) {
                if (i == x && j == y) break;
                if ((j % 3) == (y % 3)) result.add(place[i][j]);
            }
        }
        return result;
    }

    /**
     * 判断该名字是否等于名单中的其中一个名字
     *
     * @param name     要判断的名字
     * @param nameList 作对比的名单
     * @return boolean
     */
    private boolean isEqualsOneof(String name, ArrayList<String> nameList) {
        for (String s : nameList) {
            if (name.equals(s)) return true;
        }
        return false;
    }

    /**
     * 刷新座位表
     *
     * @param mode 刷新模式
     */
    public synchronized void sync(Mode mode) {
        Random random = new Random();
        ArrayList<String> name = new ArrayList<>(this.studentsName);
        switch (mode) {
            case NINE:
                syncWithNineProtect(random, name);
                break;
            case GROUP:
                syncWithGroupProtect(random, name);
                break;
        }
    }

    private void syncWithNineProtect(Random random, ArrayList<String> name) {
        String[][] tempPlace = new String[7][9];
        for (int i = 0; i < tempPlace.length; i++) {
            for (int j = 0; j < tempPlace[i].length; j++) {
                tempPlace[i][j] = "　　　";
            }
        }
        String ranName;
        int ranIndex;
        for (int i = 0; i < place.length; i++) {
            for (int j = 0; j < place[i].length; j++) {
                if (i == 5 && j == 8) continue;
                int count = 0;
                while (!name.isEmpty()) {
                    ranIndex = random.nextInt(name.size());
                    ranName = name.get(ranIndex);
                    if (!isEqualsOneof(ranName, getLastNameList(new Position(i, j))) || count == 100) {
                        tempPlace[i][j] = ranName;
                        name.remove(ranIndex);
                        break;
                    }
                    count++;
                }
            }
        }
        place = tempPlace;
    }

    private void syncWithGroupProtect(Random random, ArrayList<String> name) {
        String[][] tempPlace = new String[7][9];
        for (int i = 0; i < tempPlace.length; i++) {
            for (int j = 0; j < tempPlace[i].length; j++) {
                tempPlace[i][j] = "　　　";
            }
        }
        String ranName;
        int ranIndex;
        int count = 0;
        for (int i = 0; i < place.length; i++) {
            for (int j = 0; j < place[i].length; j++) {
                int groupNum = j % 3;
                if (i == 5 && j == 8) continue;
                while (!name.isEmpty()) {
                    ranIndex = random.nextInt(name.size());
                    ranName = name.get(ranIndex);
                    if (groupNum != (getPositionFromName(ranName).getY() % 3) || count == 100) {
                        tempPlace[i][j] = ranName;
                        name.remove(ranIndex);
                        break;
                    }
                    count++;
                }
            }
        }
        place = tempPlace;
    }

    /**
     * 调换座位
     *
     * @param nameOne 名字1
     * @param nameTwo 名字2
     * @throws ExchangeException
     */
    public void exchange(String nameOne, String nameTwo) throws ExchangeException {
        if (isExistStudent(nameOne) && isExistStudent(nameTwo)) {
            Position one = getPositionFromName(nameOne);
            Position two = getPositionFromName(nameTwo);
            if ((one.getX() == two.getX()) && ((one.getY() / 3) == (two.getY() / 3)) && Math.abs(one.getY() - two.getY()) <= 2) {
                place[one.getX()][one.getY()] = nameTwo;
                place[two.getX()][two.getY()] = nameOne;
            } else {
                throw new ExchangeException("调换座位的条件不符合！");
            }
        } else {
            throw new ExchangeException("该学生不存在!");
        }
    }

    /**
     * 强制调换座位
     *
     * @param nameOne 名字1
     * @param nameTwo 名字2
     * @throws ExchangeException
     */
    public void adminExchange(String nameOne, String nameTwo) throws ExchangeException {
        if (isExistStudent(nameOne) && isExistStudent(nameTwo)) {
            Position one = getPositionFromName(nameOne);
            Position two = getPositionFromName(nameTwo);
            place[one.getX()][one.getY()] = nameTwo;
            place[two.getX()][two.getY()] = nameOne;
        } else {
            throw new ExchangeException("该学生不存在!");
        }
    }

    public String[][] getPlace() {
        return place;
    }

    public ArrayList<String> getStudentsNameList() {
        return studentsName;
    }

    /**
     * 坐标类
     */
    class Position {
        private int x;
        private int y;

        Position(int x, int y) {
            this.x = x;
            this.y = y;
        }

        int getX() {
            return x;
        }

        int getY() {
            return y;
        }
    }

    /**
     * 调换座位的异常类
     */
    public class ExchangeException extends Exception {
        ExchangeException(String message) {
            super(message);
        }
    }

}
