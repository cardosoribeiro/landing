package br.com.landing.realm;

import org.apache.shiro.authc.*;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;
import org.mindrot.jbcrypt.BCrypt;

import javax.persistence.*;
import javax.persistence.Persistence;

import java.util.List;

import br.com.landing.entity.User;

public class ShiroRealm extends AuthorizingRealm {

    private EntityManagerFactory emf;

    public ShiroRealm() {
        try {
            emf = Persistence.createEntityManagerFactory("landing-pu"); // Persistence unit name
        } catch (Exception e) {
            e.printStackTrace(); // Log the exception!
            throw new RuntimeException("Error initializing EntityManagerFactory", e);
        }
    }

    @Override
    protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken token) throws AuthenticationException {
        UsernamePasswordToken upToken = (UsernamePasswordToken) token;
        String username = upToken.getUsername();
        String password = new String(upToken.getPassword());

        try {
            EntityManager em = emf.createEntityManager();
            
            TypedQuery<User> query = em.createQuery("SELECT u FROM User u WHERE u.username = :username", User.class);
            query.setParameter("username", username);
    
            // Change it to getSingleResult();
            List<User> users = query.getResultList();

            User user = null;
            if (!users.isEmpty()) {
                user = users.get(0); // Get the first user (username should be unique)
            }
    
    
            if (user == null) {
                throw new UnknownAccountException("No account found for user [" + username + "]");
            }


            if (!BCrypt.checkpw(password.trim(), user.getPassword())) {            
                throw new IncorrectCredentialsException("Incorrect password.");
            }

            // VERY IMPORTANT:  The plain password is being gave back to Shiro,
            // We need be sure it is not putted in the http messages, or it is
            // a breach.
            return new SimpleAuthenticationInfo(username, password, getName());
        } catch (Exception e) {
            e.printStackTrace(); // Log the exception!
            throw new AuthenticationException("Error during authentication", e);
        }
    }

    @Override
    protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principals) {
        String username = (String) principals.getPrimaryPrincipal();

        try {
            EntityManager em = emf.createEntityManager();
            
            TypedQuery<User> query = em.createQuery("SELECT u FROM User u WHERE u.username = :username", User.class);
            query.setParameter("username", username);
    
            // Change it to getSingleResult();
            List<User> users = query.getResultList();

            User user = null;
            if (!users.isEmpty()) {
                user = users.get(0); // Get the first user (username should be unique)
            }

            if (user == null) {
                return null; // Or throw an exception if you prefer
            }

            SimpleAuthorizationInfo info = new SimpleAuthorizationInfo();
            info.addRole(user.getRole()); // Assumes User has a getRole() method

            // IMPORTANT: Check if the configuration of roles is an "or" or an "and".
            // In the shiro.ini

            return info;
        } catch (Exception e) {
            e.printStackTrace(); // Log the exception!
            return null; // Or throw an exception
        }
    }
/*
    @PreDestroy
    public void destroy() {
        if (emf != null && emf.isOpen()) {
            emf.close();
        }
    }
*/    
}