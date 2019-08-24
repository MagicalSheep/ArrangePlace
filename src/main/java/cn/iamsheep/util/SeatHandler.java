package cn.iamsheep.util;

import cn.iamsheep.model.SeatDiagram;
import cn.iamsheep.model.property.Position;
import cn.iamsheep.model.Student;

import java.io.IOException;
import java.util.*;

public class SeatHandler {
    private SeatDiagram currentSeat; // 当前的座位表
    private SeatDiagram lastSeat; // 上一次的座位表
    private SeatDiagram previousSeat; // 上上次的座位表
    private SeatDiagram resultSeat; // 新座位表

    private StudentRandom random;

    public SeatHandler() throws IOException, ClassNotFoundException {
        init();
        random = new StudentRandom();
    }

    public void init() throws IOException, ClassNotFoundException {
        currentSeat = FileHandler.openSeatDiagram();
        resultSeat = FileHandler.openSeatDiagram();
        lastSeat = FileHandler.openSeatDiagram(1);
        previousSeat = FileHandler.openSeatDiagram(2);
    }

    /**
     * 计算某学生在该位置上所得的权重
     *
     * @param student 学生对象
     * @param selectedPos 要判断的位置坐标对象
     * @param ruleMap 加权规则
     * @return 权重
     * @throws Exception Exception
     */
    private int countWeight(Student student, Position selectedPos, HashMap<String, Integer> ruleMap) throws Exception {
        int weight = 1;
        if (ruleMap.get("LastTimeSameGroup") != -1) {
            if ((selectedPos.getY() / 3) == (getPositionFromName(student.getName(), 1).getY() / 3)) {
                if (ruleMap.get("LastTimeSameGroup") == 0) return 0;
                weight += ruleMap.get("LastTimeSameGroup");
            }
        }
        if (ruleMap.get("PreviousTimeSameGroup") != -1) {
            if ((selectedPos.getY() / 3) == (getPositionFromName(student.getName(), 2).getY() / 3)) {
                if (ruleMap.get("PreviousTimeSameGroup") == 0) return 0;
                weight += ruleMap.get("PreviousTimeSameGroup");
            }
        }
        if (ruleMap.get("BeforePreviousTimeSameGroup") != -1) {
            if ((selectedPos.getY() / 3) == (getPositionFromName(student.getName(), 3).getY() / 3)) {
                if (ruleMap.get("BeforePreviousTimeSameGroup") == 0) return 0;
                weight += ruleMap.get("BeforePreviousTimeSameGroup");
            }
        }
        if (ruleMap.get("LastTimeNotThisGroup") != -1) {
            if ((selectedPos.getY() / 3) != (getPositionFromName(student.getName(), 1).getY() / 3)) {
                if (ruleMap.get("LastTimeNotThisGroup") == 0) return 0;
                weight += ruleMap.get("LastTimeNotThisGroup");
            }
        }
        if (ruleMap.get("PreviousTimeNotThisGroup") != -1) {
            if ((selectedPos.getY() / 3) != (getPositionFromName(student.getName(), 2).getY() / 3)) {
                if (ruleMap.get("PreviousTimeNotThisGroup") == 0) return 0;
                weight += ruleMap.get("PreviousTimeNotThisGroup");
            }
        }
        if (ruleMap.get("BeforePreviousTimeNotThisGroup") != -1) {
            if ((selectedPos.getY() / 3) != (getPositionFromName(student.getName(), 3).getY() / 3)) {
                if (ruleMap.get("BeforePreviousTimeNotThisGroup") == 0) return 0;
                weight += ruleMap.get("BeforePreviousTimeNotThisGroup");
            }
        }
        if (ruleMap.get("LastTimeFrontRow") != -1) {
            if (getPositionFromName(student.getName(), 1).getX() <= (currentSeat.getSeat().length / 2)) {
                if (ruleMap.get("LastTimeFrontRow") == 0) return 0;
                if (selectedPos.getX() > (currentSeat.getSeat().length / 2)) {
                    weight += ruleMap.get("LastTimeFrontRow");
                }
            }
        }
        if (ruleMap.get("PreviousTimeFrontRow") != -1) {
            if (getPositionFromName(student.getName(), 2).getX() <= (currentSeat.getSeat().length / 2)) {
                if (ruleMap.get("PreviousTimeFrontRow") == 0) return 0;
                if (selectedPos.getX() > (currentSeat.getSeat().length / 2)) {
                    weight += ruleMap.get("PreviousTimeFrontRow");
                }
            }
        }
        if (ruleMap.get("BeforePreviousTimeFrontRow") != -1) {
            if (getPositionFromName(student.getName(), 3).getX() <= (currentSeat.getSeat().length / 2)) {
                if (ruleMap.get("BeforePreviousTimeFrontRow") == 0) return 0;
                if (selectedPos.getX() > (currentSeat.getSeat().length / 2)) {
                    weight += ruleMap.get("BeforePreviousTimeFrontRow");
                }
            }
        }
        if (ruleMap.get("LastTimeBehindRow") != -1){
            if(getPositionFromName(student.getName(), 1).getX() > (currentSeat.getSeat().length / 2)) {
                if(ruleMap.get("LastTimeBehindRow") == 0) return 0;
                if(selectedPos.getX() <= (currentSeat.getSeat().length / 2)) {
                    weight += ruleMap.get("LastTimeBehindRow");
                }
            }
        }
        if (ruleMap.get("PreviousTimeBehindRow") != -1){
            if(getPositionFromName(student.getName(), 2).getX() > (currentSeat.getSeat().length / 2)) {
                if(ruleMap.get("PreviousTimeBehindRow") == 0) return 0;
                if(selectedPos.getX() <= (currentSeat.getSeat().length / 2)) {
                    weight += ruleMap.get("PreviousTimeBehindRow");
                }
            }
        }
        if (ruleMap.get("BeforePreviousTimeBehindRow") != -1){
            if(getPositionFromName(student.getName(), 3).getX() > (currentSeat.getSeat().length / 2)) {
                if(ruleMap.get("BeforePreviousTimeBehindRow") == 0) return 0;
                if(selectedPos.getX() <= (currentSeat.getSeat().length / 2)) {
                    weight += ruleMap.get("BeforePreviousTimeBehindRow");
                }
            }
        }
        return weight;
    }

