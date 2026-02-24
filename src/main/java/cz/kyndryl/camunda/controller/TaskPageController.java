package cz.kyndryl.camunda.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@Controller
public class TaskPageController {

    @GetMapping("/tasks/{page}/{taskKey}")
    public String taskPage(@PathVariable String page, @PathVariable String taskKey) {
        return "forward:/tasks/" + page + ".html";
    }
}
