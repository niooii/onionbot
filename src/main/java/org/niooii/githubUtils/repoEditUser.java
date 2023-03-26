package org.niooii.githubUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

public class repoEditUser implements Serializable {
    private boolean repoEditMode;
    private String repoEditName;
    public repoEditUser(){
    }

    public void setRepoEditMode(boolean repoEditMode) {
        this.repoEditMode = repoEditMode;
    }

    public void setRepoEditName(String repoEditName) {
        this.repoEditName = repoEditName;
    }

    public String getRepoEditName() {
        return repoEditName;
    }

    public boolean isRepoEditMode() {
        return repoEditMode;
    }



}
