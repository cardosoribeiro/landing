package br.com.landing.beans;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import java.io.Serializable;
import javax.persistence.*;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.faces.event.ComponentSystemEvent;
import java.util.Map;
import javax.faces.context.FacesContext;

import br.com.landing.entity.Course;
import br.com.landing.entity.Department;

import br.com.landing.repository.Repository;
import br.com.landing.utils.FacesMessageUtils;

@ManagedBean(name = "courseBean")
@ViewScoped
public class CourseBean implements Serializable {

    @PersistenceContext(unitName = "landing-pu")
    private EntityManager entityManager;
    private Repository<Course> courseRepository;
    private Repository<Department> departmentRepository;

    private List<Course> courses;
    private List<Department> departments;

    public List<Course> getCourses() {
        return courses;
    }

    public List<Department> getDepartments() {
        return departments;
    }

    private Course newCourse = new Course();

    public Course getNewCourse() {
        return newCourse;
    }

    public void setNewCourse(Course newCourse) {
        this.newCourse = newCourse;
    }

    private void testEntityManager() {
        if (entityManager == null) {
            System.out.println("EntityManager is NULL!");
        } else {
            System.out.println("EntityManager is NOT NULL!");
        }
    }

    @PostConstruct
    private void init() {
        try {
            EntityManagerFactory emf = Persistence.createEntityManagerFactory("landing-pu"); 
            entityManager = emf.createEntityManager();

            courseRepository = new Repository<>(entityManager, Course.class);
            departmentRepository = new Repository<>(entityManager, Department.class);
            loadDepartments();
            loadCourses();
        } catch (Exception e) {
            e.printStackTrace();
            FacesMessageUtils.addErrorMessage("Error initializing CourseBean: " + e.getMessage());
        }
    }

    private void loadDepartments() {
        try {
            departments = departmentRepository.findAll();
        } catch (Exception e) {
            e.printStackTrace();
            FacesMessageUtils.addErrorMessage("Error loading departments.");
        }
    }

    private void loadCourses() {
        try {
            courses = courseRepository.findAll();
        } catch (Exception e) {
            e.printStackTrace();
            FacesMessageUtils.addErrorMessage("Error loading courses.");
        }
    }

    public String addCourse() {
        try {
            Department selectedDepartment = departmentRepository.findById(newCourse.getDepartment().getDepartmentName());
            if (selectedDepartment == null) {
                FacesMessageUtils.addErrorMessage("Department not found.");
                return null;
            }
            newCourse.setDepartment(selectedDepartment);
            courseRepository.persist(newCourse);
            newCourse = new Course();
            loadCourses();
            FacesMessageUtils.addInfoMessage("Course added successfully!");
            return "/course/list.xhtml";
        } catch (Exception e) {
            e.printStackTrace();
            FacesMessageUtils.addErrorMessage("Error adding course: " + e.getMessage());
            return null;
        }
    }

    public void loadCourseForEdit(ComponentSystemEvent event) {
        FacesContext context = FacesContext.getCurrentInstance();
        Map<String, String> params = context.getExternalContext().getRequestParameterMap();
        String courseId = params.get("courseId");
        if (courseId != null) {
            newCourse.setCourseId(courseId);
            editCourse();
        } else {
            FacesMessageUtils.addErrorMessage("Course ID is required for editing.");
        }
    }

    public void editCourse() {        
        if (newCourse.getCourseId() != null) {
            newCourse = courseRepository.findById(newCourse.getCourseId());
            if (newCourse == null) {
                FacesMessageUtils.addErrorMessage("Course not found");
                return;
            }
        } else {
            FacesMessageUtils.addErrorMessage("Course ID is required for editing.");
        }
    }

    public String updateCourse() {        
        try {
            Department selectedDepartment = departmentRepository.findById(newCourse.getDepartment().getDepartmentName());
            if (selectedDepartment == null) {
                FacesMessageUtils.addErrorMessage("Department not found.");
                return null;
            }
            newCourse.setDepartment(selectedDepartment);
            courseRepository.merge(newCourse);
            newCourse = new Course();
            loadCourses();
            FacesMessageUtils.addInfoMessage("Course updated successfully!");
            return "/course/list.xhtml";
        } catch (Exception e) {
            e.printStackTrace();
            FacesMessageUtils.addErrorMessage("Error updating course: " + e.getMessage());
            return null;
        }
    }

    public void deleteCourse(String courseId) {
        try {
            Course course = courseRepository.findById(courseId);
            if (course != null) {
                courseRepository.remove(course);
                loadCourses();
                FacesMessageUtils.addInfoMessage("Course deleted successfully!");
            } else {
                FacesMessageUtils.addErrorMessage("Course not found for deletion");
            }
        } catch (Exception e) {
            e.printStackTrace();
            FacesMessageUtils.addErrorMessage("Error deleting course: " + e.getMessage());
        }
    }
}