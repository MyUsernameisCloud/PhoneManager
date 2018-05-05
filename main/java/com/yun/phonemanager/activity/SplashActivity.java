package com.yun.phonemanager.activity;

import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.yun.phonemanager.R;
import com.yun.phonemanager.util.IOUtil;
import com.yun.phonemanager.util.ServiceState;
import com.yun.touch.RocketService;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;

public class SplashActivity extends AppCompatActivity {
    // 用来处理的常量
    protected static final int CODE_UPDATE_DIALOG = 0;
    protected static final int CODE_URL_ERRO = 1;
    protected static final int CODE_Protocol_ERRO = 2;
    protected static final int CODE_IO_ERRO = 3;
    protected static final int CODE_JASONPARSE_ERRO = 4;
    protected static final int CODE_NEW_VERSION = 5;
    protected static final int CODE_NOT_UPDATE = 6;
    private DevicePolicyManager mDPM;
    private ComponentName myDeviceAdminSample;
    private long startT;
    private long endT;
    private String versionName;
    private int versionCode;
    private String description;
    private String downloadUrl;
    private RelativeLayout rl;
    private SharedPreferences spf;
    // handler处理相应的行为
    Handler handler = new Handler() {
        public void handleMessage(android.os.Message msg) {
            switch (msg.what) {
                // 显示对话框
                case CODE_UPDATE_DIALOG:
                  //  showDialog();
                    break;
                // 错误提示
                case CODE_URL_ERRO:
                    Toast.makeText(SplashActivity.this, "网络地址连接错误",  Toast.LENGTH_SHORT).show();
                    enterHome();
                    break;
                case CODE_Protocol_ERRO:
                    Toast.makeText(SplashActivity.this, "网络协议错误",  Toast.LENGTH_SHORT).show();
                    enterHome();
                    break;
                case CODE_IO_ERRO:
                    Toast.makeText(SplashActivity.this, "网络读取错误",  Toast.LENGTH_SHORT).show();
                    enterHome();
                    break;
                case CODE_JASONPARSE_ERRO:
                    Toast.makeText(SplashActivity.this, "json解析错误",  Toast.LENGTH_SHORT).show();
                    enterHome();
                    break;
                // 最新版本不用更新，进入主页
                case CODE_NEW_VERSION:
                    enterHome();
                    break;
                case CODE_NOT_UPDATE:
                    enterHome();
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        // 拿到设备管理器
        mDPM = (DevicePolicyManager) getSystemService(Context.DEVICE_POLICY_SERVICE);// 获取设备策略服务
        myDeviceAdminSample = new ComponentName(this,
                com.yun.phonemanager.receiver.AdminReceiver.class);// 设备管理组件
        // 激活设备管理器
        activeDevice();
        // splash设置渐变效果
        AlphaAnimation aa = new AlphaAnimation(0.2f, 1);
        rl = (RelativeLayout) findViewById(R.id.rl_splash);
        aa.setDuration(2000);
        rl.startAnimation(aa);

        //启动悬浮窗
        if (!ServiceState.serviceRunning(this, "com.yun.touch.RocketService")) {
            Intent intent = new Intent(this, RocketService.class);
            startService(intent);
        }
        // 拷贝火星地址转换数据库到files文件夹
       // copy("axisoffset.dat");
        // 拷贝电话归属地查询数据库到files文件夹
        copy("address.db");
        // 拷贝常用号码数据库到files文件夹
        copy("commonnum.db");
        // 拷贝病毒数据库到files文件夹
        copy("antivirus.db");
        // 动态设置splah的版本号
        TextView tv_version = (TextView) findViewById(R.id.tv_version);
        tv_version.setText("版本号:" + getVersionName());
        // 获得SharedPreferences然后进行相应处理
        spf = getSharedPreferences("config", Context.MODE_PRIVATE);
        // 创建快捷图标
        shortcut();
        Toast.makeText(SplashActivity.this, "暂时没有新版本", Toast.LENGTH_SHORT).show();
            Message msg = handler.obtainMessage();
            msg.what = CODE_NOT_UPDATE;
            handler.sendMessageDelayed(msg, 2000);
    }

    /**
     * 创建快捷图标
     */
    public void shortcut() {
        // 查看配置文件中是否已经设置快捷图标
        boolean install = spf.getBoolean("install", false);
        // 设置了就跳出
        if (install) {
            return;
        }
        // 设置安装快捷图标的意图
        Intent intent = new Intent(
                "com.android.launcher.action.INSTALL_SHORTCUT");
        // 设置名字
        intent.putExtra(Intent.EXTRA_SHORTCUT_NAME, "安卓手机管家");
        // 设置图标
        intent.putExtra(Intent.EXTRA_SHORTCUT_ICON,
                BitmapFactory.decodeResource(getResources(), R.drawable.icon));
        // 设置意图
        Intent it = new Intent("com.yun.home");
        intent.putExtra(Intent.EXTRA_SHORTCUT_INTENT, it);
        // 发送广播，创建快捷图标
        sendBroadcast(intent);
        // 创建后就讲配置文件设置印记
        spf.edit().putBoolean("install", true).commit();
    }

    /**
     * 激活设备管理器
     */
    public void activeDevice() {
        if (!mDPM.isAdminActive(myDeviceAdminSample)) {// 判断设备管理器是否已经激活
            // 如果未激活就激活弹出激活页面
            Intent intent = new Intent(DevicePolicyManager.ACTION_ADD_DEVICE_ADMIN);
            intent.putExtra(DevicePolicyManager.EXTRA_DEVICE_ADMIN,
                    myDeviceAdminSample);
            intent.putExtra(DevicePolicyManager.EXTRA_ADD_EXPLANATION,
                    "开启手机防盗功能必须激活此设备管理器");
            startActivity(intent);
        }
    }

    /**
     * 获取版本名
     *
     * @return 版本名
     */
    public String getVersionName() {
        PackageManager packageManager = getPackageManager();
        try {
            PackageInfo packageInfo = packageManager.getPackageInfo(
                    getPackageName(), 0);
            return packageInfo.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return "";

    }

    /**
     * 获取版本号
     *
     * @return 版本号
     */
    public int getVersionCode() {
        PackageManager packageManager = getPackageManager();
        try {
            PackageInfo packageInfo = packageManager.getPackageInfo(
                    getPackageName(), 0);
            return packageInfo.versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return 0;

    }

    /**
     * 复制assets里的内容到内部存储的files文件夹
     *
     * @param assetname
     */

    public void copy(String assetname) {
        // 目标文件位置
        File locFile = new File(getFilesDir(), assetname);
        if (locFile.exists()) {
            return;
        }
        OutputStream out = null;
        InputStream in = null;
        try {
            // assets的文件变成读取流
            in = getAssets().open(assetname);
            // 输出流
            out = new FileOutputStream(locFile);
            byte[] buf = new byte[1024];
            int num = 0;
            // 开始拷贝
            while ((num = in.read(buf)) != -1) {
                out.write(buf, 0, num);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (in != null) {
                try {
                    // 如果读取流不为空则关闭
                    in.close();
                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
                    // 关闭报错就设置为空，让虚拟机自己回收
                    in = null;
                }
            }
            if (out != null) {
                try {
                    // 如果输出流不为空则关闭
                    out.close();
                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
                    // 关闭报错就设置为空，让虚拟机自己回收
                    out = null;
                }
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // TODO Auto-generated method stub
        super.onActivityResult(requestCode, resultCode, data);

        // 安装界面被关闭，强制进入主页
        enterHome();
    }

    /**
     * 进入主界面
     */
    public void enterHome() {
        Intent intent = new Intent(this, Home2Activity.class);
        startActivity(intent);
        // 关闭splash
        finish();
        // splash退出效果
        overridePendingTransition(R.anim.splansh_in, R.anim.splansh_out);

    }

}
