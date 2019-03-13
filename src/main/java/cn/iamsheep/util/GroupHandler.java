package cn.iamsheep.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

public class GroupHandler {
    private Group group;
    private Student[][] place;
    private ArrayList<Student> studentsList;
    private ArrayList<Student> groupOneList;
    private ArrayList<Student> groupTwoList;
    private ArrayList<Student> groupThreeList;

    public GroupHandler(Group group) {
        setGroup(group);
    }

    public void setGroup(Group group) {
        this.group = group;
        if (place != null || studentsList != null) {
            place = null;
            studentsList = null;
            groupOneList = null;
            groupTwoList = null;
            groupThreeList = null;
        }
        place = new Student[group.getPlace().length][];
        for (int i = 0; i < group.getPlace().length; i++) {
            place[i] = group.getPlace()[i].clone();
        }
        studentsList = new ArrayList<>(group.getStudentsList());
        groupOneList = new ArrayList<>(group.getGroupOneList());
        groupTwoList = new ArrayList<>(group.getGroupTwoList());
        groupThreeList = new ArrayList<>(group.getGroupThreeList());
    }

    /**
     * 刷新座位表
     *
     * @param mode 刷新模式
     */
    public synchronized void sync(Mode mode) {
        Random random = new Random();
        switch (mode) {
            case NINE:
                syncWithNineProtect(random);
                break;
            case GROUP:
                syncWithGroupProtect(random);
                break;
        }
    }

    /**
     * 九宫格保护
     *
     * @param random
     */
    private void syncWithNineProtect(Random random) {
        Student ranStudent;
        int ranIndex;
        for (int i = 0; i < place.length; i++) {
            for (int j = 0; j < place[i].length; j++) {
                if (i == 5 && j == 8) continue;
                int count = 0;
                while (!studentsList.isEmpty()) {
                    ranIndex = random.nextInt(studentsList.size());
                    ranStudent = studentsList.get(ranIndex);
                    if (!isEqualsOneof(ranStudent, getLastStudentList(new Position(i, j))) || count == 1000) {
                        place[i][j] = ranStudent;
                        studentsList.remove(ranIndex);
                        break;
                    }
                    count++;
                }
            }
        }
        group.setPlace(place);
    }

    /**
     * 分组保护
     *
     * @param random
     */
    private void syncWithGroupProtect(Random random) {
        int ranIndex;
        Student[] newGroupOne = new Student[groupOneList.size()];
        Student[] newGroupTwo = new Student[groupTwoList.size()];
        Student[] newGroupThree =new Student[groupThreeList.size()];
        for (int i = 0; i < newGroupOne.length; i++) {
            if (random.nextBoolean()) {
                ranIndex = random.nextInt(groupTwoList.size());
                newGroupOne[i] = groupTwoList.get(ranIndex);
                groupTwoList.remove(ranIndex);
            } else {
                ranIndex = random.nextInt(groupThreeList.size());
                newGroupOne[i] = groupThreeList.get(ranIndex);
                groupThreeList.remove(ranIndex);
            }
        }
        for (int i = 0; i < newGroupTwo.length; i++) {
            if(groupThreeList.size()!= 0){
                ranIndex = random.nextInt(groupThreeList.size());
                newGroupTwo[i] = groupThreeList.get(ranIndex);
                groupThreeList.remove(ranIndex);
            }else{
                ranIndex = random.nextInt(groupOneList.size());
                newGroupTwo[i] = groupOneList.get(ranIndex);
                groupOneList.remove(ranIndex);
            }
        }
        for (int i = 0; i < newGroupThree.length; i++) {
            if (random.nextBoolean() && groupOneList.size() != 0) {
                ranIndex = random.nextInt(groupOneList.size());
                newGroupThree[i] = groupOneList.get(ranIndex);
                groupOneList.remove(ranIndex);
            }else{
                ranIndex = random.nextInt(groupTwoList.size());
                newGroupThree[i] = groupTwoList.get(ranIndex);
                groupTwoList.remove(ranIndex);
            }
        }
        ArrayList<Student> temp = new ArrayList<>(Arrays.asList(newGroupTwo));
        for (int i = 0; i < place.length; i++) {
            for (int j = 0; j < place[i].length; j++) {
                int groupNum = Math.abs((j / 3) - 3);
                if (i == 5 && j == 8) continue;
                if (i == 6 && j == 2) break;
                if (groupNum == 3) {
                    place[i][j] = newGroupThree[j + 3 * i];
                } else if (groupNum == 2) {
                    int index = random.nextInt(temp.size());
                    place[i][j] = temp.get(index);
                    temp.remove(index);
                } else {
                    place[i][j] = newGroupOne[(j - 6) + 3 * i];
                }
            }
        }
        group.setPlace(place);
    }

    /**
     * 判断是否存在该学生
     *
     * @param name 学生名字
     * @return boolean
     */
    private boolean isExistStudent(String name) {
        for (Student student : studentsList) {
            if (name.equals(student.getName())) return true;
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
                if (place[i][j].getName().equals(name)) return new Position(i, j);
            }
        }
        return null;
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
                result.add(place[Math.abs(i)][Math.abs(j)]);
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
     * @throws ExchangeException
     */
    public void exchange(String nameOne, String nameTwo) throws ExchangeException {
        if (isExistStudent(nameOne) && isExistStudent(nameTwo)) {
            Position one = getPositionFromName(nameOne);
            Position two = getPositionFromName(nameTwo);
            if ((one.getX() == two.getX()) && ((one.getY() / 3) == (two.getY() / 3)) && Math.abs(one.getY() - two.getY()) <= 2) {
                Student temp = place[one.getX()][one.getY()];
                place[one.getX()][one.getY()] = place[two.getX()][two.getY()];
                place[two.getX()][two.getY()] = temp;
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
            Student temp = place[one.getX()][one.getY()];
            place[one.getX()][one.getY()] = place[two.getX()][two.getY()];
            place[two.getX()][two.getY()] = temp;
        } else {
            throw new ExchangeException("该学生不存在!");
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
