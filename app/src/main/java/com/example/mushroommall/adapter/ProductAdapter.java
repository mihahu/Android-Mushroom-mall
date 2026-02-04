package com.example.mushroommall.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.example.mushroommall.R; // 如果爆红，请按Alt+Enter导入你自己的包名
import com.example.mushroommall.bean.Product;
import java.util.List;

public class ProductAdapter extends RecyclerView.Adapter<ProductAdapter.ViewHolder> {

    private Context context;
    private List<Product> mList;
    private OnItemClickListener listener;

    // 1. 定义点击事件接口，供 HomeFragment 调用
    public interface OnItemClickListener {
        void onItemClick(Product product);
    }

    // 设置监听器的方法
    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    public ProductAdapter(Context context, List<Product> list) {
        this.context = context;
        this.mList = list;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // 加载 item_product.xml 布局
        View view = LayoutInflater.from(context).inflate(R.layout.item_product, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Product product = mList.get(position);

        // 设置文本
        holder.tvName.setText(product.getName());
        holder.tvPrice.setText("¥ " + product.getPrice());

        // 使用 Glide 加载网络图片
        Glide.with(context)
                .load(product.getImageUrl())
                .placeholder(R.mipmap.ic_launcher) // 加载中显示的占位图
                .error(R.mipmap.ic_launcher)       // 加载失败显示的图
                .into(holder.imgProduct);

        // 处理点击事件 -> 跳转详情
        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onItemClick(product);
            }
        });
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    // ViewHolder 类，绑定 item_product.xml 中的控件
    static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imgProduct;
        TextView tvName, tvPrice;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imgProduct = itemView.findViewById(R.id.img_product);
            tvName = itemView.findViewById(R.id.tv_name);
            tvPrice = itemView.findViewById(R.id.tv_price);
        }
    }
}