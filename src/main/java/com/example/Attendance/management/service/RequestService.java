package com.example.Attendance.management.service;

import com.example.Attendance.management.controller.form.AttendanceForm;
import com.example.Attendance.management.controller.form.RequestForm;
import com.example.Attendance.management.repository.AttendanceRepository;
import com.example.Attendance.management.repository.RequestRepository;
import com.example.Attendance.management.repository.entity.Request;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
public class RequestService {
    @Autowired
    RequestRepository requestRepository;
    @Autowired
    AttendanceRepository attendanceRepository;

    //全件取得
    public List<RequestForm> findRequest() {
        List<Request> requests = new ArrayList<Request>();
        requests = requestRepository.findAllByOrderByCreatedDate();
        List<RequestForm> result = setRequestForm(requests);
        return result;
    }

    //
    public List<RequestForm> findRequestById(int id){
        List<Request> request = requestRepository.findById(id);
        List<RequestForm> result = setRequestForm(request);
        return result;
    }

    public List<RequestForm> findRequestByUserId(int id){
        List<Request> request = requestRepository.findByUserId(id);
        List<RequestForm> result = setRequestForm(request);
        return result;
    }

    public void updateRequest(RequestForm requestForm){
        Request request = setRequest(requestForm);
        requestRepository.save(request);
    }

    public void deleteRequest(int requestId){
        requestRepository.deleteById(requestId);
    }

    private List<RequestForm> setRequestForm(List<Request> requests) {
        List<RequestForm> requestForms = new ArrayList<RequestForm>();
        for (Request request : requests){
            RequestForm requestForm = new RequestForm();

            requestForm.setId(request.getId());
            requestForm.setUserId(request.getUserId());
            requestForm.setState(request.getState());
            requestForm.setStartDate(request.getStartDate());
            requestForm.setEndDate(request.getEndDate());
            requestForm.setCreatedDate(request.getCreatedDate());
            requestForm.setUpdatedDate(request.getUpdatedDate());
            requestForm.setUser(request.getUser());

            requestForms.add(requestForm);
        }
        return requestForms;
    }

    private Request setRequest(RequestForm requestForm) {
        Request request = new Request();

        request.setId(requestForm.getId());
        request.setUserId(requestForm.getUserId());
        request.setState(requestForm.getState());
        request.setStartDate(requestForm.getStartDate());
        request.setEndDate(requestForm.getEndDate());
        request.setCreatedDate(requestForm.getCreatedDate());
        request.setUpdatedDate(requestForm.getUpdatedDate());

        return request;
    }

    public void saveRequest(AttendanceForm start, AttendanceForm end) throws ParseException {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        String strStartDate = start.getAttendance();
        String strEndDate = end.getAttendance();

        Date startDate = formatter.parse(strStartDate);
        Date endDate = formatter.parse(strEndDate);

        RequestForm requestForm = new RequestForm();
        requestForm.setUserId(start.getUserId());
        requestForm.setState(0);
        requestForm.setStartDate(startDate);
        requestForm.setEndDate(endDate);
        Request request = setRequest(requestForm);
        requestRepository.save(request);
    }

}
