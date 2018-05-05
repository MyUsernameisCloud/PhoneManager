package com.yun.phonemanager.activity;

import java.util.ArrayList;
import java.util.List;

import com.yun.phonemanager.R;
import com.yun.phonemanager.activity.BaseTouch2.MyAdapter2;
import com.yun.phonemanager.activity.BaseTouch2.MyListener2;
import com.yun.phonemanager.dao.AntivirusDao;
import com.yun.phonemanager.domain.AppInfo;
import com.yun.phonemanager.engine.APPInfoProvider;
import com.yun.phonemanager.util.MD5Utils;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog.Builder;
import android.content.ClipData;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.AnimationSet;
import android.view.animation.ScaleAnimation;
import android.view.animation.TranslateAnimation;
import android.webkit.ValueCallback;
import android.widget.Chronometer;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SlidingDrawer;
import android.widget.TextView;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;


public class AntivirusActivity  extends  BaseTouch2{
    //extends BaseTouch2
    protected static final int FINISH = 0;
    protected static final int SCAN = 1;
    protected static final int START = 2;
    private GridView gv2;
    private ImageView iv_antivirus;
    private AnimationSet as;
    private TextView tv_antivirus_state;
    private LinearLayout ll_antivirus_display;
    private List<AntiInfo> ainfos;
    private TextView tv_antivirus_safe;
    private WebView webview;
    private ValueCallback<Uri> uploadMessage;
    private ValueCallback<Uri[]> uploadMessageAboveL;
    private final static int FILE_CHOOSER_RESULT_CODE = 10000;
    private Context mcontext;
    private Chronometer timer;
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_antivirus);
        mcontext=this.getApplicationContext();
        timer = (Chronometer) findViewById(R.id.timer);
       // gv2 = (GridView) findViewById(R.id.gv_home2_low);
        //setSD((SlidingDrawer) findViewById(R.id.sd));
       // gv2.setAdapter(new MyAdapter2());
        //gv2.setOnItemClickListener(new MyListener2());

        iv_antivirus = (ImageView) findViewById(R.id.iv_antivirus);
        tv_antivirus_state = (TextView) findViewById(R.id.tv_antivirus_state);
        ll_antivirus_display = (LinearLayout) findViewById(R.id.ll_antivirus_display);
        tv_antivirus_safe = (TextView) findViewById(R.id.tv_antivirus_safe);
        as = new AnimationSet(false);

        TranslateAnimation ta = new TranslateAnimation(
                TranslateAnimation.RELATIVE_TO_SELF, 0,
                TranslateAnimation.RELATIVE_TO_SELF, 1.7f,
                TranslateAnimation.RELATIVE_TO_PARENT, 0,
                TranslateAnimation.RELATIVE_TO_PARENT, 0);
        ta.setDuration(300);
        ta.setRepeatCount(-1);

        as.addAnimation(ta);

        ScaleAnimation sa = new ScaleAnimation(0.3f, 0.9f, 0.3f, 0.9f,
                ScaleAnimation.RELATIVE_TO_SELF, 0.5f,
                ScaleAnimation.RELATIVE_TO_SELF, 0.5f);
        sa.setDuration(300);
        sa.setRepeatCount(-1);

        as.addAnimation(sa);

        AlphaAnimation aa = new AlphaAnimation(0, 1);
        aa.setDuration(300);
        aa.setRepeatMode(-1);

        as.addAnimation(aa);
        anim();
    }

    Handler handler = new Handler() {
        public void handleMessage(android.os.Message msg) {
            switch (msg.what) {
                case SCAN:
                    AntiInfo ai = (AntiInfo) msg.obj;
                    Drawable icon = ai.getIcon();
                    String name = ai.getName();
                    tv_antivirus_state.setText("正在扫描:" + name);
                    iv_antivirus.setBackgroundDrawable(icon);
                    TextView view = new TextView(AntivirusActivity.this);
                    view.setTextSize(18);
                    if (ai.isAniti) {
                        view.setTextColor(0xffff0000);
                        view.setText("发现病毒:"+name);
                        ainfos.add(ai);
                    }else{
                        view.setTextColor(0x66000000);
                        view.setText("扫描安全:"+name);
                    }
                    ll_antivirus_display.addView(view, 0);
                    break;
                case FINISH:
                    //计时结束
                    TimeStop();
                    tv_antivirus_state.setText("扫描完成");
                    iv_antivirus.clearAnimation();
                    iv_antivirus.setVisibility(View.INVISIBLE);
                    //发现病毒，弹出处理对话框
                    if (ainfos!=null&&ainfos.size()>0) {
                        Builder builder = new Builder(AntivirusActivity.this);
                        builder.setTitle("警告!");
                        builder.setMessage("在您的手机发现"+ainfos.size()+"个病毒,请选择立即杀毒！");
                        builder.setNegativeButton("以后再说", null);
                        builder.setPositiveButton("立即杀毒", new OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                for (AntiInfo info : ainfos) {
                                    String packageName = info.getPackageName();
                                    Intent intent = new Intent(Intent.ACTION_DELETE);
                                    intent.setData(Uri.parse("package:"+packageName));
                                    startActivity(intent);
                                }
                            }
                        });
                        builder.show();
                        tv_antivirus_safe.setVisibility(View.VISIBLE);
                        tv_antivirus_safe.setText("病毒入侵");
                    }
                    else{
                        tv_antivirus_safe.setVisibility(View.VISIBLE);
                    }
                    break;
                case START:
                    //计时开始
                    TimeStart();
                    ainfos = new ArrayList<AntivirusActivity.AntiInfo>();
                    AntiInfo ais = (AntiInfo) msg.obj;
                    Drawable icons = ais.getIcon();
                    String names = ais.getName();
                    tv_antivirus_state.setText("正在扫描:" + names);
                    iv_antivirus.setBackgroundDrawable(icons);
                    iv_antivirus.setVisibility(View.VISIBLE);
                    iv_antivirus.startAnimation(as);
                    TextView v = new TextView(AntivirusActivity.this);
                    v.setTextSize(18);
                    if (ais.isAniti) {
                        v.setTextColor(0xffff0000);
                        v.setText("发现病毒:"+names);
                        ainfos.add(ais);
                    }else{
                        v.setTextColor(0x66000000);
                        v.setText("扫描安全:"+names);
                    }
                    ll_antivirus_display.addView(v, 0);
                    break;
            }
        };
    };

    //开始计时
    public  void TimeStart()
    {
        int hour = (int) ((SystemClock.elapsedRealtime() - timer.getBase()) / 1000 / 60);
        timer.setFormat("0"+String.valueOf(hour)+":%s");
        timer.start();
    }
   //结束计时
    public  void TimeStop()
    {
        timer.stop();
    }

    public void anim() {
        final PackageManager pm = getPackageManager();
        final AntivirusDao ad = new AntivirusDao();
        new Thread() {
            public void run() {
                List<AppInfo> infos = APPInfoProvider.getAppInfo(AntivirusActivity.this);
                //初始化
                try {
                    sleep(2000);
                } catch (InterruptedException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
                for (int x = 0; x < infos.size(); x++) {
                    try {
                        sleep(330);
                    } catch (InterruptedException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                    if (x == 0) {
                        AntiInfo ai = new AntiInfo();
                        Drawable icon = infos.get(x).getIcon();
                        String name = infos.get(x).getName();
                        String packageName = infos.get(x).getPackageName();
                        try {
                            PackageInfo packageInfo = pm.getPackageInfo(infos.get(x).getPackageName(),
                                    PackageManager.GET_UNINSTALLED_PACKAGES
                                            + PackageManager.GET_SIGNATURES);
                            String signature = MD5Utils.encode(packageInfo.signatures[0].toCharsString());
                            //用signature到病毒库中查询，返回值为空说明病毒库中没有这条信息，这个应用不是病毒
                            if (ad.query(signature,mcontext) == null) {
                                ai.setAniti(false);
                            } else {
                                ai.setAniti(true);
                            }
                        } catch (Exception e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        }
                        ai.setPackageName(packageName);
                        ai.setIcon(icon);
                        ai.setName(name);
                        Message msg = handler.obtainMessage();
                        msg.obj = ai;
                        msg.what = START;
                        handler.sendMessage(msg);
                    }
                    else {
                        AntiInfo ai = new AntiInfo();

                        Drawable icon = infos.get(x).getIcon();
                        String name = infos.get(x).getName();
                        String packageName = infos.get(x).getPackageName();
                        try {
                            PackageInfo packageInfo = pm.getPackageInfo(infos
                                            .get(x).getPackageName(),
                                    PackageManager.GET_UNINSTALLED_PACKAGES
                                            + PackageManager.GET_SIGNATURES);
                            String signature = MD5Utils
                                    .encode(packageInfo.signatures[0]
                                            .toCharsString());
                            if (ad.query(signature,mcontext) == null) {
                                ai.setAniti(false);
                            } else {
                                ai.setAniti(true);
                            }

                        } catch (Exception e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        }
                        ai.setPackageName(packageName);
                        ai.setIcon(icon);
                        ai.setName(name);
                        Message msg = handler.obtainMessage();
                        msg.obj = ai;

                        msg.what = SCAN;
                        handler.sendMessage(msg);
                    }

                }//for

                Message msg = handler.obtainMessage();
                msg.what = FINISH;
                handler.sendMessage(msg);
            };
        }.start();

    }

    class AntiInfo {
        private Drawable icon;
        private String name;
        private boolean isAniti;
        private String packageName;
        public String getPackageName() {
            return packageName;
        }
        public void setPackageName(String packageName) {
            this.packageName = packageName;
        }
        public Drawable getIcon() {
            return icon;
        }
        public void setIcon(Drawable icon) {
            this.icon = icon;
        }
        public String getName() {
            return name;
        }
        public void setName(String name) {
            this.name = name;
        }
        public boolean isAniti() {
            return isAniti;
        }
        public void setAniti(boolean isAniti) {
            this.isAniti = isAniti;
        }
    }

    @Override
    public void showNextPage() {
        // TODO Auto-generated method stub

    }

    @Override
    public void showPreviousPage() {
        // TODO Auto-generated method stub

    }

}


/*	webview = (WebView) findViewById(R.id.webview);

		webview.loadUrl("https://m.qq.com/security_lab/scans_online.jsp");


		WebSettings settings = webview.getSettings();

		// 设置与Js交互的权限
		settings.setJavaScriptEnabled(true);
		// 设置允许JS弹窗
		settings.setJavaScriptCanOpenWindowsAutomatically(true);
		settings.setUseWideViewPort(true);
		settings.setLoadWithOverviewMode(true);

		webview.setWebViewClient(new WebViewClient() {
			@Override
			public boolean shouldOverrideUrlLoading(WebView view, String url) {
				view.loadUrl(url);
				return true;
			}
		});
*/

	/*
	webview.setWebViewClient(new WebViewClient() {
		@Override
		public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {
			handler.proceed();    //表示等待证书响应
			// handler.cancel();      //表示挂起连接，为默认方式
			// handler.handleMessage(null);    //可做其他处理
		}
	});
*/
/*
	webview.setWebChromeClient(new WebChromeClient() {

		// For Android < 3.0
		public void openFileChooser(ValueCallback<Uri> valueCallback) {
			uploadMessage = valueCallback;
			openImageChooserActivity();
		}

		// For Android  >= 3.0
	public void openFileChooser(ValueCallback valueCallback, String acceptType) {
		uploadMessage = valueCallback;
		openImageChooserActivity();
	}

	//For Android  >= 4.1
	public void openFileChooser(ValueCallback<Uri> valueCallback, String acceptType, String capture) {
		uploadMessage = valueCallback;
		openImageChooserActivity();
	}

	// For Android >= 5.0
	@Override
	public boolean onShowFileChooser(WebView webView, ValueCallback<Uri[]> filePathCallback, WebChromeClient.FileChooserParams fileChooserParams) {
		uploadMessageAboveL = filePathCallback;
		openImageChooserActivity();
		return true;
	}
});
		String targetUrl = "file:///android_asset/up.html";
		webview.loadUrl(targetUrl);
	}


	private void openImageChooserActivity() {
		Intent i = new Intent(Intent.ACTION_GET_CONTENT);
		i.addCategory(Intent.CATEGORY_OPENABLE);
		i.setType("image/*");
		startActivityForResult(Intent.createChooser(i, "Image Chooser"), FILE_CHOOSER_RESULT_CODE);
	}


	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (requestCode == FILE_CHOOSER_RESULT_CODE) {
			if (null == uploadMessage && null == uploadMessageAboveL) return;
			Uri result = data == null || resultCode != RESULT_OK ? null : data.getData();
			if (uploadMessageAboveL != null) {
				onActivityResultAboveL(requestCode, resultCode, data);
			} else if (uploadMessage != null) {
				uploadMessage.onReceiveValue(result);
				uploadMessage = null;
			}
		}
	}

	@TargetApi(Build.VERSION_CODES.LOLLIPOP)
	private void onActivityResultAboveL(int requestCode, int resultCode, Intent intent) {
		if (requestCode != FILE_CHOOSER_RESULT_CODE || uploadMessageAboveL == null)
			return;
		Uri[] results = null;
		if (resultCode == Activity.RESULT_OK) {
			if (intent != null) {
				String dataString = intent.getDataString();
				ClipData clipData = intent.getClipData();
				if (clipData != null) {
					results = new Uri[clipData.getItemCount()];
					for (int i = 0; i < clipData.getItemCount(); i++) {
						ClipData.Item item = clipData.getItemAt(i);
						results[i] = item.getUri();
					}
				}
				if (dataString != null)
					results = new Uri[]{Uri.parse(dataString)};
			}
		}
		uploadMessageAboveL.onReceiveValue(results);
		uploadMessageAboveL = null;
	}


	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK && webview.canGoBack()) {
			 webview.goBack();
			return true;
		}

		return super.onKeyDown(keyCode, event);
	}

	@Override
	protected void onDestroy() {
		if (webview != null) {
			webview.loadDataWithBaseURL(null, "", "text/html", "utf-8", null);
			webview.clearHistory();

			((ViewGroup) webview.getParent()).removeView(webview);
			webview.destroy();
			webview = null;
		}
		super.onDestroy();
	}
*/
