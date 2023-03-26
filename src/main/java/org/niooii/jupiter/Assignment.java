package com.example.niooii.jupitered;

import java.io.Serializable;

public class Assignment implements Serializable {
    private String name, impact, category;
    private double score;
    private String status;
    public Assignment(String name,
                      double score,
                      String impact,
                      String category,
                      String isMissing){
        this.name = name;
        this.score = score;
        this.impact = impact;
        this.category = category;
        this.status = isMissing;
    }

    public String getName(){
        return name;
    }

    public double getScore() {
        return score;
    }

    public String getImpact() {
        return impact;
    }

    public String getCategory() {
        return category;
    }

    public String getStatus() {
        return status;
    }
}
