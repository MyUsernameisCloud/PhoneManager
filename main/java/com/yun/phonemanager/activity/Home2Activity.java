package com.yun.phonemanager.activity;


import java.util.Random;

import com.yun.phonemanager.R;
import com.yun.phonemanager.util.MD5Utils;
import com.yun.phonemanager.util.ProcessInfoUtil;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.text.format.Formatter;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.RotateAnimation;
import android.view.animation.ScaleAnimation;
import android.view.animation.TranslateAnimation;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SlidingDrawer;
import android.widget.TextView;
import android.widget.Toast;

//public class Home2Activity extends BaseTouch
public class Home2Activity extends Activity {
    private CountDownTimer timer;
    private GridView gv;
    private GridView gv2;
    private SlidingDrawer sd;
    private ImageView iv_home;
    private TextView tv_home_num;
    private TextView tv_home_state;
    private ImageView iv_home_bg;
    private Random rd=new Random();
    private Random rd2=new Random();
    private boolean flag = false ;
    // 应用名
    String[] itemName = new String[] { "手机杀毒", "缓存清理", "软件管理", "通讯卫士" };
    String[] itemName2 = new String[] { "手机防盗", "进程管理", "高级工具", "设置中心" };
    // String[] itemName = new String[] { "手机防盗", "通讯卫士", "软件管理", "进程管理",
    //
    // "手机杀毒", "缓存清理", "高级工具", "设置中心" };
    // 应用图片ID
    // int[] itemR = new int[] { R.drawable.sjfd, R.drawable.t2xws,
    // R.drawable.r3jgl, R.drawable.h7cql, R.drawable.l5ltj,
    // R.drawable.j4cgl, R.drawable.g8jgj,
    // R.drawable.s9zzx, };
    int[] itemR = new int[] { R.drawable.l5ltj, R.drawable.j4cgl,
            R.drawable.r3jgl, R.drawable.t2xws};
    int[] itemR2 = new int[] { R.drawable.sjfd, R.drawable.open,
            R.drawable.g8jgj, R.drawable.s9zzx};
    private SharedPreferences sf;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        // 设置整个页面的布局
        setContentView(R.layout.activity_home2);
        // 获取haredPreferences获得配置信息
        sf = getSharedPreferences("config", MODE_PRIVATE);
        // 拿到gridview并设置适配器
        gv = (GridView) findViewById(R.id.gv_home2_home);
        gv2 = (GridView) findViewById(R.id.gv_home2_low);
        sd = (SlidingDrawer) findViewById(R.id.sd);
        tv_home_num = (TextView) findViewById(R.id.tv_home_num);
        tv_home_state = (TextView) findViewById(R.id.tv_home_state);
        iv_home_bg = (ImageView) findViewById(R.id.iv_home_bg);
        iv_home = (ImageView) findViewById(R.id.iv_home);
        RotateAnimation ra = new RotateAnimation(0, 360, RotateAnimation.RELATIVE_TO_SELF, 0.5f, RotateAnimation.RELATIVE_TO_SELF, 0.5f);
        ra.setDuration(600);
        ra.setRepeatCount(-1);
        iv_home.startAnimation(ra);
        check();
        //适配器填充数据
        gv.setAdapter(new MyAdapter());
        gv2.setAdapter(new MyAdapter2());
        // 设置item的点击事件
        gv.setOnItemClickListener(new MyListener());
        gv2.setOnItemClickListener(new MyListener2());
        //首页检测的点击事件
        iv_home_bg.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                finish();
                startActivity(new Intent(Home2Activity.this,CacheClearActivity.class));
            }
        });
    }
    /**
     * 进入程序锁
     * @param v
     */
   /* public void OnClickprogramLock(View v){
        startActivity(new Intent(this,ProgramLockActivity.class));
        overridePendingTransition(R.anim.exit_in, R.anim.exit_out);
    }*/

    Handler handler = new Handler(){
        public void handleMessage(android.os.Message msg) {
            int i = msg.what;
            if (i == 0)
                //在主线程中调用
                timer.cancel();
        }
            /*tv_home_num.setText(i+"");
            int radom=rd2.nextInt(5);

            if (i==100 ) {
                tv_home_state.setVisibility(View.VISIBLE);
                iv_home.clearAnimation();
                iv_home.setVisibility(View.INVISIBLE);
            }

            if (i == rd.nextInt(20) + 60 && (radom==1 || radom==3 || radom==4))
            {
                flag=true;
                tv_home_state.setVisibility(View.VISIBLE);
                tv_home_state.setText("清理一下吧");
                tv_home_state.setTextColor(getResources().getColor(R.color.red));
                iv_home.clearAnimation();
                iv_home.setVisibility(View.INVISIBLE);
            }
        };*/
    };

    /**
     * 首页的检测
     * @author Administrator
     *
     */
    public void check(){
        //获得可用内存和总内存，以计算内存使用率
       final int avaim = Integer.parseInt(String.valueOf( ProcessInfoUtil.getAvaiMemory(this)).trim().substring(0,2));
       //int totalm = Integer.parseInt(String.valueOf( ProcessInfoUtil.getTotalMemory(this)).trim().substring(0,2));

        //内存使用率
        //final int ratio=Integer.parseInt(String.valueOf( avaim).trim().substring(0,2));
      //  tv_home_num.setText(String.valueOf(ratio));

        timer=new CountDownTimer(1000*100,1000) {
            @Override
            public void onTick(long l) {
                if (Integer.parseInt(String.valueOf(l).trim().substring(0,2))==avaim+50)
                    onFinish();
                tv_home_num.setText(String.valueOf(l).trim().substring(0,2));
            }

            @Override
            public void onFinish() {
                if (avaim>=60)
                {
                    tv_home_state.setVisibility(View.VISIBLE);
                    iv_home.clearAnimation();
                    iv_home.setVisibility(View.INVISIBLE);
                }
                else {
                    tv_home_num.setText(String.valueOf(avaim));
                    tv_home_state.setVisibility(View.VISIBLE);
                    tv_home_state.setText("清理一下吧");
                    tv_home_state.setTextColor(getResources().getColor(R.color.red));
                    iv_home.clearAnimation();
                    iv_home.setVisibility(View.INVISIBLE);
                }
                Message msg = handler.obtainMessage();
                msg.what = 0 ;
                handler.sendMessage(msg);
            }
        }.start();

        /*
        new Thread(){
            public void run() {
                Random rd = new Random();
                for (int i = 1; i <=100; i++) {
                    if (flag == true)
                    {
                        Message msg = handler.obtainMessage();
                        msg.what = i ;
                        handler.sendMessage(msg);
                        break;
                    }

                    try {
                        sleep(100+rd.nextInt(200));
                        Message msg = handler.obtainMessage();
                        msg.what = i ;
                        handler.sendMessage(msg);
                    } catch (InterruptedException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }

                }//for

            };
        }.start();
        */
    }

    // item点击监听器
    class MyListener implements OnItemClickListener {

        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            switch (position) {
                case 0:
                    // 手机杀毒
                    startActivity(new Intent(Home2Activity.this,
                            AntivirusActivity.class));
                    overridePendingTransition(R.anim.exit_in, R.anim.exit_out);
                    break;
                case 1:
                    //缓存清理
                    startActivity(new Intent(Home2Activity.this, CacheClearActivity.class));
                    overridePendingTransition(R.anim.exit_in, R.anim.exit_out);
                    break;
                case 2:
                    // 软件管理
                    startActivity(new Intent(Home2Activity.this,
                            APPManageActivity.class));
                    overridePendingTransition(R.anim.exit_in, R.anim.exit_out);
                    break;
                case 3:
                    // 通讯卫士
                    startActivity(new Intent(Home2Activity.this,
                            CallSafeActivity.class));
                    overridePendingTransition(R.anim.exit_in, R.anim.exit_out);
                    break;
            }
        }
    }

    class MyListener2 implements OnItemClickListener {

        public void onItemClick(AdapterView<?> parent, View view, int position,
                                long id) {
            switch (position) {
                case 0:
                    //手机防盗
                    // 弹出对话框输入密码
                    showdialog();
                    break;
                case 1:
                    // 进程管理
                    startActivity(new Intent(Home2Activity.this,
                            ProcessManageActivity.class));
                    enterAndEndAnimation();
                    break;
                case 2:
                    // 高级工具
                    startActivity(new Intent(Home2Activity.this, ToolActivity.class));
                    enterAndEndAnimation();
                    break;
                case 3:
                    // 设置中心
                    startActivity(new Intent(Home2Activity.this,
                            SettingActivity.class));
                    enterAndEndAnimation();
                    break;
            }
        }
    }

    /**
     * 自定义网格适配器
     *
     * @author Administrator
     *
     */

    class MyAdapter extends BaseAdapter {  //自定义adapter必须实现的四个方法
        // 网格数量
        public int getCount() {
            return itemName.length;
        }
        public Object getItem(int position) {
            return itemName[position];
        }
        public long getItemId(int position) {
            return position;
        }
        // 设置展示的每个itemview
        public View getView(int position, View convertView, ViewGroup parent) {
            // 填充item布局xml
            View v = View.inflate(Home2Activity.this, R.layout.gridview_item,
                    null);
            //这种写法与上面等价
            //View v = layoutinflater.from(Home2Activity.this).inflate(R.layout.gridview_item,parent,true);

            // 设置应用名，和图片
            ImageView iv = (ImageView) v.findViewById(R.id.iv_item);
            TextView tv = (TextView) v.findViewById(R.id.tv_item);
            iv.setImageResource(itemR[position]);
            tv.setText(itemName[position]);
            // 返回设置好的Item
            return v;
        }

    }
    class MyAdapter2 extends BaseAdapter {
        // 网格数量
        public int getCount() {
            return itemName2.length;
        }

        public Object getItem(int position) {
            return itemName2[position];
        }

        public long getItemId(int position) {
            return position;
        }

        // 设置展示的每个itemview
        public View getView(int position, View convertView, ViewGroup parent) {
            // 填充item布局xml
            View v = View.inflate(Home2Activity.this, R.layout.gridview_item2,
                    null);
            // 设置应用名，和图片
            ImageView iv = (ImageView) v.findViewById(R.id.iv_item);
            TextView tv = (TextView) v.findViewById(R.id.tv_item);
            iv.setImageResource(itemR2[position]);
            tv.setText(itemName2[position]);
            // 返回设置好的Item
            return v;

        }

    }

    /**
     * 判断是否弹出 对话框设置密码
     */

    public void showdialog() {
        // 从配置信息中获得密码
        String savepass = sf.getString("pass", null);
        // 如果密码不为空这直接输入密码
        if (!TextUtils.isEmpty(savepass)) {
            //去判断输入框中的密码是否与已经存储的一样
            inputDialog(savepass);
        } else {
            // 如果密码为空这提示设置密码，即以前没有设置过这个密码
            setDialog();
        }
    }

    /**
     * 输入对话框
     */
    public void inputDialog(final String savepass) {
        Builder builder = new Builder(this);
        // 得到Dialog
        final AlertDialog dialog = builder.create();
        // 将自定义dialog填充成view,在这里输入密码
        View v = View.inflate(this, R.layout.dialoginput, null);
        // 将自定义view设置给dialog,并且去边框保证2.x能达到效果
        dialog.setView(v, 0, 0, 0, 0);
        // 拿到editview
        final EditText et_dialogps = (EditText) v.findViewById(R.id.et_dialogps);
        // 获得确定和取消Button
        Button bt_ok = (Button) v.findViewById(R.id.bt_ok);
        Button bt_cancel = (Button) v.findViewById(R.id.bt_cancel);
        // 给确定键设置监听
        bt_ok.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                // 获取密码信息
                String pass = et_dialogps.getText().toString();
                // 判断密码和确定密码是否正确，是否为空
                if (!TextUtils.isEmpty(pass)) {
                    if (MD5Utils.encode(pass).equals(savepass)) {
                        Toast.makeText(Home2Activity.this, "登陆成功", Toast.LENGTH_SHORT).show();
                        // 成功登陆后跳转到手机防盗页面
                        dialog.dismiss();
                        Intent intent = new Intent(Home2Activity.this, LostFindActivity.class);
                        startActivity(intent);
                        // 跳转动画
                        enterAndEndAnimation();
                    } else {
                        // 提示用户密码不正确
                        Toast.makeText(Home2Activity.this, "密码不正确",  Toast.LENGTH_SHORT).show();
                    }
                    // 密码为空提示用户
                } else {
                    Toast.makeText(Home2Activity.this, "密码不能为空", Toast.LENGTH_SHORT).show();
                }
            }
        });

        // 给取消键设置监听
        bt_cancel.setOnClickListener(new OnClickListener() {

            public void onClick(View v) {
                // 如果按取消键则隐藏dialog
                dialog.dismiss();
            }
        });
        // 显示
        dialog.show();
    }

    /**
     * 设置对话框，第一次登陆
     */
    public void setDialog() {
        Builder builder = new Builder(this);
        // 得到Dialog
        final AlertDialog dialog = builder.create();
        // 将自定义dialog填充成view
        View v = View.inflate(this, R.layout.dialog, null);
        // 将自定义view设置给dialog,并且去边框保证2.x能达到效果
        dialog.setView(v, 0, 0, 0, 0);
        // 拿到editview
        final EditText et_dialoginput = (EditText) v.findViewById(R.id.et_dialoginput);
        // 拿到确认密码的editview
        final EditText et_dialoginputagain = (EditText) v.findViewById(R.id.et_dialoginputagain);
        // 获得确定和取消Button
        Button bt_ok = (Button) v.findViewById(R.id.bt_ok);
        Button bt_cancel = (Button) v.findViewById(R.id.bt_cancel);
        // 给确定键设置监听
        bt_ok.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                // 获取密码信息
                String pass = et_dialoginput.getText().toString();
                String passagain = et_dialoginputagain.getText().toString();
                // 判断密码和确定密码是否正确，是否为空
                if (!TextUtils.isEmpty(pass) && !TextUtils.isEmpty(passagain)) {
                    //两输入框都有输入，再进一步判断两次输入的密码是否一样
                    if (pass.equals(passagain)) {
                        // 达到要求后吧密码保存到配置信息中
                        sf.edit().putString("pass", MD5Utils.encode(pass))
                                .commit();
                        Toast.makeText(Home2Activity.this, "登陆成功", Toast.LENGTH_SHORT).show();
                        // 成功登陆后跳转到手机防盗页面
                        dialog.dismiss();
                        Intent intent = new Intent(Home2Activity.this, LostFindActivity.class);
                        startActivity(intent);
                        // 跳转动画
                        enterAndEndAnimation();
                    } else {
                        // 提示用户密码和确认密码不匹配
                        Toast.makeText(Home2Activity.this, "密码和确认密码不匹配", Toast.LENGTH_SHORT).show();
                    }
                    // 密码和确认密码为空提示用户
                } else {
                    Toast.makeText(Home2Activity.this, "密码或确认密码不能为空", Toast.LENGTH_SHORT).show();
                }
            }
        });

        // 给取消键设置监听

        bt_cancel.setOnClickListener(new OnClickListener() {

            public void onClick(View v) {
                // 如果按取消键则隐藏dialog
                dialog.dismiss();
            }
        });
        // 显示
        dialog.show();
    }
    /**
     * 检测menu键打开抽屉
     */
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        // TODO Auto-generated method stub
        if (keyCode==KeyEvent.KEYCODE_MENU) {
            //isOpened是slidedrawer自带方法
            if (sd.isOpened()) {
                sd.close();
            }else if (!sd.isOpened()) {
                sd.open();
            }
        }

        if (keyCode == KeyEvent.KEYCODE_BACK) {
            Intent home = new Intent(Intent.ACTION_MAIN);
            home.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            home.addCategory(Intent.CATEGORY_HOME);
            startActivity(home);
            return true;
        }

        return super.onKeyDown(keyCode, event);
    }
    /**
     * 进入和退出动画
     */
    public void enterAndEndAnimation() {
        // 设置进入和退出的动画
        overridePendingTransition(R.anim.splansh_in, R.anim.splansh_out);
    }
/*
    @Override
    public void showNextPage() {

    }

    @Override
    public void showPreviousPage() {

    }
    */
}
