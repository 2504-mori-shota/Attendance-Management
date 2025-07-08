package com.example.Attendance.management.controller;


import com.example.Attendance.management.controller.form.AttendanceForm;
import com.example.Attendance.management.controller.form.AttendanceListForm;
import com.example.Attendance.management.controller.form.UserForm;
import com.example.Attendance.management.service.AttendanceService;
import com.example.Attendance.management.service.RequestService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.text.ParseException;
import java.time.Duration;
import java.time.LocalDate;
import java.util.List;

@Controller
public class HomeController {


    @Autowired
    RequestService requestService;
    @Autowired
    private AttendanceService attendanceService;
    @Autowired
    HttpSession session;


    // ホーム画面に月間勤怠情報を表示
    @GetMapping("/home")
    public String home(
            @RequestParam(value = "year", required = false) Integer year,
            @RequestParam(value = "month", required = false) Integer month,
            Model model, HttpServletRequest request) {

        session = request.getSession();


        //バリエーションチャック
        UserForm user = (UserForm) request.getSession().getAttribute("loginUser");
        if (user == null) {
            return "redirect:/";
        }

        LocalDate now = LocalDate.now();
        if (year == null || month == null) {
            year = now.getYear();
            month = now.getMonthValue();
        }

        // 月をまたいだときの対応
        if (month < 1) {
            year--;
            month = 12;
        }
        if (month > 12) {
            year++;
            month = 1;

        }

        //指定した月のデータを表示
        LocalDate target = LocalDate.of(year, month, 1);
        List<AttendanceForm> attendanceForms = attendanceService.getMonthlyAttendance(user.getId(), target);
        int totalDays = target.lengthOfMonth();
        //serviceで計算した労働時間合計を受け取る
        Duration totalWorkingTime = attendanceService.calculateTotalWorkingTime(attendanceForms);
        // 時間と分に変換
        long hours = totalWorkingTime.toHours();
        long minutes = totalWorkingTime.toMinutes() % 60;

        List<AttendanceForm> attendance = attendanceService.getMonthlyAttendance(user.getId(),LocalDate.now());
        AttendanceListForm attendanceListForm = new AttendanceListForm();
        attendanceListForm.setAttendances(attendance);
        model.addAttribute("attendances", attendanceService.getMonthlyAttendance(user.getId(), LocalDate.now()));
        model.addAttribute("attendanceList", attendanceListForm);

        // modelにオブジェクト格納してreturnで返す
        model.addAttribute("year", year);
        model.addAttribute("month", month);
        model.addAttribute("totalDays", totalDays);
        model.addAttribute("attendances", attendanceForms);
        model.addAttribute("statuses", AttendanceForm.Status.values());
        model.addAttribute("totalWorkingTime", String.format("%02d:%02d", hours, minutes));

        return"home";
}

        @PostMapping("/application")
        public ModelAndView application (
                HttpServletRequest request,
                @ModelAttribute("attendanceList") AttendanceListForm attendanceForms,RedirectAttributes redirectAttributes,
                Model model) throws ParseException {
            session = request.getSession();
            UserForm user = (UserForm) session.getAttribute("loginUser");

            if (attendanceForms.getAttendances() == null){
                redirectAttributes.addFlashAttribute("errorMessageForm", "今月の勤怠情報がありません");
                return new ModelAndView("redirect:/home");
            }
            List<AttendanceForm> list = attendanceForms.getAttendances();

            for (AttendanceForm attendanceForm : list) {
                if (attendanceForm.getState() == 1 || attendanceForm.getState() == 4) {
                    redirectAttributes.addFlashAttribute("errorMessageForm", "既に申請済みです");
                    return new ModelAndView("redirect:/home");
                }

            }

            attendanceService.saveAttendanceState(list,1);

            requestService.saveRequest(list.get(0), list.get(list.size() - 1));

            return new ModelAndView("redirect:/home");
        }

    @RequestMapping("/logout")
    public String logout(HttpServletRequest request) {
        HttpSession session = request.getSession(false); // セッションを取得
        if (session != null) {
            session.invalidate();//セッション破棄
        }
        return "redirect:/";
    }
    @DeleteMapping("/Attendance/delete/{id}")
    public String deleteAttendance(@PathVariable int id){
        attendanceService.deleteAttendance(id);
        return "redirect:/home";
    }

}

