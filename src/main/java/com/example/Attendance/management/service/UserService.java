package com.example.Attendance.management.service;

import com.example.Attendance.management.controller.form.UserForm;
import com.example.Attendance.management.repository.PostRepository;
import com.example.Attendance.management.repository.UserRepository;
import com.example.Attendance.management.repository.entity.Post;
import com.example.Attendance.management.repository.entity.User;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
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

        Date date = new Date();

        User report = new User();
        report.setId(reqUser.getId());
        report.setAccount(reqUser.getAccount());

        if (!reqUser.getPassword().isBlank()) {
            String encodedPassword = passwordEncoder.encode(reqUser.getPassword());
            report.setPassword(encodedPassword);
            //元のハッシュ化されたパスワードを取得
        } else {
            List<User> user = userRepository.findById(reqUser.getId());
            User user1 = user.get(0);
            String password = user1.getPassword();
            report.setPassword(password);
        }

        report.setName(reqUser.getName());
        report.setPostId(reqUser.getPostId());
        report.setIsStopped(reqUser.getIsStopped());
        report.setUpdatedDate(date);
        return report;
    }

    public UserForm login(String account, String password) {

        List<User> results = userRepository.findByAccount(account);

        if(results.size() == 0) {
            return null;
        }
        if (passwordEncoder.matches(password,results.get(0).getPassword())){
            List<UserForm> users = setUserForm(results);
            return users.get(0);
        } else {
            return null;
        }
    }

    public List<UserForm>  findByAllUser(){
        List<User> userList = userRepository.findAllByOrderByIdAsc();
        List<UserForm> users = setUserForm(userList);
        return users;

    }

    public List<UserForm> findUserById(int id){

        List<User> results = userRepository.findByIdWithPost(id);
        List<UserForm> users = setUserForm(results);
        return users;
    }


    public List<UserForm> findByIdWithPost(int id){
        List<User> userList = userRepository.findByIdWithPost(id);
        List<UserForm> users = setUserForm(userList);
        return users;
    }

    public UserForm findById(int id){

        List<User> user = userRepository.findById(id);

        //URLパラメーターから存在しないidで情報をDBから探しに行ったときにnullで返す
        if(user == null){
            return null;
        }

        List<UserForm> userForm = setUserForm(user);

        return userForm.get(0);
    }

    @Transactional
    public void updateStatus(Integer id, int status) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("指定されたidが見つかりません: ID=" + id));
        user.setIsStopped(status);
        user.setUpdatedDate(new Date());
        userRepository.save(user);
    }

    public UserForm findByAccount(String account){

        List<User> results = userRepository.findByAccount(account);
        List<UserForm> users = setUserForm(results);
        if(users.isEmpty()){
            return null;
        }
        return users.get(0);
    }
}
