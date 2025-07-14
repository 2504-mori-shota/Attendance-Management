package com.example.Attendance.management.controller;

import com.example.Attendance.management.controller.form.AttendanceForm;
import com.example.Attendance.management.controller.form.UserForm;
import com.example.Attendance.management.service.AttendanceService;
import jakarta.servlet.http.HttpServletRequest;
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

    @GetMapping("/attendanceedit")
    public ModelAndView newAttend(
            @RequestParam(name="id", required = false) String strId,
             HttpServletRequest request,
             RedirectAttributes redirectAttributes) {
        // セッションからユーザーオブジェクトを取得
        session = request.getSession();
        UserForm user = (UserForm) session.getAttribute("loginUser");

        //なぜかisBlankだけだとnullをひっかけてくれない
        if (strId == null || strId.isBlank()) {
            redirectAttributes.addFlashAttribute("errorMessageForm", "不正なパラメータが入力されました");
            return new ModelAndView("redirect:/home");
        }

        AttendanceForm attendanceForm = attendanceService.findById(Integer.parseInt(strId));

        //idが存在しない場合のバリデーション
        if (attendanceForm == null){
            redirectAttributes.addFlashAttribute("errorMessageForm", "不正なパラメータが入力されました");
            return new ModelAndView("redirect:/home");
        }
        //自分が登録している勤怠idを入力したときのバリデーション
        if (user.getId() != attendanceForm.getUserId()) {
            redirectAttributes.addFlashAttribute("errorMessageForm", "無効なアクセスです");
            return new ModelAndView("redirect:/home");
        }

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

        if (result.hasErrors()) {
            ModelAndView mav = new ModelAndView("attendanceedit");
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
