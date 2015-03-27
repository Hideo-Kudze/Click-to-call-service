package com.HideoKuzeGits.Callback;

import com.HideoKuzeGits.Callback.registration.UserService;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.security.Principal;
import java.util.Collection;

/**
 * Created by root on 17.12.14.
 */

public class ExposeUserIdToModelInterceptor extends HandlerInterceptorAdapter {

    private UserService userService;

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {

        if (modelAndView == null)
            return;

        Principal userPrincipal = request.getUserPrincipal();
        if (userPrincipal == null)
            return;
        Collection<GrantedAuthority> authorities = ((UsernamePasswordAuthenticationToken) userPrincipal).getAuthorities();
        if (!authorities.contains(new SimpleGrantedAuthority("ROLE_USER")))
            return;
        String email = userPrincipal.getName();
        String uuid = userService.getUserId(email);
        modelAndView.getModel().put("userId", uuid);
    }

    public void setUserService(UserService userService) {
        this.userService = userService;
    }
}
