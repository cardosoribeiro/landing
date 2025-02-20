# Update curse and delete course not working

# /landing/pom.xml
```xml
<project xmlns="http://maven.apache.org/POM/4.0.0"
    xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
    xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>
    <groupId>br.com.landing</groupId>
    <artifactId>landing</artifactId>
    <version>0.0.1-SNAPSHOT</version>
    <packaging>war</packaging>
    <name>landing</name>
    <description>A simple landing page</description>
    <repositories>
        <repository>
            <id>jvnet-nexus-releases</id>
            <name>jvnet-nexus-releases</name>
            <url>https://maven.java.net/content/repositories/releases/</url>
        </repository>            
    </repositories>

    <dependencies>
        <dependency>
            <groupId>javax</groupId>
            <artifactId>javaee-api</artifactId>
            <version>7.0</version>
            <scope>provided</scope>
        </dependency>

        <dependency>
            <groupId>org.glassfish</groupId>
            <artifactId>javax.faces</artifactId>
            <version>2.2.10</version>
        </dependency>        

<!-- https://mvnrepository.com/artifact/org.mariadb.jdbc/mariadb-java-client -->
<dependency>
    <groupId>org.mariadb.jdbc</groupId>
    <artifactId>mariadb-java-client</artifactId>
    <version>3.5.2</version>
    <scope>provided</scope>  </dependency>

<dependency>
    <groupId>org.hibernate</groupId>
    <artifactId>hibernate-core</artifactId>
    <version>5.6.14.Final</version> </dependency>

<dependency>
    <groupId>javax.servlet</groupId>
    <artifactId>jstl</artifactId>
    <version>1.2</version>
    <scope>provided</scope>
</dependency>

    </dependencies>

    <build>
        <plugins>
            <plugin>
                <groupId>org.apache.maven.plugins</groupId>
                <artifactId>maven-compiler-plugin</artifactId>
                <version>3.1</version>
                <configuration>
                    <source>1.8</source>
                    <target>1.8</target>
                </configuration>
            </plugin>
            <plugin>
                <artifactId>maven-resources.plugin</artifactId>
                <version>2.7</version>
                <configuration>
                    <encoding>UTF-8</encoding>                    
                </configuration>
            </plugin>
        </plugins>
    </build>
</project>
```

# /landing/src/java/br/com/landing/
```java
```
# /landing/src/java/br/com/landing/beans

## /landing/src/java/br/com/landing/beans/CourseBean.java
```java
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

    public void editCourse(Course course) {
        this.newCourse = course; // Or a deep copy if needed
    }
    
    public String updateCourse() {
        if (entityManager == null) {
            System.out.println("\n\nEntityManager is NULL!"); // Check logs
        } else {
            System.out.println("\n\nEntityManager is NOT NULL!"); // Check logs
        }        
        EntityTransaction transaction = entityManager.getTransaction();
        try {
            transaction.begin();
            entityManager.merge(newCourse); // Assuming newCourse has the courseId
            transaction.commit();
            newCourse = new Course(); // Reset the form
            loadCourses(); // Refresh the list
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Course updated successfully!"));
            return "/course/list_courses.xhtml";
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
        EntityTransaction transaction = entityManager.getTransaction();
        try {
            transaction.begin();
            Course course = entityManager.find(Course.class, courseId);
            if (course != null) {
                entityManager.remove(course);
            }
            transaction.commit();
            loadCourses(); // Refresh the list
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
```
# /landing/src/java/br/com/landing/beans/Course.java
```java
package br.com.landing.beans;

import javax.persistence.*;

@Entity
@Table(name = "course") // Optional: Specify table name if different from class name
public class Course {

    @Id
    @Column(name = "course_id") // Optional: Specify column name if different from field name
    private String courseId;

    @Column(name = "title")
    private String title;

    @Column(name = "department_name") 
    private String departmentName;

    @Column(name = "credits") 
    private int credits;

    // **Essential: Getters and setters for ALL fields**

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

    public String getDepartmentName() {
        return departmentName;
    }

    public void setDepartmentName(String departmentName) {
        this.departmentName = departmentName;
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
                ", departmentName='" + departmentName + '\'' +
                ", credits=" + credits +
                '}';
    }
}
```

