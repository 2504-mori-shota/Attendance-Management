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
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
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
             @RequestParam(value = "date", required = false)String date,
             Model model,
             RedirectAttributes redirectAttributes) {
        //引数チェック
        if (date.isBlank()) {
            redirectAttributes.addFlashAttribute("errorMessageForm", "不正なパラメータが入力されました");
            return new ModelAndView("redirect:/home");
        }

        UserForm user = (UserForm) session.getAttribute("loginUser");
        ModelAndView mav = new ModelAndView();
        // form用の空のentityを準備
        AttendanceForm attendanceForm = new AttendanceForm();
        // URLにdateを直打ちした際のバリデーション
        int year;
        int month;
        int day;
        try {
            year = Integer.parseInt(date.substring(0, 4));
            month = Integer.parseInt(date.substring(5, 7));
            day = Integer.parseInt(date.substring(8, 10));

            //取得処理
            Calendar calendar = Calendar.getInstance();
            calendar.set(Calendar.YEAR, year);
            calendar.set(Calendar.MONTH, month - 1);
            int result = calendar.getActualMaximum(Calendar.DAY_OF_MONTH);

            if (date.length() != 10 || date.charAt(4) != '-' || date.charAt(7) != '-' || month > 12 || day > result) {
                throw new Exception();
            }
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessageForm", "不正なパラメータが入力されました");
            return new ModelAndView("redirect:/home");
        }
        date = String.format("%04d",year) + "-" + String.format("%02d",month) + "-" + String.format("%02d",day);
        attendanceForm.setDate(date);
        // 画面遷移先を指定
        mav.setViewName("/attendance");
        mav.addObject("formModel", user);
        // 準備した空のFormを保管
        mav.addObject("attendanceInfo", attendanceForm);
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