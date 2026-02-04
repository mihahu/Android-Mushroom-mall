package com.example.mushroommall.utils;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import com.example.mushroommall.bean.CartItem;
import com.example.mushroommall.bean.Product;

public class JdbcUtils {
    // 🔴 重点：模拟器必须用 10.0.2.2，真机必须用电脑的局域网 IP (如 192.168.1.5)
    private static final String URL = "jdbc:mysql://10.0.2.2:3306/mushroom_mall?useSSL=false&characterEncoding=utf8";
    private static final String USER = "root";       // 你的 MySQL 账号
    private static final String PWD = "123456"; // 你的 MySQL 密码

    // 获取连接
    public static Connection getConnection() throws Exception {
        Class.forName("com.mysql.jdbc.Driver");
        return DriverManager.getConnection(URL, USER, PWD);
    }

    // 直接查询所有商品
    public static List<Product> getAllProductsFromMySQL() {
        List<Product> list = new ArrayList<>();
        Connection conn = null;
        Statement stmt = null;
        ResultSet rs = null;
        try {
            conn = getConnection();
            stmt = conn.createStatement();
            // 执行 SQL
            rs = stmt.executeQuery("SELECT * FROM product");
            while (rs.next()) {
                int id = rs.getInt("id");
                String name = rs.getString("name");
                double price = rs.getDouble("price");
                String desc = rs.getString("description");
                String imgUrl = rs.getString("image_url");

                list.add(new Product(id, name, price, desc, imgUrl));
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            // 关闭资源 (偷懒写法，实际要try-catch关闭)
            try { if(rs!=null) rs.close(); if(stmt!=null) stmt.close(); if(conn!=null) conn.close(); } catch(Exception e){}
        }
        return list;
    }

    // 获取购物车列表 (连表查询)
    public static List<CartItem> getCartItemsFromMySQL() {
        List<CartItem> list = new ArrayList<>();
        Connection conn = null;
        try {
            conn = getConnection();
            String sql = "SELECT c.id, c.count, p.id, p.name, p.price, p.description, p.image_url " +
                    "FROM cart c JOIN product p ON c.product_id = p.id";
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(sql);
            while (rs.next()) {
                int cartId = rs.getInt(1);
                int count = rs.getInt(2);
                // 构建 Product 对象
                Product p = new Product(rs.getInt(3), rs.getString(4), rs.getDouble(5), rs.getString(6), rs.getString(7));

                list.add(new CartItem(cartId, p, count));
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try { if(conn!=null) conn.close(); } catch(Exception e){}
        }
        return list;
    }

    // 加入购物车 (核心逻辑：有则加1，无则插入)
    public static boolean addToCartMySQL(int productId) {
        Connection conn = null;
        try {
            conn = getConnection();
            // 先查询是否存在
            String checkSql = "SELECT id, count FROM cart WHERE product_id = " + productId;
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(checkSql);

            if (rs.next()) {
                // 存在 -> 数量 + 1
                int id = rs.getInt(1);
                int count = rs.getInt(2) + 1;
                stmt.executeUpdate("UPDATE cart SET count = " + count + " WHERE id = " + id);
            } else {
                // 不存在 -> 插入新记录
                stmt.executeUpdate("INSERT INTO cart (product_id, count) VALUES (" + productId + ", 1)");
            }
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        } finally {
            try { if(conn!=null) conn.close(); } catch(Exception e){}
        }
    }

    //  更新数量 (+/-)
    public static void updateCartCount(int cartId, int newCount) {
        Connection conn = null;
        try {
            conn = getConnection();
            if (newCount <= 0) {
                // 如果数量为0，直接删除
                conn.createStatement().executeUpdate("DELETE FROM cart WHERE id = " + cartId);
            } else {
                conn.createStatement().executeUpdate("UPDATE cart SET count = " + newCount + " WHERE id = " + cartId);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try { if(conn!=null) conn.close(); } catch(Exception e){}
        }
    }

    // 用户登录
    public static boolean login(String username, String password) {
        Connection conn = null;
        try {
            conn = getConnection();
            // 查询用户名和密码是否匹配
            String sql = "SELECT * FROM users WHERE username = '" + username + "' AND password = '" + password + "'";
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(sql);
            // 如果查到了结果 (rs.next() 为 true)，说明登录成功
            return rs.next();
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        } finally {
            try { if(conn!=null) conn.close(); } catch(Exception e){}
        }
    }

    // 用户注册
    public static boolean register(String username, String password) {
        Connection conn = null;
        try {
            conn = getConnection();
            Statement stmt = conn.createStatement();

            // 先检查用户名是否存在
            String checkSql = "SELECT * FROM users WHERE username = '" + username + "'";
            ResultSet rs = stmt.executeQuery(checkSql);
            if (rs.next()) {
                return false; // 用户名已存在，注册失败
            }

            // 插入新用户
            String sql = "INSERT INTO users (username, password) VALUES ('" + username + "', '" + password + "')";
            int rows = stmt.executeUpdate(sql);
            return rows > 0; // 受影响行数大于0，说明注册成功
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        } finally {
            try { if(conn!=null) conn.close(); } catch(Exception e){}
        }
    }

    // 获取个人信息 (返回avatar, address)
    public static java.util.Map<String, String> getUserInfo(String username) {
        java.util.Map<String, String> map = new java.util.HashMap<>();
        Connection conn = null;
        try {
            conn = getConnection();
            String sql = "SELECT avatar, address FROM users WHERE username = '" + username + "'";
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(sql);
            if (rs.next()) {
                map.put("avatar", rs.getString("avatar"));
                map.put("address", rs.getString("address"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try { if(conn!=null) conn.close(); } catch(Exception e){}
        }
        return map;
    }

    //更新个人地址
    public static boolean updateAddress(String username, String newAddress) {
        Connection conn = null;
        try {
            conn = getConnection();
            // 使用 PreparedStatement 防止 SQL 注入（特别是地址里可能有单引号）
            String sql = "UPDATE users SET address = ? WHERE username = ?";
            java.sql.PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, newAddress);
            pstmt.setString(2, username);
            int rows = pstmt.executeUpdate();
            return rows > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        } finally {
            try { if(conn!=null) conn.close(); } catch(Exception e){}
        }
    }
}