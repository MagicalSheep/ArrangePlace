package cn.iamsheep.util;

import cn.iamsheep.model.Student;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

class StudentRandom extends Random {

    Student nextStudent(HashMap<Integer, ArrayList<Student>> studentsList) {
        int probabilitySum = 0; // 权重总和
        // 遍历 权重 - 对应学生名单 列表
        for (Object weight : studentsList.keySet()) {
            System.out.print("权重为" + weight + "的学生有：");
            for(Student student : studentsList.get(weight)){
                System.out.print(student.getName() + " ");
            }
            System.out.println("");
            probabilitySum += (Integer) weight; // 计算权重总和
        }
        System.out.println("");
        int randomNum = (probabilitySum == 0) ? 0 : nextInt(probabilitySum) + 1; // 生成范围为 1 - (权重总和 + 1) 的随机数
        int sum = 0;
        // 遍历 权重 - 对应学生名单 列表
        for (Object weight : studentsList.keySet()) {
            for (int j = 0; j < (Integer) weight; j++) {
                sum++; // 权重为多少，sum 就自增多少
            }
            if (sum >= randomNum) {
                ArrayList<Student> selectedList = studentsList.get(weight);
                return selectedList.get(nextInt(selectedList.size()));
            }
        }
        return null;
    }
}
