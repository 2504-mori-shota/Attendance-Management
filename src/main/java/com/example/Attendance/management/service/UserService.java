package com.example.Attendance.management.service;

import com.example.Attendance.management.controller.form.UserForm;
import com.example.Attendance.management.repository.PostRepository;
import com.example.Attendance.management.repository.UserRepository;
import com.example.Attendance.management.repository.entity.Post;
import com.example.Attendance.management.repository.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {
    @Autowired
    UserRepository userRepository;
    @Autowired
    PasswordEncoder passwordEncoder;
    @Autowired
    PostRepository postRepository;

    //アカウント重複チェック用
    public boolean AccountDuB(String account) {
        List<User> results = userRepository.findByAccount(account);
        return !results.isEmpty();
    }

    public List<Post> findAllPost(){
        return postRepository.findAll();
    }

    private List<UserForm> setUserForm(List<User> results) {
        List<UserForm> users = new ArrayList<>();

        for (int i = 0; i < results.size(); i++) {
            UserForm user = new UserForm();
            User result = results.get(i);
            user.setId(result.getId());
            user.setAccount(result.getAccount());
            user.setName(result.getName());
            user.setPassword(result.getPassword());
            user.setPostId(result.getPostId());
            user.setIsStopped(result.getIsStopped());
            user.setPost(result.getPost());
            user.setCreatedDate(result.getCreatedDate());
            user.setUpdatedDate(result.getUpdatedDate());
            users.add(user);
        }
        return users;
    }


    /*
     * レコード追加
     */
    public void saveUser(UserForm reqUser) {
        User saveUser = setUserEntity(reqUser);
        userRepository.save(saveUser);
    }

    private User setUserEntity(UserForm reqUser) {
        String encodedPassword = passwordEncoder.encode(reqUser.getPassword());

        User report = new User();
        report.setId(reqUser.getId());
        report.setAccount(reqUser.getAccount());
        report.setPassword(encodedPassword);
        report.setName(reqUser.getName());
        report.setPostId(reqUser.getPostId());
        report.setIsStopped(reqUser.getIsStopped());
        return report;
    }
}
