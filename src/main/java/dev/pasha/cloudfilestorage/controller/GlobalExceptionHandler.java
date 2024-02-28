package dev.pasha.cloudfilestorage.controller;


import dev.pasha.cloudfilestorage.exception.DeleteMinioObjectException;
import dev.pasha.cloudfilestorage.exception.UserObjectMinioServiceException;
import dev.pasha.cloudfilestorage.exception.UserAuthMinioServiceException;
import dev.pasha.cloudfilestorage.model.User;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.ModelAndView;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ModelAndView uniqueUsernameException(DataIntegrityViolationException ex) {
        ModelAndView modelAndView = new ModelAndView("signup-form");
        modelAndView.addObject("errorMessage", "This username is already in use");
        modelAndView.addObject("user", new User());
        return modelAndView;
    }

    @ExceptionHandler(UserAuthMinioServiceException.class)
    public ModelAndView userRegMinioServiceException(UserAuthMinioServiceException ex) {
        ModelAndView modelAndView = new ModelAndView("signup-form");
        modelAndView.addObject("errorMessage", ex.getMessage());
        modelAndView.addObject("user", new User());
        return modelAndView;
    }

    @ExceptionHandler(UserObjectMinioServiceException.class)
    public ModelAndView userObjectException(UserObjectMinioServiceException ex) {
        ModelAndView modelAndView = new ModelAndView("auth");
        modelAndView.addObject("errorMessage", ex.getMessage());
        return modelAndView;
    }

    @ExceptionHandler(DeleteMinioObjectException.class)
    public ModelAndView deleteMinioObjectException(DeleteMinioObjectException ex) {
        ModelAndView modelAndView = new ModelAndView("index");
        modelAndView.addObject("errorMessage", ex.getMessage());
        return modelAndView;
    }
}
