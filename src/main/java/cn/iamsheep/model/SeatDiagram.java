package cn.iamsheep.model;

import cn.iamsheep.model.property.Position;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

public class SeatDiagram implements Serializable {
    private int row; // 表行数
    private int column; // 表列数
    private int groupNum; // 组数
    private HashMap<Integer, Group> group = new HashMap<>(); // 组
    private ArrayList<Student> studentsList; // 学生名单
    private ArrayList<Position> emptySeat; // 空位置列表
    private Student[][] seat; // 座位表

    public SeatDiagram(ArrayList<Student> studentsList, int row, int column, int groupNum, int[] groupColumn, Position[] emptyPosition) {
        this.row = row;
        this.column = column;
        this.groupNum = groupNum;
        this.studentsList = studentsList;
        this.seat = new Student[row][column];
        int index = 0;

        emptySeat = new ArrayList<>();
        emptySeat.addAll(Arrays.asList(emptyPosition));

        for (int i = 1; i <= groupNum; i++) {
            group.put(i, new Group(groupColumn[i - 1]));
        }

        for (int i = 0; i < seat.length; i++) {
            for (int j = 0; j < seat[i].length; j++) {
                if (isEmptyPosition(new Position(i, j))) continue;
                if (index < studentsList.size()) {
                    Student student = studentsList.get(index++);
                    student.setPosition(new Position(i, j));
                    seat[i][j] = student;
                    groupStudent(i, j);
                } else {
                    emptySeat.add(new Position(i,j));
                }
            }
        } // 初始化座位表
    }

    /**
     * 是否为空座位
     *
     * @param position 坐标
     * @return boolean
     */
    public boolean isEmptyPosition(Position position){
        for (Position value : emptySeat) {
            if (position.equal(value)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 将学生分组
     *
     * @param i 行号
     * @param j 列号
     */
    private void groupStudent(int i, int j){
        int temp = j;
        for (Object num : group.keySet()) {
            int columnNum = group.get(num).getColumn();
            if ((temp / columnNum) != 0) {
                temp -= columnNum;
            }else{
                group.get(num).add(seat[i][j]);
            }
        }
    }

    public Student[][] getSeat() {
        return seat;
    }


    public void setSeat(Student[][] place) {
        this.seat = place;
        update();
    }

    public void setSeat(int x, int y, Student student) {
        seat[x][y] = student;
        update();
    }

    public void setSeat(Position position, Student student) {
        seat[position.getX()][position.getY()] = student;
        update();
    }

    private void update() {
        for (int i = 0; i < seat.length; i++) {
            for (int j = 0; j < seat[i].length; j++) {
                if (isEmptyPosition(new Position(i, j))) continue;
                groupStudent(i,j);
            }
        }
    }

    public int getGroupNum(Student student){
        return getGroupNum(student.getPosition());
    }

    public int getGroupNum(int x, int y) {
        int groupNum = 1;
        for (Object num : group.keySet()) {
            int columnNum = group.get(num).getColumn();
            if ((y / columnNum) != 0) {
                y -= columnNum;
                groupNum++;
            } else {
                break;
            }
        }
        return groupNum;
    }

    public int getGroupNum(Position position) {
        return getGroupNum(position.getX(), position.getY());
    }

    public ArrayList<Student> getStudentsList() {
        return studentsList;
    }

    public int getRow() {
        return row;
    }

    public int getColumn() {
        return column;
    }

    public int getGroupNum() {
        return groupNum;
    }

    public ArrayList<Position> getEmptySeat() {
        return emptySeat;
    }

    public Group getGroup(int num) {
        return group.get(num);
    }

    public void setRow(int row) {
        this.row = row;
    }

    public void setColumn(int column) {
        this.column = column;
    }

    public void setGroupNum(int groupNum) {
        this.groupNum = groupNum;
    }
}
