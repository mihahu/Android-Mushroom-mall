package com.example.mushroommall.fragment;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import com.bumptech.glide.Glide;
import com.example.mushroommall.R;
import com.example.mushroommall.activity.LoginActivity;
import com.example.mushroommall.utils.JdbcUtils;
import java.util.Map;

public class MineFragment extends Fragment {

    private ImageView imgAvatar;
    private TextView tvUsername;
    private EditText etAddress;
    private Button btnSave;
    private ImageView ivMenu; // 操作图标
    private String currentUsername;
    private Handler handler = new Handler(Looper.getMainLooper());

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_mine, container, false);

        imgAvatar = view.findViewById(R.id.img_avatar);
        tvUsername = view.findViewById(R.id.tv_username);
        etAddress = view.findViewById(R.id.et_address);
        btnSave = view.findViewById(R.id.btn_save_address);
        ivMenu = view.findViewById(R.id.iv_address_menu); // 新增
        Button btnLogout = view.findViewById(R.id.tv_logout);

        // 获取当前用户
        SharedPreferences sp = getContext().getSharedPreferences("user_info", Context.MODE_PRIVATE);
        currentUsername = sp.getString("username", "");

        tvUsername.setText(currentUsername.isEmpty() ? "未登录" : currentUsername);

        // 加载数据
        loadUserInfo();

        // 1. 操作图标点击事件 -> 弹出菜单
        ivMenu.setOnClickListener(v -> showAddressMenu(v));

        // 2. 保存按钮点击事件
        btnSave.setOnClickListener(v -> {
            String newAddress = etAddress.getText().toString().trim();
            if (newAddress.isEmpty()) {
                Toast.makeText(getContext(), "地址不能为空", Toast.LENGTH_SHORT).show();
                return;
            }
            saveAddress(newAddress);
        });

        // 退出登录
        btnLogout.setOnClickListener(v -> {
            startActivity(new Intent(getContext(), LoginActivity.class));
            getActivity().finish();
        });

        return view;
    }

    // 显示弹出菜单 (编辑/删除)
    private void showAddressMenu(View view) {
        PopupMenu popupMenu = new PopupMenu(getContext(), view);
        popupMenu.getMenu().add("编辑地址");
        popupMenu.getMenu().add("删除地址");

        popupMenu.setOnMenuItemClickListener(item -> {
            if ("编辑地址".equals(item.getTitle())) {
                // 开启编辑模式
                enableEditMode(true);
            } else if ("删除地址".equals(item.getTitle())) {
                // 确认删除
                new AlertDialog.Builder(getContext())
                        .setTitle("提示")
                        .setMessage("确定要清空收货地址吗？")
                        .setPositiveButton("确定", (dialog, which) -> saveAddress("")) // 存空字符串
                        .setNegativeButton("取消", null)
                        .show();
            }
            return true;
        });
        popupMenu.show();
    }

    // 切换编辑模式 (true=可编辑, false=只读)
    private void enableEditMode(boolean enable) {
        etAddress.setEnabled(enable);
        if (enable) {
            etAddress.setBackgroundResource(android.R.drawable.edit_text); // 恢复下划线背景提示用户可输入
            etAddress.requestFocus(); // 获取焦点
            // 把光标移到末尾
            etAddress.setSelection(etAddress.getText().length());
            btnSave.setVisibility(View.VISIBLE); // 显示保存按钮
        } else {
            etAddress.setBackground(null); // 去掉背景
            btnSave.setVisibility(View.GONE); // 隐藏保存按钮
        }
    }

    private void loadUserInfo() {
        if (currentUsername.isEmpty()) return;

        new Thread(() -> {
            Map<String, String> userInfo = JdbcUtils.getUserInfo(currentUsername);
            handler.post(() -> {
                if (userInfo != null && !userInfo.isEmpty()) {
                    String avatarUrl = userInfo.get("avatar");
                    String address = userInfo.get("address");

                    // 显示地址
                    if (address != null && !address.equals("null") && !address.isEmpty()) {
                        etAddress.setText(address);
                    } else {
                        etAddress.setText("");
                        etAddress.setHint("暂无地址，请点击右上角编辑");
                    }

                    // 显示头像
                    Glide.with(getContext())
                            .load(avatarUrl)
                            .placeholder(R.mipmap.ic_launcher)
                            .error(R.mipmap.ic_launcher)
                            .into(imgAvatar);
                }
            });
        }).start();
    }

    private void saveAddress(String newAddr) {
        new Thread(() -> {
            boolean success = JdbcUtils.updateAddress(currentUsername, newAddr);
            handler.post(() -> {
                if (success) {
                    if (newAddr.isEmpty()) {
                        etAddress.setText("");
                        etAddress.setHint("暂无地址，请点击右上角编辑");
                        Toast.makeText(getContext(), "地址已删除", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(getContext(), "保存成功", Toast.LENGTH_SHORT).show();
                    }
                    // 保存成功后，关闭编辑模式
                    enableEditMode(false);
                } else {
                    Toast.makeText(getContext(), "操作失败，请检查网络", Toast.LENGTH_SHORT).show();
                }
            });
        }).start();
    }
}