package br.com.landing.entity;

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;

@Entity
@Table(name = "department")
public class Department implements Serializable {

    @Id
    @Column(name = "department_name")
    private String departmentName;

    @Column(name = "building")
    private String building;

    @Column(name = "budget")
    private BigDecimal budget;

    // Constructors (no-arg and parameterized)
    public Department() {
    }

    public Department(String departmentName, String building, BigDecimal budget) {
        this.departmentName = departmentName;
        this.building = building;
        this.budget = budget;
    }

    // Getters and setters
    public String getDepartmentName() {
        return departmentName;
    }

    public void setDepartmentName(String departmentName) {
        this.departmentName = departmentName;
    }

    public String getBuilding() {
        return building;
    }

    public void setBuilding(String building) {
        this.building = building;
    }

    public BigDecimal getBudget() {
        return budget;
    }

    public void setBudget(BigDecimal budget) {
        this.budget = budget;
    }

    @Override
    public String toString() {
        return "Department{" +
                "departmentName='" + departmentName + '\'' +
                ", building='" + building + '\'' +
                ", budget=" + budget +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Department that = (Department) o;

        return departmentName.equals(that.departmentName);
    }

    @Override
    public int hashCode() {
        return departmentName.hashCode();
    }
}