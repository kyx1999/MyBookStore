package com.kyx1999.mybookstore.controller;

import com.kyx1999.mybookstore.service.BulletinService;
import com.kyx1999.mybookstore.service.UserService;
import com.kyx1999.mybookstore.util.Tools;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import javax.servlet.http.HttpServletRequest;

@Controller
public class ErrorController {

    @Autowired
    private UserService userService;

    @Autowired
    private BulletinService bulletinService;

    @GetMapping("/error/404")
    public String error404(Model model, HttpServletRequest request) {
        Tools.setLayout(userService, bulletinService, model, request, true);

        return "error/404";
    }
}
