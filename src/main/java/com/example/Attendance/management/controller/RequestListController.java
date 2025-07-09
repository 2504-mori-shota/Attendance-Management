package com.example.Attendance.management.controller;

import com.example.Attendance.management.controller.form.RequestForm;
import com.example.Attendance.management.controller.form.UserForm;
import com.example.Attendance.management.service.RequestService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
public class RequestListController {

    @Autowired
    RequestService requestService;
    @Autowired
    HttpSession session;

    //承認者権限を持つ人の画面
    @GetMapping("/request/list")
    public String view (Model model, RedirectAttributes redirectAttributes){
        //　↓はログインユーザのセッションに入っている情報を取得
        UserForm userForm = (UserForm) session.getAttribute("loginUser");

        if (userForm.getPostId() != 2) {
            //フラッシュメッセージをセット
            redirectAttributes.addFlashAttribute("errorMessageForm", "無効なアクセスです");
            return "redirect:/home";
        }
        List<RequestForm> requestForms = requestService.findRequest();
        model.addAttribute("requests",requestForms);
        model.addAttribute("statuses",RequestForm.Status.values());
        return "request_list";
    }

    //自分の申請一覧が見れる画面
    @GetMapping("/myrequest/{id}")
    public String myview(@PathVariable("id") String userId, Model model){
//        UserForm user = (UserForm) session.getAttribute("loginUser");
        List<RequestForm> requestForms = requestService.findRequestByUserId(Integer.parseInt(userId));
        model.addAttribute("requests",requestForms);
        model.addAttribute("statuses",RequestForm.Status.values());
        return "request_list";
    }
}
