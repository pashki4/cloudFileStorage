package dev.pasha.cloudfilestorage.controller;


import dev.pasha.cloudfilestorage.exception.UserRegMinioServiceException;
import dev.pasha.cloudfilestorage.model.User;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.ModelAndView;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ModelAndView uniqueUsernameExceptionHandler(DataIntegrityViolationException ex) {
        ModelAndView modelAndView = new ModelAndView("signup-form");
        modelAndView.addObject("errorMessage", "This username is already in use");
        modelAndView.addObject("user", new User());
        return modelAndView;
    }

    @ExceptionHandler(UserRegMinioServiceException.class)
    public ModelAndView userRegMinioService(UserRegMinioServiceException ex) {
        ModelAndView modelAndView = new ModelAndView("signup-form");
        modelAndView.addObject("errorMessage", ex.getMessage());
        modelAndView.addObject("user", new User());
        return modelAndView;
    }
}
