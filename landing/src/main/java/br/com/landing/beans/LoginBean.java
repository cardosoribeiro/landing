// LoginBean.java
package br.com.landing.beans;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped; // Important: Use SessionScoped
import javax.faces.context.FacesContext;
import javax.persistence.*;
import javax.persistence.Persistence;
//import javax.persistence.EntityManager;
//import javax.persistence.PersistenceContext;
//import javax.persistence.TypedQuery;
//import javax.persistence.EntityManagerFactory;
import java.io.Serializable;
import javax.annotation.PostConstruct;

import org.mindrot.jbcrypt.BCrypt;

import br.com.landing.entity.User;

@ManagedBean(name = "loginBean")
@SessionScoped // Keep the user logged in across requests
public class LoginBean implements Serializable {

    @PersistenceContext(unitName = "landing-pu")
    private EntityManager entityManager;

    private String username;
    private String password;
    private User loggedInUser;

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public User getLoggedInUser() { return loggedInUser; }
    public void setLoggedInUser(User loggedInUser) { this.loggedInUser = loggedInUser; }
    
    @PostConstruct  // Recommended way to initialize courses
    private void init() {
        try {            
            EntityManagerFactory emf = Persistence.createEntityManagerFactory("landing-pu"); // Same name!
            entityManager = emf.createEntityManager();            
        } catch (Exception e) {
            e.printStackTrace(); // Log the exception!
        }
    }
    

    public String login() {
        try {
            TypedQuery<User> query = entityManager.createQuery(
                    "SELECT u FROM User u WHERE u.username = :username", User.class);
            query.setParameter("username", username);

            User user = query.getSingleResult();  // Will throw exception if not found

            // VERY IMPORTANT:  Do NOT store plain passwords.  Hash the entered password
            // and compare it to the stored hash using a secure comparison method.
            if (user != null && BCrypt.checkpw(password, user.getPassword())) { // Use BCrypt for password comparison
                loggedInUser = user; // Store the logged-in user
                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Login successful!"));
                return "/index.xhtml"; // Redirect to a welcome page
            } else {
                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR.toString(), "Invalid username or password."));
                return null; // Stay on the login page
            }
        } catch (Exception e) { // Catch NoResultException or other exceptions
            e.printStackTrace(); // Log the error!
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR.toString(), "Invalid username or password."));
            return null;
        }
    }


    public String logout() {
        loggedInUser = null; // Invalidate the session
        FacesContext.getCurrentInstance().getExternalContext().invalidateSession(); // Completely invalidate
        return "/login.xhtml"; // Redirect to login page (or wherever you want)
    }

    public boolean isLoggedIn() {
        return loggedInUser != null;
    }

    public boolean isUserInRole(String role) {
        return isLoggedIn() && loggedInUser.getRole().equals(role);
    }
}