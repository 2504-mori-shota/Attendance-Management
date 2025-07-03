package com.example.Attendance.management.controller;

import com.example.Attendance.management.controller.form.UserForm;
import com.example.Attendance.management.service.UserService;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

@Controller
public class LoginController {
    @Autowired
    private HttpSession session;
    @Autowired
    private UserService userService;

    @GetMapping("/")
    public String view(Model model){
        // form用の空のentityを準備
        UserForm userForm = new UserForm();
        session.removeAttribute("errorMessages");
        // 準備した空のFormを保管
        model.addAttribute("userForm", userForm);
        return "login";
    }

    /*
      ログイン処理
     */
    @PostMapping("/login")
    public String login(@Validated(UserForm.LoginGroup.class)
                                  @Valid @ModelAttribute("userForm") UserForm userForm, BindingResult result,
                              Model model) throws ParseException {
        List<String> errorMessages = new ArrayList<String>();
        // パスワードとアカウントの入力チェック
        if (result.hasErrors()) {
            return "/login"; // フォワードで遷移
        }
        // アカウント情報とパスワード情報で指定のアカウントを探しに行く
        UserForm user = userService.login(userForm.getAccount(), userForm.getPassword());
        // アカウントが存在しない場合と停止状態のときにバリデーション
        if (user == null || user.getIsStopped() == 1) {
            errorMessages.add("ログインに失敗しました");
            model.addAttribute("errorMessages",errorMessages);
            return "/login";
        }
        // セッションにログインユーザー情報を格納
        session.setAttribute("loginUser", user); //ここからやる
        session.setAttribute("loginId", user.getId());
        return "redirect:/home";
    }

}
