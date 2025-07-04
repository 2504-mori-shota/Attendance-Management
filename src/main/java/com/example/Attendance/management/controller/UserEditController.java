package com.example.Attendance.management.controller;

import com.example.Attendance.management.controller.form.UserForm;
import com.example.Attendance.management.repository.entity.Post;
import com.example.Attendance.management.service.UserService;
import io.micrometer.common.util.StringUtils;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Controller
public class UserEditController {
    @Autowired
    UserService userService;
    @Autowired
    HttpSession session;

    /*("/userEdit/id={id}")の（id=）をつけずに｛"/userEdit/{id}"｝にしてしまうと
    　 if(!StringUtils.isBlank(strId) && strId.matches("^[0-9]*$"))が機能しなくなる
     */
    @GetMapping("/useredit/id={id}")
    public ModelAndView newContent(
            @PathVariable("id") String strId,
            HttpServletRequest request,
            RedirectAttributes redirectAttributes) throws IOException {
        ModelAndView mav = new ModelAndView();

        //URLパターンチェック
        UserForm user = null;
        if (!StringUtils.isBlank(strId) && strId.matches("^[0-9]*$")) {
            user = userService.findById(Integer.parseInt(strId));
            // 準備した空のFormを保管
            mav.addObject("formModel", user);
        }
        if (user == null) {
            redirectAttributes.addFlashAttribute("errorMessageForm", "不正なパラメータが入力されました");
            return new ModelAndView("redirect:/system");
        }

        //ログインユーザ情報チェック
        session = request.getSession();
        UserForm sessionUser = (UserForm) session.getAttribute("loginUser");

        //FilterConfig及びLoginFilterの機能の代替
        //"userEdit/{id}"の{id}の部分がFilterConfigで記載方法がない
        if (sessionUser == null) {
            session.setAttribute("errorMessageForm", "ログインしてください");
            return new ModelAndView("redirect:/");
        }

        List<UserForm> users = userService.findByIdWithPost(sessionUser.getId());
        UserForm userInfoForm = users.get(0);

        if (userInfoForm.getPostId() != 3) {
            redirectAttributes.addFlashAttribute("errorMessageForm", "不正なパラメータが入力されました");
            return new ModelAndView("redirect:/system");
        }
        // 画面遷移先を指定
        mav.setViewName("/useredit");
        // 準備した空のFormを保管
        mav.addObject("postOptions", getPostOptions());
        // mav.addObject("errorMessageForm", errorMessages);
        return mav;
    }

    // コントローラー内に選択肢を返すメソッド プルダウンで使用
    private Map<Integer, String> getPostOptions() {
        List<Post> posts = userService.findAllPost();
        Map<Integer, String> options = new LinkedHashMap<>();
        //↓ソースコードべた書き解消
        for (int i = 0; i < posts.size(); i++) {
            Post post = posts.get(i);

            String name = post.getPostName();
            options.put(i + 1, name);
        }

        return options;
    }


    @PostMapping("/update")
    public String updateUser(
            @Validated(UserForm.EditGroup.class)
            @Valid @ModelAttribute("formModel") UserForm userForm,
            BindingResult result,
            Model model) {
        // パスワード確認チェック
        if (!result.hasFieldErrors("passwordConfirm") &&
                !userForm.getPassword().equals(userForm.getPasswordConfirm())) {
            result.rejectValue("passwordConfirm", null, "パスワードとパスワード確認が一致しません");
        }
        UserForm userPass = userService.findByAccount(userForm.getAccount());
        // アカウント重複チェック
        if ((userPass != null) && (userPass.getId() != userForm.getId())) {
            result.rejectValue("account", "duplicate", "アカウントが重複しています");
        }


        if (userForm.getPassword().matches("^[a-zA-Z]+$") && (userForm.getPassword().length() >= 6 && userForm.getPassword().length() <= 20)) {
            userService.saveUser(userForm);
            return "redirect:/management";
        }


        if (!userForm.getPassword().isBlank() && !userForm.getPassword().matches("^[a-zA-Z]+$")) {
            result.rejectValue("password", "duplicate", "パスワードは半角かつ6文字以上20文字以内で入力してください");
        }

        if ((!userForm.getPassword().isBlank() && userForm.getPassword().length() < 6) || userForm.getPassword().length() > 20) {
            result.rejectValue("password", "duplicate", "パスワードは6文字以上20文字以内で入力してください");
        }

        if (result.hasErrors()) {
            model.addAttribute("postOptions", getPostOptions());
            return "useredit"; // フォワードで遷移
        }

        userForm.setPassword(userPass.getPassword());

        userService.saveUser(userForm);
        return "redirect:/system";
    }
}