# /landing/src/resources/
# /landing/webapp/WEB-INF/web.xml
```xml
<?xml version="1.0" encoding="UTF-8"?>
<web-app xmlns="http://xmlns.jcp.org/xml/ns/javaee"
    xmlns:xsi="http://w3.org/2001/XMLSchema-instance"
    xsi:schemaLocation="http://xmlns.jcp.org/xml/ns/javaee http://xmlns.jcp.org/xml/ns/javaee/web-app_3_1.xsd"
    version="3.1">
    <display-name>landing</display-name>
    
    <servlet>
        <servlet-name>Faces Servlet</servlet-name>
        <servlet-class>javax.faces.webapp.FacesServlet</servlet-class>
        <load-on-startup>1</load-on-startup>
    </servlet>

    <servlet-mapping>
        <servlet-name>Faces Servlet</servlet-name>
        <url-pattern>*.xhtml</url-pattern>
    </servlet-mapping>

    <servlet-mapping>
        <servlet-name>Faces Servlet</servlet-name>
        <url-pattern>/faces/*</url-pattern>
    </servlet-mapping>

    <servlet-mapping>
        <servlet-name>Faces Servlet</servlet-name>
        <url-pattern>*.jsf</url-pattern>
    </servlet-mapping>

    <context-param>
        <param-name>javax.faces.PROJECT_STAGE</param-name>
        <param-value>Development</param-value>
    </context-param>

    <context-param>
        <param-name>javax.faces.INTERPRET_EMPTY_SUBMITTED_VALUES_AS_NULL</param-name>
        <param-value>true</param-value>
    </context-param>

    <context-param>
        <param-name>javax.DATETIMECONVERTER_DEFAULT_TIMEZONE_IS_SYSTEM_TIMEZONE</param-name>
        <param-value>true</param-value>
    </context-param>        

    <welcome-file-list>
        <welcome-file>index.jsf</welcome-file>
        <welcome-file>index.html</welcome-file>
        <welcome-file>index.htm</welcome-file>
    </welcome-file-list>
</web-app>
```
# /landing/webapp/helloworld.xhtml
```xhtml
<?xml version="1.0" encoding="UTF-8"?>
<html xmlns="http://www.w3.org/1999/xhtml"
    xmlns:h="http://xmlns.jcp.org/jsf/html"
    xmlns:f="http://xmlns.jcp.org/jsf/core">
    <h:head>
        <title>
            Initial Test JSF
        </title>
    </h:head>   
    
    <h:body>
        <h:outputText value="Hello World!"/>
    </h:body>
</html>
```
# /landing/webapp/index.xhtml
```xhtml
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml"
      xmlns:h="http://xmlns.jcp.org/jsf/html">

<h:head>
    <title>Landing Page</title>
</h:head>

<h:body>
    <h1>Welcome to My Landing Page</h1>

    <h:form>
        <h:outputText value="This is the index page." /> <br/>
        <h:link outcome="helloworld" value="Go to Hello World Page" />  </h:form>
</h:body>

</html>
```

