package org.niooii.jupiter;

import java.io.Serializable;
import java.util.ArrayList;

public class User implements Serializable {
    private String id;
    private final String osis;
    private final String password;
    private JupiterData jupiterData;
    private long lastUpdated;
    private ArrayList<String> toDoList;
    public User(String id, String osis, String password, JupiterData jupiterData, long lastUpdated){
        this.id = id;
        this.osis = osis;
        this.password = password;
        this.jupiterData = jupiterData;
        this.lastUpdated = lastUpdated;
        toDoList = new ArrayList<>();
    }

    public String getOsis() {
        return osis;
    }

    public String getPassword() {
        return password;
    }

    public JupiterData getJupiterData() {
        return jupiterData;
    }

    public void updateData(JupiterSession session){
        this.jupiterData = new JupiterData(session);
    }

    public void setLastUpdated(long lastUpdated){
        this.lastUpdated = lastUpdated;
    }

    public long getLastUpdated() {
        return lastUpdated;
    }

    public boolean appendToDoList(String task) {
        if(toDoList.size() == 25) return false;
        toDoList.add(task);
        return true;
    }

    public ArrayList<String> getToDoList() {
        return toDoList;
    }

    public boolean deleteTask(int id) {
        if(id >= toDoList.size() || id < 0) return false;
        toDoList.remove(id);
        return true;
    }

    public boolean clearList() {
        if(toDoList.size() == 0) return false;
        toDoList.clear();
        return true;
    }


}
