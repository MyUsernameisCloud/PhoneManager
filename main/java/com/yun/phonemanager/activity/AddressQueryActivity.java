package com.yun.phonemanager.activity;


import com.yun.phonemanager.R;
import com.yun.phonemanager.dao.AddressDao;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Vibrator;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.CycleInterpolator;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class AddressQueryActivity extends BaseTouch {

    private EditText et;
    private TextView tv;
    private Context mcontext;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_addressquery);
        mcontext=this.getApplicationContext();
        // 拿到输入号码的View
        et = (EditText) findViewById(R.id.et_addressquery);
        // 拿到输出结果的View
        tv = (TextView) findViewById(R.id.tv_query_result);

        // 给输入框设置监听变化改变,实时查询结果
        et.addTextChangedListener(new MyWatcher());
    }

    /**
     * 监听器
     *
     * @author Administrator
     *
     */
    class MyWatcher implements TextWatcher {
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            // 文本框发生变化之前调用
        }
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            // 文本框发生变化时调用
            if (!TextUtils.isEmpty(s.toString())) {
                // 非空查询数据库，得到地址
                String address = AddressDao.addressQuery(s.toString(),mcontext);
                tv.setText(address);
            }
        }
        public void afterTextChanged(Editable s) {
            // 文本框发生变化之后调用
        }
    }
    /**
     * 查询电话地址
     *
     * @param v
     */
    public void OnClickquery(View v) {
        String phone = et.getText().toString();
        if (!TextUtils.isEmpty(phone)) {
            // 非空查询数据库，得到地址
            String address = AddressDao.addressQuery(phone,mcontext);
            tv.setText(address);
        } else {
            // 如果为空用输入框震动提示用户
            // 拿到动画
            Animation shake = AnimationUtils.loadAnimation(this, R.anim.shake);
            // 设置插补器
            shake.setInterpolator(new CycleInterpolator(5));
            // 开启动画
            et.startAnimation(shake);
            // 开启震动
            vibrate();
        }
    }

    /**
     * 震动方法
     */
    public void vibrate() {
        // 拿到振动器
        Vibrator vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
        // 设置震动频率
        //按照指定的模式去震动。
        // 数组参数意义：第一个参数为等待指定时间后开始震动，震动时间为第二个参数。后边的参数依次为等待震动和震动的时间
        //第二个参数为重复次数，-1为不重复，0为一直震动
        vibrator.vibrate(new long[] { 200, 100, 500, 100 }, -1);
    }

    @Override
    public void showNextPage() {

    }

    @Override
    public void showPreviousPage() {
        // 进入高级工具activity
//		Intent intent = new Intent(this, ToolActivity.class);
//		startActivity(intent);
        // 关闭页面
        finish();
        // 动画
        previousAnimation();
    }

}
