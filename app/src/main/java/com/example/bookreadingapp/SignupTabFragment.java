package com.example.bookreadingapp;

import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.bookreadingapp.room.DBOpenHelper;
import com.example.bookreadingapp.room.User;

import java.util.List;

public class SignupTabFragment extends Fragment {

    EditText email;
    EditText username;
    EditText password;
    EditText confirm;
    Button signup;
    private Context mContext;
    float v = 0;
    String mail, name, check, pass;

    DBOpenHelper dbOpenHelper;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        this.mContext = getActivity();
        dbOpenHelper = new DBOpenHelper(mContext);
    }


    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        ViewGroup root = (ViewGroup) inflater.inflate(R.layout.signup_fragment, container, false);


        username = root.findViewById(R.id.username);
        password = root.findViewById(R.id.pass);
        email = root.findViewById(R.id.email);
        confirm = root.findViewById(R.id.confirm);
        signup = root.findViewById(R.id.signup);

        username.setTranslationY(800);
        password.setTranslationY(800);
        email.setTranslationY(800);
        username.setTranslationY(800);

        username.setAlpha(v);
        password.setAlpha(v);
        email.setAlpha(v);
        username.setAlpha(v);

        email.animate().translationY(0).alpha(1).setDuration(1000).setStartDelay(300).start();
        password.animate().translationY(0).alpha(1).setDuration(1000).setStartDelay(500).start();
        confirm.animate().translationY(0).alpha(1).setDuration(1000).setStartDelay(500).start();
        username.animate().translationY(0).alpha(1).setDuration(1000).setStartDelay(700).start();

        signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mail = email.getText().toString();
                pass = password.getText().toString();
                check = confirm.getText().toString();
                name = username.getText().toString();

                System.out.println("第二次打印： check: " + check + " pass: " + pass);
                if(check.equals(pass) && !TextUtils.isEmpty(mail) && !TextUtils.isEmpty(pass) && !TextUtils.isEmpty(check) && !TextUtils.isEmpty(name)){
                    List<User> list = dbOpenHelper.getAllData();
                    boolean flag = true;
//                    for (User user1 : list){
//                        Log.e("user 打印", user1.toString());
//                        if(user1.getUsername().equals(user.getUsername()))
//                        {
//                            flag = false;
//                            break;
//                        }
//                    }
                    if (flag){
                        dbOpenHelper.add(mail, name, pass);
                        Toast.makeText(mContext, "用户注册成功，请先登录", Toast.LENGTH_SHORT).show();
                    }else
                    {
                        Toast.makeText(mContext, "用户注册失败，请修改用户名后登陆", Toast.LENGTH_SHORT).show();
                    }

                }else if(TextUtils.isEmpty(mail) || TextUtils.isEmpty(pass) || TextUtils.isEmpty(check) || TextUtils.isEmpty(name)) {
                    Toast.makeText(mContext, "请将用户信息填完后注册！", Toast.LENGTH_SHORT).show();
                } else{
                    Toast.makeText(mContext, "请确保两次密码输入一致", Toast.LENGTH_SHORT).show();
                }
            }
        });


        return root;
    }
}
