package cn.iamsheep.base;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

public class StudentRandom extends Random {

    public Student nextStudent(HashMap<Integer, ArrayList<Student>> studentsList) {
        int probabilitySum = 0;
        for (Object weight : studentsList.keySet()) {
            probabilitySum += (Integer) weight;
        }
        int randomNum = nextInt(probabilitySum) + 1;
        int sum = 0;
        for (Object weight : studentsList.keySet()) {
            for (int j = 0; j < (Integer) weight; j++) {
                sum++;
            }
            if (sum >= randomNum) {
                ArrayList<Student> selectedList = studentsList.get(weight);
                return selectedList.get(nextInt(selectedList.size()));
            }
        }
        return null;
    }
}
