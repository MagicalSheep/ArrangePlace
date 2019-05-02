package cn.iamsheep.model;

import cn.iamsheep.model.property.Position;
import cn.iamsheep.model.property.Sex;

import java.io.Serializable;

public class Student implements Serializable {
    private String name;
    private Sex sex;
    private Position position;

    public Student(String name, Sex sex) {
        this.name = name;
        this.sex = sex;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setSex(Sex sex) {
        this.sex = sex;
    }

    public String getName() {
        return name;
    }

    public Sex getSex() {
        return sex;
    }

    public Position getPosition() {
        return position;
    }

    public void setPosition(Position position) {
        this.position = position;
    }
}
