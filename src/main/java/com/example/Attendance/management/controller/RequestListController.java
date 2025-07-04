package com.example.Attendance.management.controller;

import com.example.Attendance.management.controller.form.RequestForm;
import com.example.Attendance.management.service.RequestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Controller
public class RequestListController {

    @Autowired
    RequestService requestService;

    @GetMapping("/request/list")
    public String view (Model model){
        List<RequestForm> requestForms = requestService.findRequest();
        model.addAttribute("requests",requestForms);
        model.addAttribute("statuses",RequestForm.Status.values());
        return "request_list";
    }
}
