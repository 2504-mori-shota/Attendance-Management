package com.example.Attendance.management.controller;

import com.example.Attendance.management.controller.form.RequestForm;
import com.example.Attendance.management.controller.form.UserForm;
import com.example.Attendance.management.service.RequestService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
public class RequestApprovalListController {
    @Autowired
    RequestService requestService;
    @Autowired
    HttpSession session;

    //承認済の申請一覧画面を表示する
    @GetMapping("request/approval/list")
    public String view (HttpServletRequest request, Model model, RedirectAttributes redirectAttributes){
        //　↓はログインユーザのセッションに入っている情報を取得
        session = request.getSession();
        UserForm userForm = (UserForm) session.getAttribute("loginUser");

        if (userForm.getPostId() != 2 && userForm.getPostId() != 3) {
            //フラッシュメッセージをセット
            redirectAttributes.addFlashAttribute("errorMessageForm", "無効なアクセスです");
            return "redirect:/home";
        }
        List<RequestForm> requestForms = requestService.findRequest();
        model.addAttribute("requests",requestForms);
        model.addAttribute("statuses",RequestForm.Status.values());
        return "request_approval_list";
    }

}
