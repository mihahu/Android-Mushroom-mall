package com.example.mushroommall.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.mushroommall.R;
import com.example.mushroommall.activity.DetailActivity;
import com.example.mushroommall.adapter.ProductAdapter;
import com.example.mushroommall.bean.Product;
import com.example.mushroommall.utils.JdbcUtils; // 导入刚才写的工具类
import java.util.List;

public class HomeFragment extends Fragment {

    private RecyclerView recyclerView;
    //定义一个成员变量缓存数据
    private List<Product> mData = null;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        recyclerView = view.findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 2));

        if (mData != null && !mData.isEmpty()) {
            showList(mData);
        } else {
            // 没有数据才去联网
            loadDataFromMySQL();
        }

        return view;
    }

    private void loadDataFromMySQL() {
        // 提示用户正在加载
        //Toast.makeText(getContext(), "", Toast.LENGTH_SHORT).show();

        // 🔴 必须开启新线程！主线程连数据库会直接闪退 (NetworkOnMainThread)
        new Thread(() -> {
            // 1. 去 MySQL 查数据
            List<Product> list = JdbcUtils.getAllProductsFromMySQL();

            // 2. 拿到数据后，切换回主线程更新 UI
            new Handler(Looper.getMainLooper()).post(() -> {
                if (list != null && !list.isEmpty()) {
                    ProductAdapter adapter = new ProductAdapter(getContext(), list);
                    adapter.setOnItemClickListener(product -> {
                        Intent intent = new Intent(getContext(), DetailActivity.class);
                        intent.putExtra("product", product);
                        startActivity(intent);
                    });
                    recyclerView.setAdapter(adapter);
                    //Toast.makeText(getContext(), "", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getContext(), "数据库连接失败或无数据", Toast.LENGTH_LONG).show();
                }
            });

        }).start();

        new Thread(() -> {
            List<Product> list = JdbcUtils.getAllProductsFromMySQL();
            new Handler(Looper.getMainLooper()).post(() -> {
                if (list != null && !list.isEmpty()) {
                    // 拿到数据先缓存起来
                    mData = list;
                    showList(mData);
                    // 首次加载成功才提示，避免每次显示连接
                    //Toast.makeText(getContext(), "加载成功", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getContext(), "连接失败", Toast.LENGTH_SHORT).show();
                }
            });
        }).start();
    }

    // 抽取一个显示列表的方法
    private void showList(List<Product> list) {
        ProductAdapter adapter = new ProductAdapter(getContext(), list);
        adapter.setOnItemClickListener(product -> {
            Intent intent = new Intent(getContext(), DetailActivity.class);
            intent.putExtra("product", product);
            startActivity(intent);
        });
        recyclerView.setAdapter(adapter);
    }
}