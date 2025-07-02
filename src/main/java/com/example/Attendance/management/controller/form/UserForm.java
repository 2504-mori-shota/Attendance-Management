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
 //   public interface LoginGroup {}
//    public interface EditGroup{}
    private int id;

    @NotBlank(message = "アカウントを入力してください"/*, groups = {LoginGroup.class, SingUpGroup.class, EditGroup.class}*/)
    @Pattern(regexp = "^[^　]*$", message = "アカウントを入力してください"/*, groups = {LoginGroup.class, SingUpGroup.class, EditGroup.class}*/)
    @Pattern(regexp = "^[a-zA-Z0-9]+$", message = "アカウントは半角英数字かつ6文字以上20文字以内で入力してください"/*, groups = {SingUpGroup.class, EditGroup.class}*/)
    @Size(min = 6, max = 20, message = "アカウントは6文字以上20文字以内で入力してください"/*, groups = {SingUpGroup.class, EditGroup.class}*/)
    private String account;

    @NotBlank(message = "パスワードを入力してください"/*, groups = {LoginGroup.class, SingUpGroup.class}*/)
    @Pattern(regexp = "^[^　]*$", message = "パスワードを入力してください"/*,groups = {LoginGroup.class, SingUpGroup.class}*/)
    @Size(min = 6, max = 20, message = "パスワードは6文字以上20文字以内で入力してください"/*, groups = SingUpGroup.class*/)
    @Pattern(regexp = "^[a-zA-Z]+$", message = "アカウントは半角かつ6文字以上20文字以内で入力してください"/*, groups = SingUpGroup.class*/ )
    private String password;

    @Transient // DBにマッピングしない
    @NotBlank(message = "入力したパスワードと確認用パスワードが一致しません"/*, groups = SingUpGroup.class*/)
    private String passwordConfirm;

    @NotBlank(message = "氏名を入力してください"/*, groups = {SingUpGroup.class, EditGroup.class}*/)
    @Size(max = 10, message = "氏名は10文字以内で入力してください"/*, groups = {SingUpGroup.class, EditGroup.class}*/)
    private String name;

    private Integer postId;

    private int isStopped;

    private Date createdDate;
    private Date updatedDate;


    private Post post;

}
