package com.example.mushroommall.activity;

import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.bumptech.glide.Glide;
import com.example.mushroommall.R;
import com.example.mushroommall.bean.Product;
import com.example.mushroommall.utils.JdbcUtils;

public class DetailActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        // 1. 获取传递过来的商品对象
        Product product = (Product) getIntent().getSerializableExtra("product");

        // 2. 初始化控件
        ImageView img = findViewById(R.id.detail_img);
        TextView name = findViewById(R.id.detail_name);
        TextView price = findViewById(R.id.detail_price);
        TextView desc = findViewById(R.id.detail_desc);
        Button btnAddCart = findViewById(R.id.btn_add_cart);
        Button btnBack = findViewById(R.id.btn_back);

        // 3. 填充数据
        if (product != null) {
            Glide.with(this)
                    .load(product.getImageUrl())
                    .placeholder(R.mipmap.ic_launcher) // 加载中显示的图
                    .error(R.mipmap.ic_launcher)       // 错误时显示的图
                    .into(img);

            name.setText(product.getName());
            price.setText("¥ " + product.getPrice());
            desc.setText(product.getDescription());

            // 4. 设置“加入购物车”点击事件 (MySQL版)
            btnAddCart.setOnClickListener(v -> {
                // 开启子线程进行网络数据库操作
                new Thread(() -> {
                    // 调用 JdbcUtils 往 MySQL 插入数据
                    boolean success = JdbcUtils.addToCartMySQL(product.getId());

                    // 切回主线程更新 UI 提示
                    runOnUiThread(() -> {
                        if (success) {
                            Toast.makeText(DetailActivity.this, "已加入购物车 (MySQL)", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(DetailActivity.this, "加入失败，请检查网络或服务器", Toast.LENGTH_SHORT).show();
                        }
                    });
                }).start();
            });
        }

        // 5. 返回按钮
        btnBack.setOnClickListener(v -> finish());
    }
}