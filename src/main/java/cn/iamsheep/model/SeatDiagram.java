package cn.iamsheep.model;

import cn.iamsheep.model.property.Position;

import java.io.Serializable;
import java.util.ArrayList;

public class SeatDiagram implements Serializable {
    private ArrayList<Student> studentsList;//学生名单
    private ArrayList<Student> groupOneList = new ArrayList<>();
    private ArrayList<Student> groupTwoList = new ArrayList<>();
    private ArrayList<Student> groupThreeList = new ArrayList<>();
    private Student[][] seat;//座位表

    public SeatDiagram(ArrayList<Student> studentsList) {
        this.studentsList = studentsList;
        this.seat = new Student[7][9];
        int index = 0;
        for (int i = 0; i < seat.length; i++) {
            for (int j = 0; j < seat[i].length; j++) {
                if (i == 5 && j == 8) {
                    seat[i][j] = new Student("　　　", null);
                    break;
                }
                if (index < studentsList.size()) {
                    Student student = studentsList.get(index++);
                    student.setPosition(new Position(i, j));
                    seat[i][j] = student;
                    if (j / 3 == 0) {
                        groupThreeList.add(student);
                    } else if (j / 3 == 1) {
                        groupTwoList.add(student);
                    } else {
                        groupOneList.add(student);
                    }
                } else {
                    seat[i][j] = new Student("　　　", null);
                }
            }
        } // 初始化座位表
    }

    public ArrayList<Student> getGroupOneList() {
        return groupOneList;
    }

    public ArrayList<Student> getGroupTwoList() {
        return groupTwoList;
    }

    public ArrayList<Student> getGroupThreeList() {
        return groupThreeList;
    }

    public ArrayList<Student> getStudentsList() {
        return studentsList;
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
        groupThreeList.clear();
        groupTwoList.clear();
        groupOneList.clear();
        for (int i = 0; i < seat.length; i++) {
            for (int j = 0; j < seat[i].length; j++) {
                if (i == 5 && j == 8) continue;
                if (j / 3 == 0) {
                    groupThreeList.add(seat[i][j]);
                } else if (j / 3 == 1) {
                    groupTwoList.add(seat[i][j]);
                } else {
                    groupOneList.add(seat[i][j]);
                }
            }
        }
    }
}
