package com.example.niooii.jupitered;

import com.example.niooii.jupitered.Assignment;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.io.Serializable;
import java.util.ArrayList;

//@JsonPropertyOrder(value = {"courseName", "gradeAverage", "missing", "assignments"})
public class Course  implements Serializable {
    double gradeAverage;

    String courseName;

    int missing;
    int graded;
    int ungraded;

    private ArrayList<Assignment> assignments = new ArrayList<>();
    public Course(String source){
        Document doc = Jsoup.parse(source); //turn source to document obj
        //get grade and name
        String gradeAverageText = doc.getElementsByClass("pad12").get(1).text();
        this.courseName = doc.getElementsByClass("big").get(1).text();
        gradeAverage = Double.parseDouble(gradeAverageText.substring(0, gradeAverageText.indexOf("%")));
        //initialize lists for assignments
        ArrayList<Element> names = doc.getElementsByClass("pad12 wrap asswidth");
        ArrayList<Element> scores = doc.getElementsByClass("pad8 right");
        ArrayList<Element> impacts = doc.getElementsByClass("pad20 padr8 right alandonly");
        ArrayList<Element> categories = doc.getElementsByClass("pad20 alandonly");
        categories.remove(0); //remove first two bc they're not relevant
        categories.remove(0);
        for(int i = 0; i < names.size(); i++){ //loop adding assignments into list
            String status = "Turned in";
            double numScore;
            String tempImpact;
            if(scores.get(i).text().contains("*0")){
                numScore = 0;
                status = "Missing";
            }
            else if (scores.get(i).text().indexOf("/") == 0 || scores.get(i).text().length() == 0) {
                numScore = 0;
                status = "Ungraded";
            }
            else {
                numScore = Double.parseDouble(scores.get(i).text().substring(0, scores.get(i).text().indexOf("%")));
            }
            if(impacts.get(i).text().length() == 0){
                tempImpact = "0";
            } else tempImpact = impacts.get(i).text();
            assignments.add(new Assignment(names.get(i).text(), numScore, tempImpact, categories.get(i).text(), status));
        }
        Element test = doc.select("b.red").first();
        if(test == null){
            missing = 0;
        } else{
            String missingText = doc.getElementsByClass("red").get(0).text();
            if(missingText.toLowerCase().contains("one")){
                missing = 1;
            } else{
                missing = Integer.parseInt(missingText.substring(0, missingText.indexOf(" ")));
            }
        }

        for(Assignment assignment : assignments){
            String status = assignment.getStatus();
            if(status.equals("Turned in")){
                graded++;
            } else if(status.equals("Ungraded")){
                ungraded++;
            }
        }

    }

    public String getName() {
        return courseName;
    }

    public ArrayList<Assignment> getAssignments(){
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
