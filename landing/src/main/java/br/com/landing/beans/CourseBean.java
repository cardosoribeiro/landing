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

/**
 * Managed bean for handling Course and Department operations.
 * This bean is view scoped, meaning it lives as long as the view is active.
 */
@ManagedBean(name = "courseBean")
@ViewScoped
public class CourseBean implements Serializable {

    /** Entity manager for JPA operations. */
    @PersistenceContext(unitName = "landing-pu")
    private EntityManager entityManager;

    /** Repository for Course entities. */
    private Repository<Course> courseRepository;

    /** Repository for Department entities. */
    private Repository<Department> departmentRepository;

    /** List of courses to be displayed in the view. */
    private List<Course> courses;

    /** List of departments to be displayed in the view. */
    private List<Department> departments;

    /** New Course object for adding or editing courses. */
    private Course newCourse = new Course();

    /**
     * Retrieves the list of courses.
     *
     * @return List of Course objects.
     */
    public List<Course> getCourses() {
        return courses;
    }

    /**
     * Retrieves the list of departments.
     *
     * @return List of Department objects.
     */
    public List<Department> getDepartments() {
        return departments;
    }

    /**
     * Retrieves the new Course object.
     *
     * @return Course object.
     */
    public Course getNewCourse() {
        return newCourse;
    }

    /**
     * Sets the new Course object.
     *
     * @param newCourse Course object to be set.
     */
    public void setNewCourse(Course newCourse) {
        this.newCourse = newCourse;
    }

    /**
     * Tests if the entity manager is initialized.
     * Prints a message to the console indicating the status.
     */
    private void testEntityManager() {
        if (entityManager == null) {
            System.out.println("EntityManager is NULL!");
        } else {
            System.out.println("EntityManager is NOT NULL!");
        }
    }

    /**
     * Initializes the bean.
     * Creates the repositories and loads the courses and departments.
     */
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

    /**
     * Loads the departments from the database.
     */
    private void loadDepartments() {
        try {
            departments = departmentRepository.findAll();
        } catch (Exception e) {
            e.printStackTrace();
            FacesMessageUtils.addErrorMessage("Error loading departments.");
        }
    }

    /**
     * Loads the courses from the database.
     */
    private void loadCourses() {
        try {
            courses = courseRepository.findAll();
        } catch (Exception e) {
            e.printStackTrace();
            FacesMessageUtils.addErrorMessage("Error loading courses.");
        }
    }

    /**
     * Adds a new course to the database.
     *
     * @return Navigation outcome.
     */
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

    /**
     * Loads the course for editing based on the request parameter.
     *
     * @param event Component system event.
     */
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

    /**
     * Edits the course loaded in newCourse.
     */
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

    /**
     * Updates the course in the database.
     *
     * @return Navigation outcome.
     */
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

    /**
     * Deletes the course from the database.
     *
     * @param courseId Course ID to be deleted.
     */
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