    /**
     * 刷新座位表
     *
     * @param ruleMap 加权规则
     * @throws Exception Exception
     */
    public synchronized void sync(HashMap<String, Integer> ruleMap) throws Exception {
        init(); // 初始化各座位表
        ArrayList<Student> studentList = new ArrayList<>(resultSeat.getStudentsList()); // 临时学生名单
        // 开始遍历座位表
        for (int i = 0; i < resultSeat.getSeat().length; i++) {
            for (int j = 0; j < resultSeat.getSeat()[i].length; j++) {
                if (i == 5 && j == 8) continue;
                if (resultSeat.getSeat()[i][j].getName().equals("　　　")) break;
                HashMap<Student, Integer> studentWeightMap = new HashMap<>(); // 学生 - 权重 临时列表
                for (Student student : studentList) {
                    studentWeightMap.put(student, countWeight(student, new Position(i, j), ruleMap)); // 计算每位学生的权重，并放入临时列表
                }
                HashMap<Integer, ArrayList<Student>> studentsList = new HashMap<>(); // 权重 - 对应学生名单 临时列表
                HashSet<Integer> weightSet = new HashSet<>(); // 权重HashSet
                // 遍历 学生 - 权重 临时列表
                for (Map.Entry<Student, Integer> entry : studentWeightMap.entrySet()) {
                    weightSet.add(entry.getValue()); // 将所有的权重类型放入 权重HashSet
                }
                ArrayList<Integer> tempArray = new ArrayList<>(weightSet); // 将 权重HashSet 转为List
                Collections.sort(tempArray); // 对List进行排序
                HashSet<Integer> sortedWeightSet = new HashSet<>(tempArray); // 将排序后的List转为 权重HashSet
                // 遍历 排序后的权重HashSet
                for (Integer weight : sortedWeightSet) {
                    ArrayList<Student> students = new ArrayList<>(); // 对应权重的学生名单
                    // 遍历 学生 - 权重 临时列表
                    for (Map.Entry<Student, Integer> entry : studentWeightMap.entrySet()) {
                        if (entry.getValue().equals(weight)) {
                            students.add(entry.getKey()); // 将所有符合该权重的学生添加至 对应权重的学生名单
                        }
                    }
                    studentsList.put(weight, students); // 构建 权重 - 对应学生名单 临时列表
                }
                // 抽取学生
                Student tempStudent = random.nextStudent(studentsList);
                // 安排座位
                resultSeat.setSeat(i, j, tempStudent);
                // 将该学生从 临时学生名单 移除
                studentList.remove(tempStudent);
            }
        }
    }

