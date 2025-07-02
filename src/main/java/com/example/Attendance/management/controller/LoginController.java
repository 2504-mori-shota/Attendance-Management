package com.example.Attendance.management.controller;

import com.example.Attendance.management.controller.form.UserForm;
import com.example.Attendance.management.service.UserService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.ModelAndView;

import java.util.ArrayList;
import java.util.List;

@Controller
public class LoginController {
    @Autowired
    private HttpSession session;
    @Autowired
    private UserService userService;

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

    /*
      ログイン処理
     */
    @PostMapping("/login")
    public ModelAndView login(@ModelAttribute("password") String password, @ModelAttribute("account") String account){
        ModelAndView mav = new ModelAndView();
        List<String> errorMessages = new ArrayList<String>();
        // パスワードとアカウントの入力チェック
        if(password.isBlank()) {
            errorMessages.add("パスワードを入力してください");
            mav.setViewName("/login");
        }
        if(account.isBlank()) {
            errorMessages.add("アカウントを入力してください");
            mav.setViewName("/login");
            // 半角英数字かつ文字数チェック
//        }else if(!account.matches("^[a-zA-Z0-9]{6,20}+$")) {
//            errorMessages.add("アカウントは半角英数字かつ6文字以上20文字以下で入力してください");
//            mav.setViewName("/login");
//        // 半角文字かつ文字数チェック
//        }else if(!password.matches("^[!-~]{6,20}+$")) {
//            errorMessages.add("パスワードは半角文字かつ6文字以上20文字以下で入力してください");
//            mav.setViewName("/login");
        }else{
            // アカウント情報とパスワード情報で指定のアカウントを探しに行く
            UserForm user = userService.login(account, password);
            // アカウントが存在しない場合と停止状態のときにバリデーション
            if(user == null || user.getIsStopped() == 1) {
                errorMessages.add("ログインに失敗しました");
                mav.addObject("errorMessages", errorMessages);
                mav.setViewName("/login");
                return mav;
            }
            // セッションにログインユーザー情報を格納
            session.setAttribute("loginUser", user);
            session.setAttribute("loginId", user.getId());

            // ホーム画面にリダイレクト処理
            mav.setViewName("redirect:/home");
        }
        //下記　要るか分からん
//        // エラーメッセージのリストをViewに渡す
//        mav.addObject("errorMessages", errorMessages);
//        // ログインに失敗した場合にアカウント情報が保持されるように
//        mav.addObject("account", account);
        return mav;
    }

}
