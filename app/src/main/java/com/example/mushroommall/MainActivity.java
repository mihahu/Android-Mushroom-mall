package com.example.mushroommall;

import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import com.example.mushroommall.fragment.CartFragment;
import com.example.mushroommall.fragment.HomeFragment;
import com.example.mushroommall.fragment.MineFragment;
import com.example.mushroommall.utils.JdbcUtils;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.sql.Connection;
import java.sql.SQLException;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        BottomNavigationView nav = findViewById(R.id.bottom_nav);

        // 默认显示首页
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new HomeFragment()).commit();

        nav.setOnItemSelectedListener(item -> {
            Fragment selectedFragment = null;
            if (item.getItemId() == R.id.nav_home) {
                selectedFragment = new HomeFragment();
            } else if (item.getItemId() == R.id.nav_cart) {
                selectedFragment = new CartFragment();
            } else if (item.getItemId() == R.id.nav_mine) {
                selectedFragment = new MineFragment();
            }

            if (selectedFragment != null) {
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, selectedFragment).commit();
            }
            return true;
        });
    }

    // 在 MainActivity 或其他地方调用
    // 在 MainActivity 中
    public void testMysqlConnection() {
        new Thread(() -> {
            try {
                // ✅ 必须放在 try 里面
                Connection conn = JdbcUtils.getConnection();

                if (conn != null) {
                    // 连接成功的逻辑
                    System.out.println("数据库连接成功");
                    conn.close(); // 记得关闭
                }
            } catch (Exception e) {
                // ✅ 必须捕获异常，打印错误日志
                e.printStackTrace();
                System.out.println("数据库连接失败: " + e.getMessage());
            }
        }).start();
    }


}