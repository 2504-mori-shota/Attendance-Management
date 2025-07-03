package com.example.Attendance.management.controller;

import com.example.Attendance.management.controller.form.UserForm;
import com.example.Attendance.management.repository.entity.User;
import com.example.Attendance.management.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
public class SystemController {

    @Autowired
    UserService userService;
    @Autowired
    HttpSession session;

    @GetMapping("/system")
    public ModelAndView manage(
            HttpServletRequest request,
            HttpServletResponse response,
            RedirectAttributes redirectAttributes,
            Model model) {
        session = request.getSession();
        //　↓はログインユーザのセッションに入っている情報を取得
        UserForm userForm = (UserForm) session.getAttribute("loginUser");
        List<UserForm> users = userService.findByIdWithPost(userForm.getId());
        UserForm userInfo =  users.get(0);
        if (userInfo.getPostId() != 3) {
            //フラッシュメッセージをセット
            redirectAttributes.addFlashAttribute("errorMessageForm", "無効なアクセスです");
            return new ModelAndView("redirect:/home");
        }

        ModelAndView mav = new ModelAndView();
        // 投稿を全件取得
        List<UserForm> userFormList = userService.findByAllUser();

        for (UserForm userRegisterInfo : userFormList) {
            List<UserForm> userInform = userService.findUserById(userRegisterInfo.getId());
            UserForm userMember = userInform.get(0);
            userRegisterInfo.setPost(userMember.getPost());
        }
        // 画面遷移先を指定
        mav.setViewName("/system");
        // 投稿データオブジェクトを保管
        mav.addObject("users", userFormList);
        model.addAttribute("statuses", UserForm.isStopped.values());
        return mav;
    }

    @PostMapping("/updateStatus")
    public String updateStatus(@RequestParam Integer id, @RequestParam int status){
        userService.updateStatus(id, status);
        return "redirect:/system";
    }
}
