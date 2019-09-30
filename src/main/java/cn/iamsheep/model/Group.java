package cn.iamsheep.model;

import java.io.Serializable;
import java.util.ArrayList;

public class Group implements Serializable {

    private ArrayList<Student> groupList;
    private int column; // 列数

    public Group(int column){
        this.column = column;
        groupList = new ArrayList<>();
    }

    public void add(Student student){
        groupList.add(student);
    }

    public int getStudentNum(){
        return groupList.size();
    }

    public int getColumn(){
        return column;
    }

}
