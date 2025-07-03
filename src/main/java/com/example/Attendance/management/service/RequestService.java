package com.example.Attendance.management.service;

import com.example.Attendance.management.controller.form.RequestForm;
import com.example.Attendance.management.repository.RequestRepository;
import com.example.Attendance.management.repository.entity.Request;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class RequestService {
    @Autowired
    RequestRepository requestRepository;

    public List<RequestForm> findRequest() {
        List<Request> requests = new ArrayList<Request>();
        requests = requestRepository.findAllByOrderByCreatedDate();
        List<RequestForm> result = setRequestForm(requests);
        return result;
    }

    private List<RequestForm> setRequestForm(List<Request> requests) {
        List<RequestForm> requestForms = new ArrayList<RequestForm>();
        for (Request request : requests){
            RequestForm requestForm = new RequestForm();

            requestForm.setId(request.getId());
            requestForm.setUserId(requestForm.getUserId());
            requestForm.setState(requestForm.getState());
            requestForm.setStartDate(request.getStartDate());
            requestForm.setEndDate(request.getEndDate());
            requestForm.setCreatedDate(request.getCreatedDate());
            requestForm.setUpdatedDate(request.getUpdatedDate());

            requestForms.add(requestForm);
        }
        return requestForms;
    }
}
