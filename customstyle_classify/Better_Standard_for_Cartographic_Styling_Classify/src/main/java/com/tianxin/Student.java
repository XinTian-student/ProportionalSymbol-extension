package com.tianxin;

/**
 * description：
 *
 * @Author X_T
 * @Date 05/07/2020 23:33
 * @Version V1.0
 **/
public class Student {
    private String name;
    public Student(String name){
        this.name = name;
    }

    public static void main(String[] args) {
        Student student = new Student("tianxin");
        System.out.println(student);
    }
}
