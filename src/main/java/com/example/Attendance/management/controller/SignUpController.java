package com.example.Attendance.management.controller;

import com.example.Attendance.management.controller.form.UserForm;
import com.example.Attendance.management.repository.entity.Post;
import com.example.Attendance.management.repository.entity.User;
import com.example.Attendance.management.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
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
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.text.ParseException;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Controller
public class SignUpController {

    @Autowired
    UserService userService;
    @Autowired
    HttpSession session;

    @GetMapping("/signup")
    public ModelAndView newContent(
            HttpServletRequest request,
            HttpServletResponse response,
            RedirectAttributes redirectAttributes,
            Model model) {
        session = request.getSession();
        UserForm user = (UserForm) session.getAttribute("loginUser");
        List<UserForm> users = userService.findByIdWithPost(user.getId());
        UserForm userInfoForm =  users.get(0);

        if (userInfoForm.getPostId() != 3) {
            //フラッシュメッセージをセット
            redirectAttributes.addFlashAttribute("errorMessageForm", "無効なアクセスです");
            return new ModelAndView("redirect:/home");
        }


        ModelAndView mav = new ModelAndView();
        UserForm userForm = new UserForm();
        mav.addObject("formModel", userForm);
        //プルダウンで使用
        mav.addObject("postOptions", getPostOptions());

        // 画面遷移先を指定
        mav.setViewName("/signup");
        return mav;
    }

    // コントローラー内に選択肢を返すメソッド プルダウンで使用
    private Map<Integer, String> getPostOptions() {
        List<Post> posts = userService.findAllPost();
        Map<Integer, String> options = new LinkedHashMap<>();

        for(int i = 0; i < posts.size(); i++ ){
            Post post = posts.get(i);

            String name = post.getPostName();
            options.put(i+1, name);
        }

        return options;
    }


    /*
     * 新規登録処理
     */
    @PostMapping("/insert")

    public String addContent(@Validated(UserForm.SignUpGroup.class)
                             @Valid @ModelAttribute("formModel") UserForm userForm, BindingResult result,
                             Model model
    ) throws ParseException {
        // パスワード確認チェック
        if (!result.hasFieldErrors("passwordConfirm") &&
                !userForm.getPassword().equals(userForm.getPasswordConfirm())) {
            result.rejectValue("passwordConfirm", null, "パスワードと確認用パスワードが一致しません");
        }
        // アカウント重複チェック
        if (userService.AccountDuB(userForm.getAccount())) {
            result.rejectValue("account", "duplicate", "アカウントが重複しています");
        }


        if (result.hasErrors()) {

            model.addAttribute("postOptions", getPostOptions());
            return "signup"; // フォワードで遷移
        }
        // 投稿をテーブルに格納
        userService.saveUser(userForm);
        // rootへリダイレクト
        return "redirect:/system";
    }
}
