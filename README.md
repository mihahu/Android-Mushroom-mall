# Mushroommall 蘑菇商城

## 项目简介
Mushroommall是一个基于Android Studio开发的电商应用，提供商品浏览、购物车管理和用户个人中心等功能。

## 功能特性

### 核心功能
- **首页**：展示商品列表，用户可以浏览商品
- **购物车**：管理用户选购的商品
- **个人中心**：用户个人信息管理
- **商品详情**：查看商品的详细信息
- **用户登录**：用户身份验证

### 技术特性
- **底部导航栏**：使用BottomNavigationView实现
- **Fragment切换**：实现不同模块间的无缝切换
- **数据库连接**：集成MySQL数据库连接功能
- **响应式设计**：适配不同屏幕尺寸

## 技术栈

- **开发工具**：Android Studio
- **编程语言**：Java
- **UI组件**：AndroidX, Material Design
- **数据库**：MySQL (通过JDBC连接)
- **网络权限**：需要INTERNET权限

## 项目结构

```
Mushroommall/
├── app/
│   ├── src/
│   │   ├── main/
│   │   │   ├── java/com/example/mushroommall/
│   │   │   │   ├── activity/
│   │   │   │   │   ├── DetailActivity.java     # 商品详情页
│   │   │   │   │   └── LoginActivity.java      # 登录页
│   │   │   │   ├── adapter/
│   │   │   │   │   ├── CartAdapter.java        # 购物车适配器
│   │   │   │   │   └── ProductAdapter.java     # 商品适配器
│   │   │   │   ├── bean/
│   │   │   │   │   ├── CartItem.java           # 购物车项实体类
│   │   │   │   │   └── Product.java            # 商品实体类
│   │   │   │   ├── fragment/
│   │   │   │   │   ├── CartFragment.java       # 购物车Fragment
│   │   │   │   │   ├── HomeFragment.java        # 首页Fragment
│   │   │   │   │   └── MineFragment.java        # 个人中心Fragment
│   │   │   │   ├── utils/
│   │   │   │   │   └── JdbcUtils.java           # 数据库连接工具
│   │   │   │   └── MainActivity.java            # 主活动，包含底部导航
│   │   │   ├── res/
│   │   │   │   ├── layout/                      # 布局文件
│   │   │   │   ├── menu/                        # 菜单文件
│   │   │   │   ├── mipmap-*/                    # 图标资源
│   │   │   │   └── values/                      # 字符串、颜色等资源
│   │   │   └── AndroidManifest.xml              # 应用配置文件
│   └── build.gradle                             # 应用级构建配置
└── build.gradle                                 # 项目级构建配置
```

## 安装和运行

### 前提条件
- Android Studio 4.0+
- JDK 8+
- Android SDK (API Level 21+)
- MySQL数据库 (可选，用于测试数据库连接功能)

### 安装步骤
1. **克隆项目**
   ```bash
   git clone https://github.com/mihahu/Mushroommall.git
   ```

2. **打开项目**
   - 在Android Studio中选择"Open an existing project"
   - 导航到克隆的项目目录并选择

3. **同步依赖**
   - Android Studio会自动提示同步Gradle依赖
   - 点击"Sync Now"完成同步

4. **配置数据库连接** (可选)
   - 打开 `app/src/main/java/com/example/mushroommall/utils/JdbcUtils.java`
   - 修改数据库连接参数为你的MySQL数据库配置

5. **运行应用**
   - 连接Android设备或启动模拟器
   - 点击"Run"按钮或按Shift+F10运行应用

## 注意事项

### 数据库连接
- 应用包含MySQL数据库连接功能，但默认配置可能需要修改
- 数据库连接操作在子线程中执行，避免阻塞主线程
- 确保你的MySQL数据库服务正在运行，并且网络连接正常
- 注意：在实际生产环境中，建议使用Web API作为中间层，而不是直接连接数据库

### 权限
- 应用需要INTERNET权限来连接网络和数据库
- 在Android 9.0+设备上，需要在`AndroidManifest.xml`中设置`android:usesCleartextTraffic="true"`以允许明文HTTP流量

## 许可证

本项目采用MIT许可证。详情请参阅LICENSE文件。

## 贡献

欢迎提交问题和拉取请求来帮助改进这个项目！

## 联系方式

- 项目链接：https://github.com/mihahu/Mushroommall

---


感谢使用Mushroommall蘑菇商城应用！

