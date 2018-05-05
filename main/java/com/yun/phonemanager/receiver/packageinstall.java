package com.yun.phonemanager.receiver;


import com.yun.phonemanager.activity.SplashActivity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.widget.Toast;

public class packageinstall extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        // TODO Auto-generated method stub
        Uri data = intent.getData();
        String action = intent.getAction();
        if (action.equals("android.intent.action.PACKAGE_ADDED")) {

            Toast.makeText(context, data + "被安装", Toast.LENGTH_SHORT).show();
        } else if (action.equals("android.intent.action.PACKAGE_REMOVED")) {
            Toast.makeText(context, data + "被卸载", Toast.LENGTH_SHORT).show();
        }
    }

}
