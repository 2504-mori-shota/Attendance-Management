package com.example.Attendance.management.controller;

import com.example.Attendance.management.controller.form.AttendanceForm;

import com.example.Attendance.management.controller.form.UserForm;
import com.example.Attendance.management.service.AttendanceService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.text.ParseException;


@Controller
public class AttendanceController {

    @Autowired
    HttpSession session;
    @Autowired
    AttendanceService attendanceService;

    @GetMapping("/attendance")
    public ModelAndView newAttend
            (HttpServletRequest request, HttpServletResponse response,
             RedirectAttributes redirectAttributes) {
        session = request.getSession();
        // セッションからユーザーオブジェクトを取得
        UserForm user = (UserForm) session.getAttribute("loginUser");
        if (user == null) {
            redirectAttributes.addFlashAttribute("errorMessageForm", "ログインしてください");
            return new ModelAndView("redirect:/");
        }
        ModelAndView mav = new ModelAndView();
        // form用の空のentityを準備
        AttendanceForm attendanceForm = new AttendanceForm();
        // 画面遷移先を指定
        mav.setViewName("/attendance");
        mav.addObject("formModel", user);
        // 準備した空のFormを保管
        mav.addObject("attendanceInfo", attendanceForm);
        // mav.addObject("errorMessageForm", errorMessages);
        return mav;

    }

//    // コントローラー内に選択肢を返すメソッド プルダウンで使用
//    private Map<Integer, String> getRequestOptions() {
//        List<Request> requests = attendanceService.findAllRequest();
//        Map<Integer, String> options = new LinkedHashMap<>();
//
//        for(int i = 0; i < requests.size(); i++ ){
//            Request request = requests.get(i);
//
//            String name = request.getState();
//            options.put(i+1, name);
//        }
//
//        return options;
//    }


    @PostMapping("/addAttendance")
    public ModelAndView addContent(
            HttpServletRequest request, HttpServletResponse response,
            @Valid
            @ModelAttribute("attendanceInfo") AttendanceForm attendanceForm,
            BindingResult result,
            Model model
    ) throws ParseException {
        session = request.getSession();
        UserForm user = (UserForm) session.getAttribute("loginUser"); // セッションから再取得


        attendanceForm.setUserId(user.getId());
        // 投稿をテーブルに格納
        attendanceService.saveAttendance(attendanceForm);
        // rootへリダイレクト
        return new ModelAndView("redirect:/home");

    }


}