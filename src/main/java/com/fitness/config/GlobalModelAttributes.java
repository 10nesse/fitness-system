package com.fitness.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

import java.util.Set;

@ControllerAdvice
public class GlobalModelAttributes {

    private static final Logger logger = LoggerFactory.getLogger(GlobalModelAttributes.class);

    @ModelAttribute
    public void addRolesToModel(Authentication authentication, Model model) {
        if (authentication != null && authentication.isAuthenticated() &&
                !(authentication instanceof AnonymousAuthenticationToken)) {
            Set<String> roles = AuthorityUtils.authorityListToSet(authentication.getAuthorities());
            model.addAttribute("roles", roles);
            logger.debug("Добавлены роли {} для пользователя {}", roles, authentication.getName());
        } else {
            logger.debug("Пользователь не аутентифицирован или анонимен.");
        }
    }
}
