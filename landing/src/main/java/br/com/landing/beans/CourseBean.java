package br.com.landing.beans;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import java.io.Serializable;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.persistence.*;
import java.util.List;
import javax.annotation.PostConstruct;

import javax.faces.event.ComponentSystemEvent;
import java.util.Map;

@ManagedBean(name = "courseBean")
@ViewScoped
public class CourseBean implements Serializable {

    @PersistenceContext(unitName = "landing-pu")
    private EntityManager entityManager;

    private List<Course> courses;

    public List<Course> getCourses() {
        return courses;
    }

    private Course newCourse = new Course(); // Initialize newCourse!

    public Course getNewCourse() {
        return newCourse;
    }

    public void setNewCourse(Course newCourse) {
        this.newCourse = newCourse;
    }

    private void testEntityManager() {
        if (entityManager == null) {
            System.out.println("\n\nEntityManager is NULL!"); // Check logs        
        } else {
            System.out.println("\n\nEntityManager is NOT NULL!"); // Check logs
        }        
    }

    @PostConstruct  // Recommended way to initialize courses
    private void init() {
        try {
            EntityManagerFactory emf = Persistence.createEntityManagerFactory("landing-pu"); // Same name!
            entityManager = emf.createEntityManager();
            
            this.testEntityManager();

        } catch (Exception e) {
            e.printStackTrace(); // Log the exception!
        }
        loadCourses();
    }

    private void loadCourses() {
        this.testEntityManager();

        try {
            TypedQuery<Course> query = entityManager.createQuery("SELECT c FROM Course c", Course.class);
            courses = query.getResultList();
        } catch (Exception e) {
            e.printStackTrace(); // Handle exceptions appropriately in a real app
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR.toString(), "Error loading courses."));
        }
    }


    public String addCourse() {
        this.testEntityManager();

        EntityTransaction transaction = entityManager.getTransaction();
        try {
            transaction.begin();
            entityManager.persist(newCourse);
            transaction.commit();

            newCourse = new Course(); // Reset the form
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Course added successfully!"));
            return "/course/list.xhtml"; // Or stay on the same page: return null;

        } catch (Exception e) {
            e.printStackTrace();
            if (transaction.isActive()) {
                transaction.rollback();
            }
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR.toString(), "Error adding course: " + e.getMessage()));
            return null; // Stay on the same page in case of error
        }
    }

    public void loadCourseForEdit(ComponentSystemEvent event) {
        FacesContext context = FacesContext.getCurrentInstance();
        Map<String, String> params = context.getExternalContext().getRequestParameterMap();
        String courseId = params.get("courseId");
    
        if (courseId != null) {
            newCourse.setCourseId(courseId); // Set the ID in your bean
            editCourse(); // Then call your editCourse method
        } else {
            // Handle the case where courseId is missing (e.g., redirect or error message)
        }
    }
    
    
    public void editCourse() {
        this.testEntityManager();

        if (newCourse.getCourseId() != null) { // Check if courseId is set
            this.newCourse = entityManager.find(Course.class, newCourse.getCourseId());
            if (this.newCourse == null){
                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR.toString(), "Course not found"));
                return;
            }
        } else {
            // Handle the case where courseId is not provided (e.g., redirect or show an error message)
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR.toString(), "Course ID is required for editing."));
        }
    }
    
    public String updateCourse() {
        this.testEntityManager();

        EntityTransaction transaction = entityManager.getTransaction();
        try {
            transaction.begin();
            entityManager.merge(newCourse); // Assuming newCourse has the courseId
            transaction.commit();
            newCourse = new Course(); // Reset the form
            loadCourses(); // Refresh the list
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Course updated successfully!"));
            return "/course/list.xhtml";
        } catch (Exception e) {
            e.printStackTrace();
            if (transaction.isActive()) {
                transaction.rollback();
            }
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR.toString(), "Error adding course: " + e.getMessage()));
            return null; // Stay on the same page in case of error
        }
    }    

    public void deleteCourse(String courseId) {
        this.testEntityManager();

        EntityTransaction transaction = entityManager.getTransaction();
        try {
            transaction.begin();
            Course course = entityManager.find(Course.class, courseId); // Retrieve managed entity
            if (course != null) {
                entityManager.remove(course);
            } else {
                 FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR.toString(), "Course not found for deletion"));
                 transaction.rollback(); // Important rollback here
                 return; // Exit to prevent commit of incomplete transaction
            }
            transaction.commit();
            loadCourses();
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Course deleted successfully!"));
        } catch (Exception e) {
            e.printStackTrace();
            if (transaction.isActive()) {
                transaction.rollback();
            }
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR.toString(), "Error adding course: " + e.getMessage()));
        }
    }
    
}