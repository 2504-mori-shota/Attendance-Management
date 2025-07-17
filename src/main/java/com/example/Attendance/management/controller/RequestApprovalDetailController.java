package com.example.Attendance.management.controller;

import com.example.Attendance.management.controller.form.AttendanceForm;
import com.example.Attendance.management.controller.form.AttendanceListForm;
import com.example.Attendance.management.controller.form.RequestForm;
import com.example.Attendance.management.controller.form.UserForm;
import com.example.Attendance.management.service.AttendanceService;
import com.example.Attendance.management.service.RequestService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.text.ParseException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Controller
public class RequestApprovalDetailController{

    @Autowired
    RequestService requestService;
    @Autowired
    AttendanceService attendanceService;
    @Autowired
    private HttpSession session;

    @GetMapping("/request/approval/detail")
    public String view (
            HttpServletRequest request,
            @RequestParam(value = "id", required = false) String id,
            RedirectAttributes redirectAttributes, Model model){

        // エラーメッセージのリスト
        List<String> errorMessages = new ArrayList<String>();

        //引数チェック
        if (id.isBlank() || !id.matches("^[0-9]+$")){
            redirectAttributes.addFlashAttribute("errorMessageForm", "不正なパラメータが入力されました");
            return "redirect:/home";
        }
        // 申請情報取得
        int requestId = Integer.parseInt(id);
        List<RequestForm> requestListData = requestService.findRequestById(requestId);

        // 存在しないidをURLで直打ちされた場合
        if (requestListData == null) {
            redirectAttributes.addFlashAttribute("errorMessageForm", "不正なパラメータが入力されました");
            return "redirect:/home";
        }

        //申請のステータスが承認済み以外の時
        if (requestListData.get(0).getState() != 2 ) {
            redirectAttributes.addFlashAttribute("errorMessageForm", "無効なアクセスです");
            return "redirect:/home";
        }

        //権限チェック
        session = request.getSession();
        UserForm loginUser = (UserForm) session.getAttribute("loginUser");
        //機能追加で変更を加えた
        if ((loginUser.getPostId() != 2 && loginUser.getPostId() != 3) && loginUser.getId() != requestListData.get(0).getUserId()){
            redirectAttributes.addFlashAttribute("errorMessageForm", "無効なアクセスです");
            return "redirect:/home";
        }

        //requestをもとに勤怠情報を取得
        RequestForm requestForm = requestListData.get(0);
        int requestUserId = requestForm.getUserId();
        //申請されている勤怠情報取得
        List<AttendanceForm> attendanceForms = attendanceService.findAttendanceByRequest(requestForm);
        AttendanceListForm attendanceListForm = new AttendanceListForm();
        attendanceListForm.setAttendances(attendanceForms);

        //月の日数を取得
        String attendanceDate = attendanceForms.get(0).getAttendance();
        LocalDate attendanceLocalDate = LocalDate.parse(attendanceDate, DateTimeFormatter.ofPattern("yyyy-MM-dd hh:mm"));
        int totalDays = attendanceLocalDate.lengthOfMonth();
        //指定した月のデータを表示
        int year = attendanceLocalDate.getYear();
        int month = attendanceLocalDate.getMonthValue();

        //改行、空欄縦線のためにリストを作成
        List<Integer> dataNumList = new ArrayList<Integer>();
        for (int i = 0; i < totalDays; i++){
            LocalDate day = LocalDate.of(year, month, i+1);
            List<AttendanceForm> dayAttendanceForms = attendanceService.getDailyAttendance(requestUserId, day);
            dataNumList.add(dayAttendanceForms.size());
        }

        // modelにオブジェクト格納してreturnで返す
        model.addAttribute("month", month);
        model.addAttribute("totalDays", totalDays);
        model.addAttribute("dataNumList", dataNumList);
        model.addAttribute("requestId", requestId);
        model.addAttribute("requestUserId", requestUserId);
        model.addAttribute("attendanceList", attendanceListForm);
        model.addAttribute("statuses",AttendanceForm.Status.values());

        return "request_approval_detail";
    }

    // 申請情報更新
    @PostMapping("/request/approval/cancel")
    public String approval (@ModelAttribute("requestId") String id, Model model) throws ParseException {
        //requestの更新処理↓↓
        int requestId = Integer.parseInt(id);
        List<RequestForm> requests = requestService.findRequestById(requestId);
        RequestForm request = requests.get(0);
        request.setState(3);
        //requestの更新処理
        requestService.updateRequest(request);

        //attendanceの更新処理↓↓
        List<AttendanceForm> attendanceForms = attendanceService.findAttendanceByRequest(request);
        attendanceService.saveAttendanceState(attendanceForms,3);
        return "redirect:/request/approval/list";
    }

}
