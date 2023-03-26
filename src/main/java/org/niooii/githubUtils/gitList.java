package org.niooii.githubUtils;

import org.kohsuke.github.GHUser;
import org.kohsuke.github.GitHub;
import org.kohsuke.github.GitHubBuilder;

import java.io.*;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class gitList implements Serializable {
    private ConcurrentHashMap<String, String> bearerMap;
    private ConcurrentHashMap<String, repoEditUser> repoEditUser; //when programmers attempt to name things
    private ConcurrentHashMap<String, GitHub> gitMap = new ConcurrentHashMap<>();
    public gitList() throws IOException {
        bearerMap = new ConcurrentHashMap<>();
        repoEditUser = new ConcurrentHashMap<>();
        retrieveData();
        //initialize gitMap (boo serialization)
        for(Map.Entry<String, String> m : bearerMap.entrySet()){
            try {
                new GitHubBuilder().withOAuthToken(m.getValue()).build().getMyself();
                gitMap.put(m.getKey(), new GitHubBuilder().withOAuthToken(m.getValue()).build());
            } catch(Exception e){
                System.out.println(e);
                System.out.println(m.getValue() + " is not a valid token");
                System.out.println("removing invalid/changed credentials");
                bearerMap.remove(m.getKey());
                writedata();
            }
        }
    }

    public void retrieveData(){
        //read from file
        try {
            File toRead=new File("src/main/resources/gitStore.data");
            FileInputStream fis=new FileInputStream(toRead);
            ObjectInputStream ois=new ObjectInputStream(fis);

            bearerMap = (ConcurrentHashMap<String, String>)ois.readObject();

            ois.close();
            fis.close();

            toRead=new File("src/main/resources/sudoStore.data");
            fis=new FileInputStream(toRead);
            ois=new ObjectInputStream(fis);

            repoEditUser = (ConcurrentHashMap<String, repoEditUser>)ois.readObject();

            ois.close();
            fis.close();

            for(Map.Entry<String, String> m : bearerMap.entrySet()){
                System.out.println(m.getKey()+" : "+m.getValue());
            }

        } catch(Exception e) {
            System.out.println(e);
        }
    }

    public void writedata(){
        try {
            File gitStore = new File("src/main/resources/gitStore.data");
            File sudoStore = new File("src/main/resources/sudoStore.data");
            FileOutputStream fos=new FileOutputStream(gitStore);
            ObjectOutputStream oos=new ObjectOutputStream(fos);

            oos.writeObject(bearerMap);
            oos.flush();
            oos.close();
            fos.close();

            fos=new FileOutputStream(sudoStore);
            oos=new ObjectOutputStream(fos);
            oos.writeObject(repoEditUser);
            oos.flush();
            oos.close();
            fos.close();
        } catch(Exception e) {
            System.out.println(e);
        }
    }

    public void clear() throws IOException, InterruptedException {
        for(HashMap.Entry<String, String> entry : bearerMap.entrySet()) {
            String key = entry.getKey();
            bearerMap.remove(key);
        }
        writedata();
    }

    public boolean addUser(String authorId, String bearer) throws IOException {
        bearerMap.put(authorId, bearer);
        try {
            new GitHubBuilder().withOAuthToken(bearer).build().getMyself();
            gitMap.put(authorId, new GitHubBuilder().withOAuthToken(bearer).build());
            repoEditUser.put(authorId, new repoEditUser());
            writedata();
            return true;
        } catch(Exception e){
            System.out.println(e);
            return false;
        }
    }

    public boolean removeUser(String authorId) throws IOException {
        try{
            bearerMap.remove(authorId);
            repoEditUser.remove(authorId);
            return true;
        } catch(Exception e){
            System.out.println(e);
            return false;
        }

    }

    public void setSudoName(String authorid, String name){
        repoEditUser.get(authorid).setRepoEditName(name);
        writedata();
    }

    public void setSudoMode(String authorid, boolean status){
        repoEditUser.get(authorid).setRepoEditMode(status);
        writedata();
    }

    public GitHub getGitConnection(String authorId){
        return gitMap.get(authorId);
    }

    public ConcurrentHashMap<String, GitHub> getGitMap() {
        return gitMap;
    }

    public ConcurrentHashMap<String, org.niooii.githubUtils.repoEditUser> getRepoEditUser() {
        return repoEditUser;
    }
}
