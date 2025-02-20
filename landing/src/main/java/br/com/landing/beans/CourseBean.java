package br.com.landing.beans;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import java.io.Serializable;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.persistence.*;
import java.util.List;
import javax.annotation.PostConstruct;

@ManagedBean(name = "courseBean")
@ViewScoped
public class CourseBean implements Serializable {

    @PersistenceContext(unitName = "landing-pu")
    private EntityManager entityManager;

    private List<Course> courses;
    private Course newCourse = new Course(); // Initialize newCourse!

    @PostConstruct  // Recommended way to initialize courses
    private void init() {
        try {
            EntityManagerFactory emf = Persistence.createEntityManagerFactory("landing-pu"); // Same name!
            entityManager = emf.createEntityManager();
            if (entityManager == null) {
                System.out.println("EntityManager is NULL!");
            } else {
                System.out.println("EntityManager is NOT NULL!");
            }
        } catch (Exception e) {
            e.printStackTrace(); // Log the exception!
        }
        loadCourses();
    }


    public List<Course> getCourses() {
        return courses;
    }

    private void loadCourses() {
        if (entityManager == null) {
            System.out.println("\n\nEntityManager is NULL!"); // Check logs
        } else {
            System.out.println("\n\nEntityManager is NOT NULL!"); // Check logs
        }
        try {
            TypedQuery<Course> query = entityManager.createQuery("SELECT c FROM Course c", Course.class);
            courses = query.getResultList();
        } catch (Exception e) {
            e.printStackTrace(); // Handle exceptions appropriately in a real app
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR.toString(), "Error loading courses."));
        }
    }

    public Course getNewCourse() {
        return newCourse;
    }

    public void setNewCourse(Course newCourse) {
        this.newCourse = newCourse;
    }

    public String addCourse() {
        if (entityManager == null) {
            System.out.println("\n\nEntityManager is NULL!"); // Check logs
        } else {
            System.out.println("\n\nEntityManager is NOT NULL!"); // Check logs
        }        
        EntityTransaction transaction = entityManager.getTransaction();
        try {
            transaction.begin();
            entityManager.persist(newCourse);
            transaction.commit();

            newCourse = new Course(); // Reset the form
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Course added successfully!"));
            return "/course/list_courses.xhtml"; // Or stay on the same page: return null;

        } catch (Exception e) {
            e.printStackTrace();
            if (transaction.isActive()) {
                transaction.rollback();
            }
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR.toString(), "Error adding course: " + e.getMessage()));
            return null; // Stay on the same page in case of error
        }
    }
}