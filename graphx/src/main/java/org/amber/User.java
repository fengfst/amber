package org.amber;

import java.util.List;

public class User {
    private String name;
    private List<User> userList;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<User> getUserList() {
        return userList;
    }

    public void setUserList(List<User> userList) {
        this.userList = userList;
    }

    public User(String name, List<User> userList) {
        this.name = name;
        this.userList = userList;
    }
}
