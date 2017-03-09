package com.bob.java.webapi.controller;

import com.bob.java.webapi.dto.Form;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.support.BindingAwareModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

/**
 * Created by bob on 17/3/9.
 */
@Controller
@RequestMapping("/text-templates")
public class ThymeleafTextTemplatesController {

    @Autowired
    private TemplateEngine templateEngine;

    @GetMapping("/form")
    public String form(Model model) {
        model.addAttribute(new Form());
        return "th-form";
    }

    @PostMapping("/form")
    public String postForm(@ModelAttribute Form form) {

        Model model = new BindingAwareModelMap();

        Context context = new Context();
        context.setVariable("name", form.getName());
        context.setVariable("url", form.getUrl());
        context.setVariable("tags", form.getTags().split(" "));

        String text = templateEngine.process("text-template", context);

        model.addAttribute("text", text);

        return "th-form";
    }
}