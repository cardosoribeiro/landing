package br.com.landing.beans;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import java.io.Serializable;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.persistence.*;
import java.util.List;
import javax.annotation.PostConstruct;


@ManagedBean
@ViewScoped
public class TestBean implements Serializable {

    @PersistenceContext(unitName = "landing-pu")  // Double-check the name!
    private transient EntityManager entityManager;

    @PostConstruct
    public void init() {
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
    }

    public String test() {
        if (entityManager == null) {
            return "EntityManager is NULL!"; // Display on the page
        } else {
            return "EntityManager is NOT NULL!"; // Display on the page
        }
    }
}