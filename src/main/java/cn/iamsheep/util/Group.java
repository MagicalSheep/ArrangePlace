package cn.iamsheep.util;

import cn.iamsheep.base.Position;
import cn.iamsheep.base.Student;

import java.io.Serializable;
import java.util.ArrayList;

public class Group implements Serializable {
    private ArrayList<Student> studentsList;//学生名单
    private ArrayList<Student> groupOneList = new ArrayList<>();
    private ArrayList<Student> groupTwoList = new ArrayList<>();
    private ArrayList<Student> groupThreeList = new ArrayList<>();
    private Student[][] place;//座位表

    public Group(ArrayList<Student> studentsList) {
        this.studentsList = studentsList;
        this.place = new Student[7][9];
        int index = 0;
        for (int i = 0; i < place.length; i++) {
            for (int j = 0; j < place[i].length; j++) {
                if (i == 5 && j == 8) {
                    place[i][j] = new Student("　　　", null);
                    break;
                }
                if (index < studentsList.size()) {
                    Student student = studentsList.get(index++);
                    student.setPosition(new Position(i, j));
                    place[i][j] = student;
                    if (j / 3 == 0) {
                        groupThreeList.add(student);
                    } else if (j / 3 == 1) {
                        groupTwoList.add(student);
                    } else {
                        groupOneList.add(student);
                    }
                } else {
                    place[i][j] = new Student("　　　", null);
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

    public Student[][] getPlace() {
        return place;
    }

    public void setPlace(Student[][] place) {
        this.place = place;
        update();
    }

    private void update() {
        groupThreeList.clear();
        groupTwoList.clear();
        groupOneList.clear();
        for (int i = 0; i < place.length; i++) {
            for (int j = 0; j < place[i].length; j++) {
                if (i == 5 && j == 8) continue;
                if (i == 6 && j == 2) break;
                if (j / 3 == 0) {
                    groupThreeList.add(place[i][j]);
                } else if (j / 3 == 1) {
                    groupTwoList.add(place[i][j]);
                } else {
                    groupOneList.add(place[i][j]);
                }
            }
        }
    }
}
