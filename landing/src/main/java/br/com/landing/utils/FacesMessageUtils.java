package br.com.landing.utils;

import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;

public class FacesMessageUtils {
    public static void addInfoMessage(String message) {
        FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, message, null));
    }

    public static void addErrorMessage(String message) {
        FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, message, null));
    }

    public static void addWarningMessage(String message) {
        FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN, message, null));
    }

    public static void addFatalMessage(String message) {
        FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_FATAL, message, null));
    }

    public static void addMessage(FacesMessage.Severity severity, String message){
        FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(severity, message, null));
    }
}