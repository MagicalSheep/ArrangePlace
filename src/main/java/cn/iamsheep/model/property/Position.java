package cn.iamsheep.model.property;

import java.io.Serializable;

/**
 * 坐标类
 */
public class Position implements Serializable {
    private int x;
    private int y;

    public Position(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public boolean equal(Position position) {
        return x == position.getX() && y == position.getY();
    }
}