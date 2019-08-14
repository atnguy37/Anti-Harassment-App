package com.asu.cse535.group_number_420.Assignment2;

public class PersonInfo {

    private static String name;
    private static String age;
    private static String sex;
    private static String id;

    private PersonInfo() {}

    public static String getAge() {
        return age;
    }

    public static void setAge(String age) {
        PersonInfo.age = age;
    }

    public static String getSex() {
        return sex;
    }

    public static void setSex(String sex) {
        PersonInfo.sex = sex;
    }

    public static String getId() {
        return id;
    }

    public static void setId(String id) {
        PersonInfo.id = id;
    }

    public PersonInfo(String name, String age, String sex, String id) {
        this.name = name;
        this.age = age;
        this.sex = sex;
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }


}
