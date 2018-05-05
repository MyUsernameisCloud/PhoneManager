package com.yun.phonemanager.receiver;

import com.yun.phonemanager.activity.SettingActivity;
import com.yun.phonemanager.sevice.AddressDisplayService;
import com.yun.phonemanager.sevice.BlackAbortService;
import com.yun.phonemanager.sevice.LockService;
import com.yun.phonemanager.sevice.ProgramDogService;
import com.yun.phonemanager.util.ServiceState;
import com.yun.touch.RocketService;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.telephony.TelephonyManager;
import android.telephony.gsm.SmsManager;
import android.text.TextUtils;

//直接在清单文件中使用过滤器接收开机的动作，当收到开机广播后就到BootCheck中执行onReceive方法
//在此方法中，可以启动需要开机启动的服务
public class BootCheck extends BroadcastReceiver {
    SharedPreferences shp;

    @Override
    public void onReceive(Context context, Intent intent) {
        // 开启启动后立即检测sim卡是否变更
        shp = context.getSharedPreferences("config", Context.MODE_PRIVATE);
        // 查看是否配置开机启动归属地查询
        boolean addressboot = shp.getBoolean("addressboot", false);
        // 查看是否配置开机启动锁屏助手
        boolean lockboot = shp.getBoolean("lockboot", true);
        //查看是否配置手机开机启动黑名单拦截
        boolean blackboot = shp.getBoolean("blackboot", false);
        //查看是否配置手机开机启动程序锁
        boolean programboot = shp.getBoolean("programboot", false);
        // 依照配置信息开机启动或不开机启动归属地查询显示
        if (addressboot) {
            //如果配置了就开机启动
            Intent it_add = new Intent(context,
                    AddressDisplayService.class);
            context.startService(it_add);
        }
        // 依照配置信息开机启动或不开机启动锁屏助手
        //如果配置了就开机启动
        if (lockboot) {
            //查询服务状态，如果悬浮窗没有启动就开启此服务
            if (!ServiceState.serviceRunning(context, "com.yun.touch.RocketService")) {
                context.startService(new Intent(context,
                        RocketService.class));
            }
        }

        if (blackboot) {
            //如果配置了就开机启动
            context.startService(new Intent(context,
                    BlackAbortService.class));
        }

        if (programboot) {
            //如果配置了就开机启动
            context.startService(new Intent(context,
                    ProgramDogService.class));
        }

        // 查看配置文件是否已经设置防盗保护
        boolean protect = shp.getBoolean("protect", false);
        // 如果用户设置了防盗保护在开始下面的功能
        if (protect) {
            // 拿到sharapreference里的sim序列号
            String SIMNumber = shp.getString("SIM", null);
            // 获得当前的sim序列号进行比对
            TelephonyManager tm = (TelephonyManager) context
                    .getSystemService(Context.TELEPHONY_SERVICE);
            String cNumber = tm.getSimSerialNumber();
            // 如果两个序列号都不为空
            if (!TextUtils.isEmpty(SIMNumber) && !TextUtils.isEmpty(cNumber)) {
                // 在序列号不匹配的情况下
                if (!SIMNumber.equals(cNumber)) {
                    //SIM已变更发送警报短信
                    //获得配置信息中的安全号码
                    String phone = shp.getString("phone", "");
                    SmsManager sm = SmsManager.getDefault();
                    //发送警报短信
                    sm.sendTextMessage(phone, null, "SMS is change!", null, null);
                }

            }
        }
    }

}
