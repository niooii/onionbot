package org.niooii.jupiter;

import org.checkerframework.checker.units.qual.C;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class UserList {
    private ConcurrentHashMap<String, User> userMap;
    public UserList(){
        userMap = new ConcurrentHashMap<>();
        retrieveData();
        System.out.printf(userMap.toString());
    }

    public void writedata(){
        try {
            File userStore = new File("src/main/resources/userStore.data");
            FileOutputStream fos=new FileOutputStream(userStore);
            ObjectOutputStream oos=new ObjectOutputStream(fos);

            oos.writeObject(userMap);
            oos.flush();
            oos.close();
            fos.close();
        } catch(Exception e) {
            System.out.println(e);
        }
    }

    public void createUser(String userId, String osis, String password, JupiterSession session, long lastUpdated) {
        userMap.put(userId, new User(userId, osis, password,
                new JupiterData(session), lastUpdated));
        writedata();
    }

    public void removeUser(String id) {
        userMap.remove(id);
        writedata();
    }

    public void updateUserData(String id, JupiterSession session){
        userMap.get(id).updateData(session);
        writedata();
    }

    public void setLastUpdated(String id, long lastUpdated){
        userMap.get(id).setLastUpdated(lastUpdated);
        writedata();
    }

    public JupiterData getJupiterData(String id){
        return userMap.get(id).getJupiterData();
    }

    public void retrieveData(){
        //read from file
        try {
            File toRead=new File("src/main/resources/userStore.data");
            FileInputStream fis=new FileInputStream(toRead);
            ObjectInputStream ois=new ObjectInputStream(fis);

            userMap = (ConcurrentHashMap<String, User>)ois.readObject();

            ois.close();
            fis.close();

            for(Map.Entry<String, User> m : userMap.entrySet()){
                System.out.println(m.getKey()+" : "+m.getValue());
            }

        } catch(Exception e) {
            System.out.println(e);
        }
    }

    public void clear() throws IOException, InterruptedException {
        for(HashMap.Entry<String, User> entry : userMap.entrySet()) {
            String key = entry.getKey();
            userMap.remove(key);
        }

        writedata();

    }

    public ConcurrentHashMap<String, User> getUserMap(){
        return userMap;
    }

    public boolean deleteTask(String userId, int id){
        boolean yes = userMap.get(userId).deleteTask(id);
        writedata();
        return yes;
    }

    public boolean addTask(String userId, String task){
        boolean yes = userMap.get(userId).appendToDoList(task);
        writedata();
        return yes;
    }

    public boolean clearTaskList(String userId){
        boolean yes = userMap.get(userId).clearList();
        writedata();
        return yes;
    }
}
