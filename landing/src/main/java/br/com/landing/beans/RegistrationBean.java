package br.com.landing.beans;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.persistence.*;
import javax.persistence.Persistence;
//import javax.persistence.EntityManager;
//import javax.persistence.PersistenceContext;
//import javax.persistence.EntityManagerFactory;
import javax.transaction.Transactional; // Important for database transactions
import javax.annotation.PostConstruct;

import org.mindrot.jbcrypt.BCrypt;

@ManagedBean(name = "registrationBean")
@ViewScoped
public class RegistrationBean {

    @PersistenceContext(unitName = "landing-pu")
    private EntityManager entityManager;

    private User newUser = new User();

    public User getNewUser() { return newUser; }
    public void setNewUser(User newUser) { this.newUser = newUser; }

    
    @PostConstruct  // Recommended way to initialize courses
    private void init() {
        try {            
            EntityManagerFactory emf = Persistence.createEntityManagerFactory("landing-pu"); // Same name!
            entityManager = emf.createEntityManager();            
        } catch (Exception e) {
            e.printStackTrace(); // Log the exception!
        }
    }
    

    //@Transactional // Ensures the operation is atomic
    public String register() {
        EntityTransaction transaction = entityManager.getTransaction();
        try {            
            /*
            if (newUser.getUsername().isEmpty() || newUser.getPassword().isEmpty() || newUser.getRole().isEmpty()) {
                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR.toString(), "All fields are required."));
                return null; // Stay on the page
            }
    
            // Check if username already exists (add this query)
            TypedQuery<User> userCheck = entityManager.createQuery("SELECT u FROM User u WHERE u.username = :username", User.class);
            userCheck.setParameter("username", newUser.getUsername());
            if (!(userCheck.getResultList().isEmpty())) {
                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR.toString(), "Username already exists."));
                return null;
            }            
            */            
            transaction.begin();

            // 1. Hash the password before storing it
            String hashedPassword = BCrypt.hashpw(newUser.getPassword(), BCrypt.gensalt());
            newUser.setPassword(hashedPassword);

            // 2. Persist the new user
            entityManager.persist(newUser);
            transaction.commit();

            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Registration successful!"));
            newUser = new User(); // Reset the form
            return "/login.xhtml"; // Redirect to login page
        } catch (Exception e) {
            e.printStackTrace(); // Log the error!
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR.toString(), "Registration failed: " + e.getMessage()));
            return null; // Stay on the registration page
        }
    }
}   