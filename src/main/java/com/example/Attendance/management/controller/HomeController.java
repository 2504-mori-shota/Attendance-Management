package com.example.Attendance.management.controller;


import com.example.Attendance.management.controller.form.AttendanceForm;
import com.example.Attendance.management.controller.form.UserForm;
import com.example.Attendance.management.repository.entity.Attendance;
import com.example.Attendance.management.service.AttendanceService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

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
            List<Attendance> attendance = attendanceService.getMonthlyAttendance(user.getId(),LocalDate.now());
            model.addAttribute("attendances", attendanceService.getMonthlyAttendance(user.getId(), LocalDate.now()));
        }


        return "home";
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

