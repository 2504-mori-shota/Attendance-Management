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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.text.ParseException;
import java.util.List;


@Controller
public class AttendanceController {

    @Autowired
    HttpSession session;
    @Autowired
    AttendanceService attendanceService;

    @GetMapping("/attendance")
    public ModelAndView newAttend
            (HttpServletRequest request,
             @RequestParam("date")String date,
             Model model,
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
        attendanceForm.setDate(date);
        // 画面遷移先を指定
        mav.setViewName("/attendance");
        mav.addObject("formModel", user);
        // 準備した空のFormを保管
        mav.addObject("attendanceInfo", attendanceForm);
//        model.addAttribute("date", date);
//        model.addAttribute("Year", year);
//        model.addAttribute("Month", month);
        // mav.addObject("errorMessageForm", errorMessages);
        return mav;

    }


    @PostMapping("/addAttendance")
    public ModelAndView addContent(
            HttpServletRequest request,
            @Valid
            @ModelAttribute("attendanceInfo") AttendanceForm attendanceForm,
            BindingResult result,
            Model model
    ) throws ParseException {

        session = request.getSession();
        UserForm user = (UserForm) session.getAttribute("loginUser"); // セッションから再取得

        //日付重複チェック
        List<AttendanceForm> attendanceFormList = attendanceService.findAllByUserId(user.getId(), attendanceForm.getDate());

        if (result.hasErrors()) {
            ModelAndView mav = new ModelAndView("attendance");
            mav.addObject("attendanceInfo", attendanceForm);
            mav.addObject("formModel", user);
            // errorsはバインディング済みなので自動的にビューへ渡る
            return mav;
        }

        for (int i = 0; i < attendanceFormList.size(); i++) {
            AttendanceForm attendance = attendanceFormList.get(i);
            //trueでifに入る
            if (attendanceService.findByTime(attendance, attendanceForm)){
                result.rejectValue("attendance", "duplicate", "勤務時間が重複しています");
            }
        }

        if (result.hasErrors()) {
            ModelAndView mav = new ModelAndView("attendance");
            mav.addObject("attendanceInfo", attendanceForm);
            mav.addObject("formModel", user);
            // errorsはバインディング済みなので自動的にビューへ渡る
            return mav;
        }
        attendanceForm.setUserId(user.getId());

        // 投稿をテーブルに格納
        attendanceService.saveAttendance(attendanceForm);
        // rootへリダイレクト
        return new ModelAndView("redirect:/home");

    }


}