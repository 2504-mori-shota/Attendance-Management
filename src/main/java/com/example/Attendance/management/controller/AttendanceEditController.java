package com.example.Attendance.management.controller;

import com.example.Attendance.management.controller.form.AttendanceForm;
import com.example.Attendance.management.controller.form.UserForm;
import com.example.Attendance.management.repository.entity.Attendance;
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
import java.util.Date;
import java.util.List;

@Controller
public class AttendanceEditController {
    @Autowired
    HttpSession session;
    @Autowired
    AttendanceService attendanceService;

    @PostMapping("/attendanceedit")
    public ModelAndView newAttend(
            @RequestParam("id") String strId,
             HttpServletRequest request,
             RedirectAttributes redirectAttributes) {
        session = request.getSession();
        // セッションからユーザーオブジェクトを取得
        UserForm user = (UserForm) session.getAttribute("loginUser");
        if (user == null) {
            redirectAttributes.addFlashAttribute("errorMessageForm", "ログインしてください");
            return new ModelAndView("redirect:/");
        }

        AttendanceForm attendanceForm = attendanceService.findById(Integer.parseInt(strId));
        ModelAndView mav = new ModelAndView();
        // form用の空のentityを準備
        // 画面遷移先を指定
        mav.setViewName("/attendanceedit");
        mav.addObject("formModel", user);
        // Formに元の情報を保管
        mav.addObject("attendanceInfo", attendanceForm);
        // mav.addObject("errorMessageForm", errorMessages);
        return mav;

    }

    @PostMapping("/updateAttendance")
    public ModelAndView updateContent(
            HttpServletRequest request,
            @Valid
            @ModelAttribute("attendanceInfo") AttendanceForm attendanceForm,
            BindingResult result,
            @RequestParam(name = "id", required = false) String strId,
            @RequestParam(name = "created_date", required = false) String createdDate,
            Model model
    ) throws ParseException {

        session = request.getSession();
        UserForm user = (UserForm) session.getAttribute("loginUser"); // セッションから再取得

        List<AttendanceForm> attendanceFormList = attendanceService.findAllByUserId(user.getId(), attendanceForm.getDate());


        for (int i = 0; i < attendanceFormList.size(); i++) {
            AttendanceForm attendance = attendanceFormList.get(i);
            //trueでifに入る
            if (attendanceService.findByTime(attendance, attendanceForm)){
                result.rejectValue("attendance", "duplicate", "勤務時間が重複しています");
            }
        }

        if (result.hasErrors()) {
            ModelAndView mav = new ModelAndView("attendanceedit");
            mav.addObject("attendanceInfo", attendanceForm);
            mav.addObject("formModel", user);
            // errorsはバインディング済みなので自動的にビューへ渡る
            return mav;
        }
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
        Date date = format.parse(createdDate);
        attendanceForm.setUserId(user.getId());
        attendanceForm.setId(Integer.parseInt(strId));
        attendanceForm.setCreatedDate(date);
        // 投稿をテーブルに格納
        attendanceService.saveAttendance(attendanceForm);
        // rootへリダイレクト
        return new ModelAndView("redirect:/home");

    }


}
