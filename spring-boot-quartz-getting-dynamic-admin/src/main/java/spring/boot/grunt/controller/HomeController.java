package spring.boot.grunt.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;

@Controller
public class HomeController {


    @GetMapping("/create")
    public ModelAndView create(HttpServletRequest request, Model model){
        ModelAndView mav = new ModelAndView("home/create");
        return mav;
    }

    @GetMapping("/")
    public ModelAndView index(HttpServletRequest request, Model model){
        ModelAndView mav = new ModelAndView("home/index");
        return mav;
    }



}
