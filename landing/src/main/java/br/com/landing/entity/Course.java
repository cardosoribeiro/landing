package br.com.landing.entity;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Table(name = "course") // Optional: Specify table name if different from class name
public class Course implements Serializable {

    @Id
    @Column(name = "course_id") // Optional: Specify column name if different from field name
    private String courseId;

    @Column(name = "title")
    private String title;

    @Column(name = "department_name") 
    private String departmentName;

    @Column(name = "credits") 
    private int credits;

    // **Essential: Getters and setters for ALL fields**

    public String getCourseId() {
        return courseId;
    }

    public void setCourseId(String courseId) {
        this.courseId = courseId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDepartmentName() {
        return departmentName;
    }

    public void setDepartmentName(String departmentName) {
        this.departmentName = departmentName;
    }

    public int getCredits() {
        return credits;
    }

    public void setCredits(int credits) {
        this.credits = credits;
    }


    @Override
    public String toString() {
        return "Course{" +
                "courseId='" + courseId + '\'' +
                ", title='" + title + '\'' +
                ", departmentName='" + departmentName + '\'' +
                ", credits=" + credits +
                '}';
    }
}