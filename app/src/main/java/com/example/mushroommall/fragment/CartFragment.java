package com.example.mushroommall.fragment;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.mushroommall.R;
import com.example.mushroommall.adapter.CartAdapter;
import com.example.mushroommall.bean.CartItem;
import com.example.mushroommall.utils.JdbcUtils;
import java.util.ArrayList;
import java.util.List;

public class CartFragment extends Fragment {

    private RecyclerView recyclerView;
    private CheckBox cbSelectAll;
    private TextView tvTotalPrice;
    private CartAdapter adapter;
    private List<CartItem> cartList = new ArrayList<>();
    private Handler handler = new Handler(Looper.getMainLooper());

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_cart, container, false);

        recyclerView = view.findViewById(R.id.recycler_cart);
        cbSelectAll = view.findViewById(R.id.cb_select_all);
        tvTotalPrice = view.findViewById(R.id.tv_total_price);
        Button btnCheckout = view.findViewById(R.id.btn_checkout);

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        // 全选逻辑
        cbSelectAll.setOnClickListener(v -> {
            boolean isChecked = cbSelectAll.isChecked();
            for (CartItem item : cartList) {
                item.setChecked(isChecked);
            }
            adapter.notifyDataSetChanged();
            calculateTotal();
        });

        // 结算
        btnCheckout.setOnClickListener(v -> Toast.makeText(getContext(), "功能开发中...", Toast.LENGTH_SHORT).show());

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        loadDataFromMySQL(); // 每次进入页面刷新数据
    }

    private void loadDataFromMySQL() {
        new Thread(() -> {
            // 1. 从 MySQL 获取数据
            List<CartItem> list = JdbcUtils.getCartItemsFromMySQL();

            handler.post(() -> {
                cartList = list;
                adapter = new CartAdapter(getContext(), cartList, new CartAdapter.OnCartChangeListener() {
                    @Override
                    public void onCheckChanged() {
                        calculateTotal(); // 仅重算价格，不查库
                    }

                    @Override
                    public void onCountChanged(int cartId, int newCount) {
                        // 2. 加减数量操作 -> 子线程更新数据库
                        updateCountInDB(cartId, newCount);
                    }
                });
                recyclerView.setAdapter(adapter);
                calculateTotal();
            });
        }).start();
    }

    private void updateCountInDB(int cartId, int newCount) {
        // 1. 先在本地内存中更新数据（这样界面反应快，且勾选状态不会丢）
        for (int i = 0; i < cartList.size(); i++) {
            CartItem item = cartList.get(i);
            if (item.getId() == cartId) {
                if (newCount <= 0) {
                    cartList.remove(i); // 如果是0，从本地列表移除
                    adapter.notifyItemRemoved(i);
                    adapter.notifyItemRangeChanged(i, cartList.size()); // 刷新后续位置
                } else {
                    item.setCount(newCount); // 更新本地数量
                    adapter.notifyItemChanged(i); // 局部刷新，不影响其他行的勾选
                }
                break;
            }
        }

        // 重新计算总价
        calculateTotal();

        // 在后台静默发送请求给 MySQL 同步数据 (不刷新列表)
        new Thread(() -> {
            JdbcUtils.updateCartCount(cartId, newCount);
        }).start();
    }

    private void calculateTotal() {
        double total = 0;
        boolean allChecked = true;
        if (cartList.isEmpty()) allChecked = false;

        for (CartItem item : cartList) {
            if (item.isChecked()) {
                total += item.getProduct().getPrice() * item.getCount();
            } else {
                allChecked = false;
            }
        }
        tvTotalPrice.setText("总计: ¥ " + String.format("%.2f", total));
        // 避免触发回调循环
        cbSelectAll.setOnCheckedChangeListener(null);
        cbSelectAll.setChecked(allChecked);
        cbSelectAll.setOnClickListener(v -> { /* 重新绑定监听 */
            boolean isChecked = cbSelectAll.isChecked();
            for (CartItem item : cartList) item.setChecked(isChecked);
            adapter.notifyDataSetChanged();
            calculateTotal();
        });
    }
}