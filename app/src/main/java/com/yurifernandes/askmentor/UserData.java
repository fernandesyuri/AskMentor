package com.yurifernandes.askmentor;

public class UserData {

    private static UserData instance;

    public String id;
    public String userName;
    public String name;

    private UserData() {

    }

    public static synchronized UserData getInstance() {
        if(instance == null) {
            instance = new UserData();
        }
        return instance;
    }
}
