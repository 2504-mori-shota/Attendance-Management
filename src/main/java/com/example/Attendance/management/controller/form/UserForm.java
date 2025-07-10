package com.example.Attendance.management.controller.form;

import com.example.Attendance.management.repository.entity.Post;

import jakarta.persistence.Transient;
import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
public class UserForm {

    //ユーザー登録とログインする際の、バリデーションがかぶるため、グループ分けをする。
    public interface SignUpGroup {}
    public interface LoginGroup {}
    public interface EditGroup{}

    @Transient
    private String error;

    private int id;

    @NotBlank(message = "アカウントを入力してください", groups = {LoginGroup.class, SignUpGroup.class, EditGroup.class})
    @Pattern(regexp = "^[^　]*$", message = "アカウントを入力してください", groups = {LoginGroup.class, SignUpGroup.class, EditGroup.class})
    @Pattern(regexp = "^[a-zA-Z0-9]+$", message = "半角英数字で入力してください", groups = {SignUpGroup.class, EditGroup.class})
    @Size(min = 6, max = 20, message = "6文字以上20文字以下で入力してください", groups = {SignUpGroup.class, EditGroup.class})
    private String account;

    @NotBlank(message = "パスワードを入力してください", groups = {LoginGroup.class, SignUpGroup.class})
    @Pattern(regexp = "^[^　]*$", message = "パスワードを入力してください",groups = {LoginGroup.class, SignUpGroup.class})
    @Size(min = 6, max = 20, message = "6文字以上20文字以内で入力してください", groups = SignUpGroup.class)
    @Pattern(regexp = "^[a-zA-Z]+$", message = "半角英字で入力してください", groups = SignUpGroup.class )
    private String password;

    @Transient // DBにマッピングしない
    private String passwordConfirm;

    @NotBlank(message = "氏名を入力してください", groups = {SignUpGroup.class, EditGroup.class})
    @Size(max = 10, message = "氏名は10文字以内で入力してください", groups = {SignUpGroup.class, EditGroup.class})
    @Pattern(regexp = "^[^　]*$", message = "氏名を入力してください", groups = {SignUpGroup.class, EditGroup.class})
    private String name;

    private Integer postId;

    private int isStopped;

    private Date createdDate;
    private Date updatedDate;


    private Post post;

    public enum isStopped {
        有効, 停止中
    }

}
