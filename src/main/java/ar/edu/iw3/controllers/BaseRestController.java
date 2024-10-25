package ar.edu.iw3.controllers;

import ar.edu.iw3.auth.User;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

public class BaseRestController {
    protected User getUserLogger() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User user = (User) auth.getPrincipal();
        return user;
    }
}
