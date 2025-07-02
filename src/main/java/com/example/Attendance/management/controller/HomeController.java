package com.example.Attendance.management.controller;


import com.example.Attendance.management.controller.form.UserForm;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class HomeController {


    @GetMapping("/home")
    public ModelAndView newContent(
            HttpServletRequest request,
            HttpServletResponse response,
            RedirectAttributes redirectAttributes,
            Model model) {
        //session = request.getSession();
        // UserForm user = (UserForm) session.getAttribute("user");
        //List<UserForm> users = userService.findByIdWithDepartmentAndBranch(user.getId());
        //UserForm userInfoForm =  users.get(0);

        /*if (userInfoForm.getDepartment().getId() != 1 && userInfoForm.getDepartmentId() != 1) {
            //フラッシュメッセージをセット
            redirectAttributes.addFlashAttribute("errorMessageForm", "不正なアクセスです");
            return new ModelAndView("redirect:/home");
        }*/


        ModelAndView mav = new ModelAndView();
        UserForm userForm = new UserForm();
        mav.addObject("formModel", userForm);
        //プルダウンで使用

        // 画面遷移先を指定
        mav.setViewName("/home");
        return mav;
    }

    @RequestMapping("/logout")
    public String logout(HttpServletRequest request) {
        HttpSession session = request.getSession(false); // セッションを取得
        if(session != null) {
            session.invalidate();//セッション破棄
        }
        return "redirect:/login";
    }

}