# /landing/webapp/test.xhtml
```xhtml
<!DOCTYPE html>
<html xmlns="http://www.w3.org/1999/xhtml"
      xmlns:h="http://xmlns.jcp.org/jsf/html"
      xmlns:ui="http://xmlns.jcp.org/jsf/facelets">  <h:head>
    <title>Test Page</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet" />
    <style type="text/css">
        body {
            padding-top: 70px; /* Espaçamento para o navbar fixo */
        }
        .custom-container {
            max-width: 800px; /* Largura máxima do conteúdo principal */
            margin: 0 auto; /* Centraliza o conteúdo */
        }
    </style>
</h:head>
<h:body>

    <nav class="navbar navbar-expand-lg navbar-dark bg-dark fixed-top">  <div class="container">
            <a class="navbar-brand" href="#">My JSF App</a>
            <button class="navbar-toggler" type="button" data-bs-toggle="collapse" data-bs-target="#navbarNav" aria-controls="navbarNav" aria-expanded="false" aria-label="Toggle navigation">
                <span class="navbar-toggler-icon"></span>
            </button>
            <div class="collapse navbar-collapse" id="navbarNav">
                <ul class="navbar-nav">
                    <li class="nav-item">
                        <a class="nav-link active" aria-current="page" href="#">Home</a>
                    </li>
                    <li class="nav-item">
                        <a class="nav-link" href="#{request.contextPath}/course/add_course.jsf">Add Course</a>
                    </li>
                    <li class="nav-item">
                        <a class="nav-link" href="#{request.contextPath}/course/list_courses.jsf">List Course</a>
                    </li>
                </ul>
            </div>
        </div>
    </nav>

    <div class="container custom-container">  <div class="row">
            <div class="col-12">  <div class="card">  <div class="card-header">
                        <h1>Hello from JSF!</h1>
                    </div>
                    <div class="card-body">
                        <h:outputText value="This is a test page." styleClass="lead" />  <h:form>
                            <h:commandButton value="Click Me" styleClass="btn btn-primary mt-3" />  </h:form>
                        <ui:insert name="conteudo"/> </div>
                </div>
            </div>
        </div>
    </div>

    <!--
    <footer class="footer mt-auto py-3 bg-light">  <div class="container text-center">
            <span class="text-muted">Copyright 2023 My JSF App</span>
        </div>
    </footer>
    -->
    
    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
</h:body>
</html>
```
# /landing/webapp/course/update_course.xhtml
```xhtml
<?xml version="1.0" encoding="UTF-8"?>
<html xmlns="http://www.w3.org/1999/xhtml"
      xmlns:h="http://xmlns.jcp.org/jsf/html"
      xmlns:f="http://xmlns.jcp.org/jsf/core">
<h:head>
    <title>Courses</title>
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" />
    <style type="text/css">
        body {
            padding-top: 70px; /* Espaçamento para o navbar fixo */
        }
        .custom-container {
            max-width: 800px; /* Largura máxima do conteúdo principal */
            margin: 0 auto; /* Centraliza o conteúdo */
        }
    </style>
</h:head>
<h:body>

    <nav class="navbar navbar-expand-lg navbar-dark bg-dark fixed-top">
        <div class="container">
            <a class="navbar-brand" href="#">My JSF App</a>
            <button class="navbar-toggler" type="button" data-bs-toggle="collapse" data-bs-target="#navbarNav" aria-controls="navbarNav" aria-expanded="false" aria-label="Toggle navigation">
                <span class="navbar-toggler-icon"></span>
            </button>
            <div class="collapse navbar-collapse" id="navbarNav">
                <ul class="navbar-nav">
                    <li class="nav-item">
                        <a class="nav-link active" aria-current="page" href="#">Courses</a>
                    </li>
                    </ul>
            </div>
        </div>
    </nav>

    <div class="container custom-container">
        <div class="row">
            <div class="col-12">
                <div class="card">
                    <div class="card-header">
                        <h1>Courses</h1>
                    </div>
                    <div class="card-body">
                        <f:metadata>
                            <f:viewParam name="courseId" value="#{courseBean.newCourse.courseId}" converter="javax.faces.convert.StringType" required="true" />
                            <f:event type="preRenderView" listener="#{courseBean.editCourse(courseBean.newCourse)}"/>
                        </f:metadata>
                        
                        <h:form>
                            <h:inputText value="#{courseBean.newCourse.title}" />
                            <h:commandButton value="Update" action="#{courseBean.updateCourse}" />
                        </h:form>
                    </div>
                </div>
            </div>
        </div>
    </div>

    <!--
    <footer class="footer mt-auto py-3 bg-light">
        <div class="container text-center">
            <span class="text-muted">Copyright 2023 My JSF App</span>
        </div>
    </footer>
    -->
    
    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
    <script>
        // JavaScript para validação de formulário Bootstrap
        (() => {
            'use strict'

            // Fetch all the forms we want to apply custom Bootstrap validation styles to
            const forms = document.querySelectorAll('.needs-validation')

            // Loop over them and prevent submission
            forms.forEach(form => {
                form.addEventListener('submit', event => {
                    if (!form.checkValidity()) {
                        event.preventDefault()
                        event.stopPropagation()
                    }

                    form.classList.add('was-validated')
                }, false)
            })
        })()
    </script>
</h:body>
</html>
```
# /landing/webapp/course/add_course.xhtml
```xhtml
<?xml version="1.0" encoding="UTF-8"?>
<html xmlns="http://www.w3.org/1999/xhtml"
      xmlns:h="http://xmlns.jcp.org/jsf/html"
      xmlns:f="http://xmlns.jcp.org/jsf/core">
<h:head>
    <title>Courses</title>
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" />
    <style type="text/css">
        body {
            padding-top: 70px; /* Espaçamento para o navbar fixo */
        }
        .custom-container {
            max-width: 800px; /* Largura máxima do conteúdo principal */
            margin: 0 auto; /* Centraliza o conteúdo */
        }
    </style>
</h:head>
<h:body>

    <nav class="navbar navbar-expand-lg navbar-dark bg-dark fixed-top">
        <div class="container">
            <a class="navbar-brand" href="#">My JSF App</a>
            <button class="navbar-toggler" type="button" data-bs-toggle="collapse" data-bs-target="#navbarNav" aria-controls="navbarNav" aria-expanded="false" aria-label="Toggle navigation">
                <span class="navbar-toggler-icon"></span>
            </button>
            <div class="collapse navbar-collapse" id="navbarNav">
                <ul class="navbar-nav">
                    <li class="nav-item">
                        <a class="nav-link active" aria-current="page" href="#">Courses</a>
                    </li>
                    </ul>
            </div>
        </div>
    </nav>

    <div class="container custom-container">
        <div class="row">
            <div class="col-12">
                <div class="card">
                    <div class="card-header">
                        <h1>Courses</h1>
                    </div>
                    <div class="card-body">
                        <h:form styleClass="needs-validation" role="form">  <div class="mb-3">
                                <h:outputLabel for="courseId" value="Course ID:" styleClass="form-label" />
                                <h:inputText id="courseId" value="#{courseBean.newCourse.courseId}" required="true" styleClass="form-control" />
                                <div class="invalid-feedback">Course ID is required.</div>
                            </div>

                            <div class="mb-3">
                                <h:outputLabel for="title" value="Title:" styleClass="form-label" />
                                    <h:inputText id="title" value="#{courseBean.newCourse.title}" required="true" styleClass="form-control" />
                                <div class="invalid-feedback">Title is required.</div>
                            </div>

                            <div class="mb-3">
                                <h:outputLabel for="department" value="Department:" styleClass="form-label" />
                                <h:inputText id="department" value="#{courseBean.newCourse.departmentName}" required="true" styleClass="form-control" />
                                <div class="invalid-feedback">Department is required.</div>
                            </div>

                            <div class="mb-3">
                                <h:outputLabel for="credits" value="Credits:" styleClass="form-label" />
                                <h:inputText id="credits" value="#{courseBean.newCourse.credits}" required="true" styleClass="form-control" />
                                <div class="invalid-feedback">Credits is required.</div>
                            </div>

                            <h:commandButton value="Add Course" action="#{courseBean.addCourse}" styleClass="btn btn-primary" />
                        </h:form>
                    </div>
                </div>
            </div>
        </div>
    </div>

    <!--
    <footer class="footer mt-auto py-3 bg-light">
        <div class="container text-center">
            <span class="text-muted">Copyright 2023 My JSF App</span>
        </div>
    </footer>
    -->
    
    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
    <script>
        // JavaScript para validação de formulário Bootstrap
        (() => {
            'use strict'

            // Fetch all the forms we want to apply custom Bootstrap validation styles to
            const forms = document.querySelectorAll('.needs-validation')

            // Loop over them and prevent submission
            forms.forEach(form => {
                form.addEventListener('submit', event => {
                    if (!form.checkValidity()) {
                        event.preventDefault()
                        event.stopPropagation()
                    }

                    form.classList.add('was-validated')
                }, false)
            })
        })()
    </script>
</h:body>
</html>
```

