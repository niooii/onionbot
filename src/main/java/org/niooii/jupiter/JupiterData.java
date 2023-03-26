package org.niooii.jupiter;

import org.openqa.selenium.WebDriver;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.concurrent.atomic.AtomicLong;
import com.example.niooii.jupitered.Course;
import com.example.niooii.jupitered.Assignment;

public class JupiterData implements Serializable {
    private HashMap<String, Double> grades = new HashMap<>();
    private String name;
    LinkedHashMap<String, Integer> assignmentStats = new LinkedHashMap<>();
    private ArrayList<CourseData> courseData = new ArrayList<>();
    private ArrayList<AssignmentData> missingAssignments = new ArrayList<>();
    private ArrayList<AssignmentData> ungradedAssignments = new ArrayList<>();
    AtomicLong counter = new AtomicLong();

    public JupiterData(JupiterSession session){
        this.name = session.getName();
        this.grades = session.getGrades();
        this.assignmentStats = session.getAssignmentStats();
        for(Course course : session.getCourses()){
            courseData.add(new CourseData(course));
        }
    }

    public String getName() {
        return name;
    }

    public ArrayList<CourseData> getCourses() {
        return courseData;
    }

    public ArrayList<AssignmentData> getMissingAssignments() {
        return missingAssignments;
    }

    public ArrayList<AssignmentData> getUngradedAssignments() {
        return ungradedAssignments;
    }

    public LinkedHashMap<String, Integer> getAssignmentStats() {
        return assignmentStats;
    }

    public void updateData(JupiterSession session){
        this.name = session.getName();
        this.grades = session.getGrades();
        this.assignmentStats = session.getAssignmentStats();
        courseData.clear();
        for(Course course : session.getCourses()){
            courseData.add(new CourseData(course));
        }
    }
}
