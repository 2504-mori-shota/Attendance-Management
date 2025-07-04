package com.example.Attendance.management.controller;

import com.example.Attendance.management.controller.form.AttendanceForm;
import com.example.Attendance.management.controller.form.RequestForm;
import com.example.Attendance.management.repository.entity.Request;
import com.example.Attendance.management.service.AttendanceService;
import com.example.Attendance.management.service.RequestService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class RequestDetailController {

    @Autowired
    RequestService requestService;
    @Autowired
    AttendanceService attendanceService;
    @Autowired
    private HttpSession session;

    @GetMapping("/request/detail/{id}")
    public String view (@RequestParam("id") String id, Model model){
        // エラーメッセージのリスト
        List<String> errorMessages = new ArrayList<String>();

        // バリデーションチェック (URLチェック)　null/半角数字以外/存在しないID
        if (id.isBlank() || (!id.matches("^[0-9]+$"))) {
            errorMessages.add("不正なパラメータが入力されました");
            model.addAttribute("errorMessages", errorMessages);
            return "redirect:/request/list";
        }
        // ユーザ情報取得
        int requestId = Integer.parseInt(id);
        List<RequestForm> requestListData = requestService.findRequestById(requestId);

        // 存在しないidをURLで直打ちされた場合
        if (requestListData == null) {
            errorMessages.add("不正なパラメータが入力されました");
            session.setAttribute("errorMessages", errorMessages);
            return "redirect:/manager/form";
        }

        List<AttendanceForm> attendanceForms = attendanceService.

        // mavにオブジェクト格納してreturnで返す
        model.addAttribute("users", userData);
        mav.addObject("branchChoices", branchChoices);
        mav.addObject("departmentChoices", departmentChoices);
        mav.setViewName("/setting");

        return mav;

        return "request_list";
    }
}
