package org.niooii.jupiter;

import com.example.niooii.jupitered.Course;
import com.example.niooii.jupitered.Assignment;
import org.niooii.chromedriver.ChromeDriverInit;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.io.Serializable;
import java.time.Duration;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicLong;

public class JupiterSession implements Serializable {
    private HashMap<String, Double> grades = new HashMap<>();
    private String name;
    private final String osis;
    private final String password;
    private final String school;
    private final String city;
    private String state;
    private final WebDriver driver;
    private String source = "";
    LinkedHashMap<String, Integer> assignmentStats = new LinkedHashMap<>();
    private ArrayList<Course> courses;
    private ArrayList<Assignment> missingAssignments = new ArrayList<>();
    private ArrayList<Assignment> ungradedAssignments = new ArrayList<>();
    AtomicLong counter = new AtomicLong();

    //session constructor
    public JupiterSession(String osis, String password) throws InterruptedException {
        this.osis = osis;
        this.password = password;
        this.school = "Bronx High School Of Science";
        this.city = "Bronx";
        this.state = "New York";
        driver =  new ChromeDriverInit().getChromeDriver();
        try {
            login();
            initializeAll();
        }
        finally {
            quit();
        }
    }

    public void login() throws InterruptedException {
        driver.get("https://login.jupitered.com/login/index.php");

        new WebDriverWait(driver, Duration.ofSeconds(10)).until(ExpectedConditions.elementToBeClickable(By.xpath("//div[@id='text_studid1']")))
                .sendKeys(osis);
        driver.findElement(By.id("text_password1")).sendKeys(password);
        driver.findElement(By.id("text_school1")).sendKeys(school);
        driver.findElement(By.id("text_city1")).sendKeys(city);
        driver.findElement(By.id("region1_label")).click();
        Thread.sleep(120);
        new WebDriverWait(driver, Duration.ofSeconds(10)).until(ExpectedConditions.elementToBeClickable(By.xpath("//div[@val='us_ny']")))
                .click();
        driver.findElement(By.id("loginbtn")).click();
        name = driver.findElement(By.className("toptabnull")).getText();
        courses = retrieveCourses();
    }

    private ArrayList<Course> retrieveCourses() {
        ArrayList<WebElement> navrows = (ArrayList<WebElement>) driver.findElements(By.className("classnav"));
        ArrayList<Course> holder = new ArrayList<>();
        for(int i = 0; i < navrows.size(); i++){
            new WebDriverWait(driver, Duration.ofSeconds(10)).until(ExpectedConditions.elementToBeClickable(By.xpath("//div[@id='touchnavbtn']")))
                    .click();
            navrows = (ArrayList<WebElement>) driver.findElements(By.className("classnav")); //if i dont do this i get a stale element error .....
            new WebDriverWait(driver, Duration.ofSeconds(10)).until(ExpectedConditions.elementToBeClickable(navrows.get(i)))
                    .click();
            holder.add(new Course(driver.getPageSource()));
        }
        return holder;
    }

    public void initializeAll(){
        retrieveGrades();
        retrieveStats();
    }

    public String getName(){
        return name;
    }

    public ArrayList<Course> getCourses(){
        return courses;
    }

    private void retrieveStats(){
        int missing = 0;
        int ungraded = 0;
        int graded = 0;
        for(Course course : courses){
            for(Assignment assignment : course.getAssignments()){
                if(Objects.equals(assignment.getStatus(), "Missing")){
                    missingAssignments.add(assignment);
                    missing++;
                } else if(Objects.equals(assignment.getStatus(), "Ungraded")){
                    ungradedAssignments.add(assignment);
                    ungraded++;
                } else {
                    graded++;
                }
            }
        }
        assignmentStats.put("Total", missing + ungraded + graded);
        assignmentStats.put("Graded", graded);
        assignmentStats.put("Ungraded", ungraded);
        assignmentStats.put("Missing", missing);
    }



    public void retrieveGrades(){
        for (Course x : courses) {
            grades.put(x.getName(), x.getGradeAverage());
        }
    }

    public ArrayList<Assignment> getUngradedAssignments() {
        return ungradedAssignments;
    }

    public ArrayList<Assignment> getMissingAssignments() {
        return missingAssignments;
    }

    public LinkedHashMap<String, Integer> getAssignmentStats() {
        return assignmentStats;
    }

    public HashMap<String, Double> getGrades() {
        return grades;
    }

    public void updateSource(){
        source = driver.getPageSource();
    }

    public void quit(){
        driver.manage().deleteAllCookies();
        driver.quit();
    }
}
