package com.example.bookreadingapp;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.bookreadingapp.room.DBOpenHelper;
import com.example.bookreadingapp.room.User;

import java.util.List;

public class LoginTabFragment extends Fragment {

    EditText email, pass;
    TextView forgetPass;
    Button login;
    float v = 0;

    String mail, password;

    private DBOpenHelper dbOpenHelper;

    private Context mContext;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        this.mContext = getActivity();
        dbOpenHelper = new DBOpenHelper(mContext);
        Log.e("login", getActivity().toString());
    }

    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        ViewGroup root = (ViewGroup) inflater.inflate(R.layout.login_tab_fragment, container, false);

        pass = root.findViewById(R.id.pass);
        forgetPass = root.findViewById(R.id.forget);
        email = root.findViewById(R.id.email);
        login = root.findViewById(R.id.login);

        pass.setTranslationY(800);
        forgetPass.setTranslationY(800);
        email.setTranslationY(800);
        login.setTranslationY(800);

        pass.setAlpha(v);
        forgetPass.setAlpha(v);
        email.setAlpha(v);
        login.setAlpha(v);

        email.animate().translationY(0).alpha(1).setDuration(1000).setStartDelay(300).start();
        forgetPass.animate().translationY(0).alpha(1).setDuration(1000).setStartDelay(500).start();
        login.animate().translationY(0).alpha(1).setDuration(1000).setStartDelay(500).start();
        pass.animate().translationY(0).alpha(1).setDuration(1000).setStartDelay(700).start();

        mail = email.getText().toString();
        password = pass.getText().toString();

        User user = new User(mail, password, null);
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mail = email.getText().toString();
                password = pass.getText().toString();
                Log.e("loginTabFragment", mail);
                Log.e("loginTabFragment", password);

                if(!TextUtils.isEmpty(mail) && !TextUtils.isEmpty(password)){
                    List<User> list = dbOpenHelper.getAllData();
                    boolean flag = false;
                    for(User user1 : list){
                        Log.e("循环中打印", user1.toString());
                        Log.e("email", String.valueOf(user1.getEmail().equals(mail)));
                        Log.e("pass", String.valueOf(user1.getPassword().equals(pass)));
                        Log.e("user1 + ", user1.getPassword());
                        Log.e("pass + ", password);
                        if(user1.getEmail().equals(mail) && user1.getPassword().equals(password)){
                            // 跳转页面
                            Intent intent = new Intent(mContext, SplashActivity.class);
                            startActivity(intent);
                        }else{
                            Toast.makeText(mContext, "用户密码错误，请重新输入密码", Toast.LENGTH_SHORT).show();
                        }
                    }
                }else{
                    Toast.makeText(mContext, "请输入完整信息后登陆", Toast.LENGTH_SHORT).show();
                }
            }
        });


        return root;
    }
}