    /**
     * 判断是否存在该学生
     *
     * @param name 学生名字
     * @return boolean
     */
    private boolean isExistStudent(String name) {
        for (Student student : currentSeat.getStudentsList()) {
            if (name.equals(student.getName())) return true;
        }
        return false;
    }


    /**
     * 根据学生名字返回其在座位表中的坐标
     *
     * @param name 学生名字
     * @param index 座位表序号（[0] 当前 [1] 上一次 [2] 上上次 [3]上上上次）
     * @return 坐标对象
     * @throws Exception Exception
     */
    private Position getPositionFromName(String name, int index) throws Exception {
        SeatDiagram tempSeatDiagram;
        switch (index){
            case 0:
                tempSeatDiagram = resultSeat;
                break;
            case 1:
                tempSeatDiagram = currentSeat;
                break;
            case 2:
                tempSeatDiagram = lastSeat;
                break;
            case 3:
                tempSeatDiagram = previousSeat;
                break;
            default:
                throw new Exception("Valid index");
        }
        for (int i = 0; i < tempSeatDiagram.getSeat().length; i++) {
            for (int j = 0; j < tempSeatDiagram.getSeat()[i].length; j++) {
                if (tempSeatDiagram.getSeat()[i][j].getName().equals(name)) return new Position(i, j);
            }
        }
        return null;
    }

    /**
     * 根据学生名字返回其在当前座位表中的坐标
     *
     * @param name 学生名字
     * @return 坐标对象
     * @throws Exception Exception
     */
    private Position getPositionFromName(String name) throws Exception {
        return getPositionFromName(name,0);
    }

    /**
     * 获取上一次该座位九宫格区域内所有的学生名单
     *
     * @param position 座位的坐标对象
     * @return ArrayList<Student>
     */
    private ArrayList<Student> getLastStudentList(Position position) {
        int x = position.getX();
        int y = position.getY();
        ArrayList<Student> result = new ArrayList<>();
        for (int i = (x - 1); i < (x + 1); i++) {
            for (int j = (y - 1); j < (y + 1); j++) {
                result.add(currentSeat.getSeat()[Math.abs(i)][Math.abs(j)]);
            }
        }
        return result;
    }

    /**
     * 判断该学生是否等于名单中的其中一个学生
     *
     * @param student 要判断的学生
     * @param nameList 作对比的名单
     * @return boolean
     */
    private boolean isEqualsOneof(Student student, ArrayList<Student> nameList) {
        for (Student s : nameList) {
            if (student.getName().equals(s.getName())) return true;
        }
        return false;
    }

    /**
     * 调换座位
     *
     * @param nameOne 名字1
     * @param nameTwo 名字2
     * @throws Exception Exception
     */
    public void exchange(String nameOne, String nameTwo) throws Exception {
        if (isExistStudent(nameOne) && isExistStudent(nameTwo)) {
            Position one = getPositionFromName(nameOne);
            Position two = getPositionFromName(nameTwo);
            if ((one.getX() == two.getX()) && ((one.getY() / 3) == (two.getY() / 3)) && Math.abs(one.getY() - two.getY()) <= 2) {
                Student temp = resultSeat.getSeat()[one.getX()][one.getY()];
                resultSeat.setSeat(one, resultSeat.getSeat()[two.getX()][two.getY()]);
                resultSeat.setSeat(two, temp);
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
     * @throws Exception Exception
     */
    public void adminExchange(String nameOne, String nameTwo) throws Exception {
        if (isExistStudent(nameOne) && isExistStudent(nameTwo)) {
            Position one = getPositionFromName(nameOne);
            Position two = getPositionFromName(nameTwo);
            Student temp = resultSeat.getSeat()[one.getX()][one.getY()];
            resultSeat.setSeat(one, resultSeat.getSeat()[two.getX()][two.getY()]);
            resultSeat.setSeat(two, temp);
        } else {
            throw new ExchangeException("该学生不存在!");
        }
    }

    /**
     * 获取新座位表
     *
     * @return 新座位表
     */
    public SeatDiagram getResultSeat() {
        return resultSeat;
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
