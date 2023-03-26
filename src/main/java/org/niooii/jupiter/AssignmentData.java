package org.niooii.jupiter;
import com.example.niooii.jupitered.Assignment;

import java.io.Serializable;

public class AssignmentData implements Serializable {
    private final String name;
    private final String impact;
    private final String category;
    private final double score;
    private final String status;
    public AssignmentData(Assignment assignment) {
        this.name = assignment.getName();
        this.impact = assignment.getImpact();
        this.category = assignment.getCategory();
        this.score = assignment.getScore();
        this.status = assignment.getStatus();
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
