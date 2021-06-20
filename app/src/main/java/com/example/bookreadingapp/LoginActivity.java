package com.example.bookreadingapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.tabs.TabLayout;

public class LoginActivity extends AppCompatActivity {

    TabLayout tabLayout;
    ViewPager viewPager;
    FloatingActionButton qq, wechat, phone;
    float v = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        tabLayout = findViewById(R.id.tab_layout);
        viewPager = findViewById(R.id.view_pager);
        qq = findViewById(R.id.qq);
        wechat = findViewById(R.id.chat);
        phone = findViewById(R.id.phone);

        tabLayout.addTab(tabLayout.newTab().setText("Login"));
        tabLayout.addTab(tabLayout.newTab().setText("Signup"));
        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);

        final LoginAdapter adapter = new LoginAdapter(getSupportFragmentManager(), this, tabLayout.getTabCount());
        viewPager.setAdapter(adapter);
        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));

        qq.setTranslationY(300);
        wechat.setTranslationY(300);
        phone.setTranslationY(300);
        tabLayout.setTranslationY(300);

        qq.setAlpha(v);
        wechat.setAlpha(v);
        phone.setAlpha(v);
        tabLayout.setAlpha(v);

        qq.animate().translationY(0).alpha(1).setDuration(1000).setStartDelay(400).start();
        wechat.animate().translationY(0).alpha(1).setDuration(1000).setStartDelay(600).start();
        phone.animate().translationY(0).alpha(1).setDuration(1000).setStartDelay(800).start();
        tabLayout.animate().translationY(0).alpha(1).setDuration(1000).setStartDelay(100).start();


    }


    public static void start(Context context){
        Intent intent =new Intent(context,BookActivity.class);
        context.startActivity(intent);
    }

    public void sign(View view) {


    }
}