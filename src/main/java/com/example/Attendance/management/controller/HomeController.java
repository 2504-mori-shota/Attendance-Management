package com.example.Attendance.management.controller;


import com.example.Attendance.management.controller.form.AttendanceForm;
import com.example.Attendance.management.controller.form.AttendanceListForm;
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
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.text.ParseException;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@Controller
public class HomeController {



    @Autowired
    private AttendanceService attendanceService;
    @Autowired
    HttpSession session;



    // ホーム画面に月間勤怠情報を表示
    @GetMapping("/home")
    public String home(@RequestParam(value = "userId", required = false) Long userId, Model model,HttpServletRequest request) {

        session = request.getSession();
        UserForm user = (UserForm) session.getAttribute("loginUser");
        if (user ==  null) {
            // userId が指定されていない場合、空の勤怠情報を設定
            model.addAttribute("attendances", List.of(new Attendance()));
        } else {
            // userId が指定されている場合、勤怠情報を取得
            List<AttendanceForm> attendance = attendanceService.getMonthlyAttendance(user.getId(),LocalDate.now());
            AttendanceListForm attendanceListForm = new AttendanceListForm();
            attendanceListForm.setAttendances(attendance);
            model.addAttribute("attendances", attendanceService.getMonthlyAttendance(user.getId(), LocalDate.now()));
            model.addAttribute("attendanceList", attendanceListForm);
        }


        return "home";
    }

    @PostMapping("/application")
    public ModelAndView application (
            HttpServletRequest request,
            @ModelAttribute("attendanceList") AttendanceListForm attendanceForms,
            Model model
    ) throws ParseException {
        session = request.getSession();
        UserForm user = (UserForm) session.getAttribute("loginUser");

        List<AttendanceForm> list = attendanceForms.getAttendances();

        attendanceService.saveAttendanceState(list);

        return new ModelAndView("redirect:/home");
    }







    @RequestMapping("/logout")
    public String logout(HttpServletRequest request) {
        HttpSession session = request.getSession(false); // セッションを取得
        if(session != null) {
            session.invalidate();//セッション破棄
        }
        return "redirect:/";
    }

}

