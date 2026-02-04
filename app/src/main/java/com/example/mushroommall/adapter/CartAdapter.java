package com.example.mushroommall.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.example.mushroommall.R;
import com.example.mushroommall.bean.CartItem;
import java.util.List;

public class CartAdapter extends RecyclerView.Adapter<CartAdapter.ViewHolder> {

    private Context context;
    private List<CartItem> mList;
    private OnCartChangeListener listener;

    // 定义接口：通知外部 勾选变化、数量增加、数量减少
    public interface OnCartChangeListener {
        void onCheckChanged(); // 勾选变化
        void onCountChanged(int cartId, int newCount); // 数量变化
    }

    public CartAdapter(Context context, List<CartItem> list, OnCartChangeListener listener) {
        this.context = context;
        this.mList = list;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_cart, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        CartItem item = mList.get(position);

        // 1. 先移除监听器（防止设置 setChecked 时触发回调）
        holder.checkBox.setOnCheckedChangeListener(null);

        // 2. 设置数据
        holder.tvName.setText(item.getProduct().getName());
        holder.tvPrice.setText("¥ " + item.getProduct().getPrice());
        holder.tvCount.setText(String.valueOf(item.getCount()));
        holder.checkBox.setChecked(item.isChecked()); // 这里设置选中状态
        Glide.with(context).load(item.getProduct().getImageUrl()).into(holder.img);

        // 3. 重新设置监听器
        holder.checkBox.setOnClickListener(v -> {
            item.setChecked(holder.checkBox.isChecked());
            listener.onCheckChanged();
        });

        // 减号点击
        holder.btnMinus.setOnClickListener(v -> {
            int newCount = item.getCount() - 1;
            listener.onCountChanged(item.getId(), newCount);
        });

        // 加号点击
        holder.btnPlus.setOnClickListener(v -> {
            int newCount = item.getCount() + 1;
            listener.onCountChanged(item.getId(), newCount);
        });
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        CheckBox checkBox;
        ImageView img;
        TextView tvName, tvPrice, tvCount;
        Button btnMinus, btnPlus; // 新增的按钮

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            checkBox = itemView.findViewById(R.id.cb_item);
            img = itemView.findViewById(R.id.img_item);
            tvName = itemView.findViewById(R.id.tv_name);
            tvPrice = itemView.findViewById(R.id.tv_price);
            tvCount = itemView.findViewById(R.id.tv_count);
            btnMinus = itemView.findViewById(R.id.btn_minus);
            btnPlus = itemView.findViewById(R.id.btn_plus);
        }
    }
}