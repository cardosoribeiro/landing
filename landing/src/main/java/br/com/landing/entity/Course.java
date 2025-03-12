package br.com.landing.entity;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Table(name = "course")
public class Course implements Serializable {

    @Id
    @Column(name = "course_id")
    private String courseId;

    @Column(name = "title")
    private String title;

    @ManyToOne
    @JoinColumn(name = "department_name")
    private Department department;

    @Column(name = "credits")
    private int credits;

    // Constructors (no-arg and parameterized)
    public Course() {
        this.department = new Department();
    }

    public Course(String courseId, String title, Department department, int credits) {
        this.courseId = courseId;
        this.title = title;
        this.department = department;
        this.credits = credits;
    }

    // Getters and setters
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

    public Department getDepartment() {
        return department;
    }

    public void setDepartment(Department department) {
        this.department = department;
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
                ", department=" + (department != null ? department.getDepartmentName() : null) + // Avoid null pointer exception
                ", credits=" + credits +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Course course = (Course) o;

        return courseId.equals(course.courseId);
    }

    @Override
    public int hashCode() {
        return courseId.hashCode();
    }
}