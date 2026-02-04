package com.example.mushroommall.activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.example.mushroommall.MainActivity;
import com.example.mushroommall.R;
import com.example.mushroommall.utils.JdbcUtils;

public class LoginActivity extends AppCompatActivity {

    private EditText etUsername, etPassword;
    private CheckBox cbRemember;
    private SharedPreferences sp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        etUsername = findViewById(R.id.et_username);
        etPassword = findViewById(R.id.et_password);
        cbRemember = findViewById(R.id.cb_remember);
        Button btnLogin = findViewById(R.id.btn_login);
        Button btnRegister = findViewById(R.id.btn_register);

        //初始化 SharedPreferences
        sp = getSharedPreferences("user_info", Context.MODE_PRIVATE);

        //检查是否需要回显用户名
        boolean isRemember = sp.getBoolean("is_remember", false);
        if (isRemember) {
            String savedName = sp.getString("username", "");
            etUsername.setText(savedName);
            cbRemember.setChecked(true);
        }

        // 点击登录
        btnLogin.setOnClickListener(v -> {
            String name = etUsername.getText().toString().trim();
            String pwd = etPassword.getText().toString().trim();

            if (name.isEmpty() || pwd.isEmpty()) {
                Toast.makeText(this, "请输入账号密码", Toast.LENGTH_SHORT).show();
                return;
            }

            // 开启子线程查库
            new Thread(() -> {
                boolean isSuccess = JdbcUtils.login(name, pwd);

                runOnUiThread(() -> {
                    if (isSuccess) {
                        // ✅ 2. 登录成功，处理“记住我”逻辑
                        saveLoginStatus(name);

                        Toast.makeText(this, "登录成功", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(LoginActivity.this, MainActivity.class));
                        finish();
                    } else {
                        Toast.makeText(this, "登录失败，密码错误或用户不存在", Toast.LENGTH_SHORT).show();
                    }
                });
            }).start();
        });

        // 点击注册
        btnRegister.setOnClickListener(v -> {
            String name = etUsername.getText().toString().trim();
            String pwd = etPassword.getText().toString().trim();

            if (name.isEmpty() || pwd.isEmpty()) {
                Toast.makeText(this, "请填写完整信息", Toast.LENGTH_SHORT).show();
                return;
            }

            new Thread(() -> {
                boolean isSuccess = JdbcUtils.register(name, pwd);

                runOnUiThread(() -> {
                    if (isSuccess) {
                        Toast.makeText(this, "注册成功，请直接登录", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(this, "注册失败，用户名可能已存在", Toast.LENGTH_SHORT).show();
                    }
                });
            }).start();
        });
    }

    //  辅助方法：保存状态
    private void saveLoginStatus(String username) {
        SharedPreferences.Editor editor = sp.edit();

        // 无论是否记住，都保存当前用户名供“我的”页面显示
        editor.putString("username", username);

        // 根据勾选框保存标记
        if (cbRemember.isChecked()) {
            editor.putBoolean("is_remember", true);
        } else {
            editor.putBoolean("is_remember", false);
            // 如果没勾选，下次进来不回显，但这里不删除 username，否则"我的"页面会空
        }
        editor.apply();
    }
}