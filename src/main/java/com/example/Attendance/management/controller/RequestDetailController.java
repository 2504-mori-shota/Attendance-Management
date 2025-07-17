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
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.text.ParseException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Controller
public class RequestDetailController {

    @Autowired
    RequestService requestService;
    @Autowired
    AttendanceService attendanceService;
    @Autowired
    private HttpSession session;

    @GetMapping("/request/detail")
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

        //申請のステータスが申請中以外の時
        if (requestListData.get(0).getState() != 1 || requestListData.get(0).getState() != 3) {
            redirectAttributes.addFlashAttribute("errorMessageForm", "無効なアクセスです");
            return "redirect:/home";
        }

        //権限チェック
        session = request.getSession();
        UserForm loginUser = (UserForm) session.getAttribute("loginUser");
        if (loginUser.getPostId() != 2 && loginUser.getId() != requestListData.get(0).getUserId()){
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

        return "request_detail";
    }

    // 申請承認機能　　※ここでエラーが起きている可能性が高い
    @PostMapping("/request/update")
    public String approval (@ModelAttribute("requestId") String id, Model model) throws ParseException {
        //requestの承認処理↓↓
        int requestId = Integer.parseInt(id);
        List<RequestForm> requests = requestService.findRequestById(requestId);
        RequestForm request = requests.get(0);
        request.setState(2);
        //requestの承認処理
        requestService.updateRequest(request);

        //attenddanceの承認処理↓↓
        List<AttendanceForm> attendanceForms = attendanceService.findAttendanceByRequest(request);
        attendanceService.saveAttendanceState(attendanceForms,2);
        return "redirect:/request/list";
    }

    // 申請差戻機能
    @PostMapping("/request/return")
    public String returnRequest (@ModelAttribute("attendanceList") AttendanceListForm attendanceListForm,
                                 @ModelAttribute("requestId") String strRequestId,
                                 Model model) throws ParseException {
        //attendanceの更新処理↓↓
        List<AttendanceForm> attendanceForms = attendanceListForm.getAttendances();
        for (AttendanceForm attendanceForm : attendanceForms){
            int attendanceId = attendanceForm.getId();
            AttendanceForm dbAttendanceForm = attendanceService.findById(attendanceId);
            if (attendanceForm.getCheckbox()){
                //statusを差戻済みX(5)に更新
                dbAttendanceForm.setState(5);
            } else {
                //statusを差戻済み〇(4)に更新
                dbAttendanceForm.setState(4);
            }
            attendanceService.saveAttendance(dbAttendanceForm);
        }

        //requestの更新処理↓↓
        int requestId = Integer.parseInt(strRequestId);
        List<RequestForm> requests = requestService.findRequestById(requestId);
        RequestForm request = requests.get(0);
        //statusを差戻済み(4)に更新
        request.setState(4);
        //requestの更新処理
        requestService.updateRequest(request);
        return "redirect:/request/list";
    }

    @PostMapping("/myrequest/delete")
    public String myRequestDelete (@ModelAttribute("requestId") String strRequestId, Model model) throws ParseException {
        //requestIdのint化
        int requestId = Integer.parseInt(strRequestId);
        //ステータス変更のためにattendanceを取得
        List<RequestForm> requestForms = requestService.findRequestById(requestId);
        List<AttendanceForm> attendanceForms = attendanceService.findAttendanceByRequest(requestForms.get(0));
        //ステータスを未申請(0)に変更
        attendanceService.saveAttendanceState(attendanceForms,0);
        //requestの削除
        requestService.deleteRequest(requestId);
        return "redirect:/home";
    }
}
