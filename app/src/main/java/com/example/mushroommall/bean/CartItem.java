package com.example.mushroommall.bean;

public class CartItem {
    private int id;
    private Product product;
    private int count;
    private boolean isChecked; // 用于购物车勾选状态

    public CartItem(int id, Product product, int count) {
        this.id = id;
        this.product = product;
        this.count = count;
        this.isChecked = false;
    }

    // Getters and Setters
    public int getId() { return id; }
    public Product getProduct() { return product; }
    public int getCount() { return count; }
    public void setCount(int count) { this.count = count; }
    public boolean isChecked() { return isChecked; }
    public void setChecked(boolean checked) { isChecked = checked; }
}