# /landing/webapp/course/list_course.xhtml
```xhtml
<?xml version="1.0" encoding="UTF-8"?>
<html xmlns="http://www.w3.org/1999/xhtml"
      xmlns:h="http://xmlns.jcp.org/jsf/html"
      xmlns:f="http://xmlns.jcp.org/jsf/core">
<h:head>
    <title>Courses</title>
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" />
    <style type="text/css">
        body {
            padding-top: 70px; /* Espaçamento para o navbar fixo */
        }
        .custom-container {
            max-width: 800px; /* Largura máxima do conteúdo principal */
            margin: 0 auto; /* Centraliza o conteúdo */
        }
    </style>
</h:head>
<h:body>

    <nav class="navbar navbar-expand-lg navbar-dark bg-dark fixed-top">
        <div class="container">
            <a class="navbar-brand" href="#">My JSF App</a>
            <button class="navbar-toggler" type="button" data-bs-toggle="collapse" data-bs-target="#navbarNav" aria-controls="navbarNav" aria-expanded="false" aria-label="Toggle navigation">
                <span class="navbar-toggler-icon"></span>
            </button>
            <div class="collapse navbar-collapse" id="navbarNav">
                <ul class="navbar-nav">
                    <li class="nav-item">
                        <a class="nav-link active" aria-current="page" href="#">Courses</a>
                    </li>
                </ul>
            </div>
        </div>
    </nav>

    <div class="container custom-container">
        <div class="row">
            <div class="col-12">
                <div class="card">
                    <div class="card-header">
                        <h1>Courses</h1>
                    </div>
                    <div class="card-body">
                        <h:dataTable value="#{courseBean.courses}" var="course" styleClass="table table-striped">
                            <h:column>
                                <f:facet name="header">Course ID</f:facet>
                                <h:outputText value="#{course.courseId}" />
                            </h:column>
                            <h:column>
                                <f:facet name="header">Title</f:facet>
                                <h:outputText value="#{course.title}" />
                            </h:column>
                            <h:column>
                                <f:facet name="header">Department</f:facet>
                                <h:outputText value="#{course.departmentName}" />
                            </h:column>
                            <h:column>
                                <f:facet name="header">Credits</f:facet>
                                <h:outputText value="#{course.credits}" />
                            </h:column>
                        </h:dataTable>
                    </div>
                </div>
            </div>
        </div>
    </div>
    <!--
    <footer class="footer mt-auto py-3 bg-light">
        <div class="container text-center">
            <span class="text-muted">Copyright 2023 My JSF App</span>
        </div>
    </footer>
    -->
    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
</h:body>
</html>
```