package org.niooii.jupiter;

import java.io.Serializable;
import java.util.ArrayList;
import com.example.niooii.jupitered.Assignment;
import com.example.niooii.jupitered.Course;

public class CourseData implements Serializable {
    double gradeAverage;

    String courseName;

    int missing;
    int graded;
    int ungraded;
    private final ArrayList<AssignmentData> assignments = new ArrayList<>();

    public CourseData(Course course){
        this.courseName = course.getName();
        this.gradeAverage = course.getGradeAverage();
        this.graded = course.getGraded();
        this.ungraded = course.getUngraded();
        this.missing = course.getMissing();
        for(Assignment assignment : course.getAssignments()){
            assignments.add(new AssignmentData(assignment));
        }
    }

    public String getName() {
        return courseName;
    }

    public ArrayList<AssignmentData> getAssignments(){
        return assignments;
    }

    public double getGradeAverage() {
        return gradeAverage;
    }

    public int getMissing() {
        return missing;
    }

    public int getGraded() {
        return graded;
    }

    public int getUngraded() {
        return ungraded;
    }
}
