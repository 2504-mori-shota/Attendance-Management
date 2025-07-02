package com.example.Attendance.management.controller;

import com.example.Attendance.management.controller.form.UserForm;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.servlet.ModelAndView;

import java.util.List;

@Controller
public class LoginController {
    @Autowired
    private HttpSession session;

    @GetMapping("/login")
    public ModelAndView view(){
        ModelAndView mav = new ModelAndView();
        // form用の空のentityを準備
        UserForm userForm = new UserForm();
        // ログインフィルターのエラーメッセージをセッションから受け取る
        //List<String> errorMessages = (List<String>) session.getAttribute("errorMessages");
        // エラーメッセージがnullじゃなかったらViewに渡す
//        if(errorMessages != null){
//            mav.addObject("errorMessages", errorMessages);
//        }
//        session.removeAttribute("errorMessages");
        // 画面遷移先を指定
        mav.setViewName("/login");
        // 準備した空のFormを保管
        mav.addObject("userForm", userForm);
        return mav;
    }

}
