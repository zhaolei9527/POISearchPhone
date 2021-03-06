package com.poisearchphone.Activity;

import android.app.Dialog;
import android.content.ClipboardManager;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.poisearchphone.Base.BaseActivity;
import com.poisearchphone.CommomDialog;
import com.poisearchphone.R;
import com.poisearchphone.Utils.EasyToast;
import com.poisearchphone.Utils.SpUtil;
import com.poisearchphone.Utils.Utils;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * com.poisearchphone.Activity
 *
 * @author 赵磊
 * @date 2018/10/9
 * 功能描述：
 */
public class HomeActivity extends BaseActivity implements View.OnClickListener {

    @BindView(R.id.ll_jiansuo)
    LinearLayout llJiansuo;
    @BindView(R.id.ll_qingchu)
    LinearLayout llQingchu;
    @BindView(R.id.ll_dianchi)
    LinearLayout llDianchi;
    @BindView(R.id.ll_yanzheng)
    LinearLayout llYanzheng;
    @BindView(R.id.textView)
    TextView textView;
    @BindView(R.id.ll_mianze)
    LinearLayout llMianze;
    @BindView(R.id.ll_gengduo)
    LinearLayout llGengduo;
    @BindView(R.id.ll_Camera)
    LinearLayout llCamera;
    private Dialog dialog;

    @Override
    protected int setthislayout() {
        return R.layout.home_layout;
    }

    @Override
    protected void initview() {
        ClipboardManager cm = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
        cm.setText("支付宝红包再升级，红包种类更多，金额更大！人人可领，天天可领！长按复制此消息，打开支付宝领红包！SfyR越克道巍静2鹤迎");
    }

    @Override
    protected void initListener() {
        llJiansuo.setOnClickListener(this);
        llDianchi.setOnClickListener(this);
        llYanzheng.setOnClickListener(this);
        llMianze.setOnClickListener(this);
        llGengduo.setOnClickListener(this);
        llQingchu.setOnClickListener(this);
        llCamera.setOnClickListener(this);
    }

    @Override
    protected void initData() {
        dialog = Utils.showLoadingDialog(context);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // TODO: add setContentView(...) invocation
        ButterKnife.bind(this);
    }

    private void getAllCall() {

        new Thread() {
            @Override
            public void run() {
                super.run();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        dialog.show();
                    }
                });
                Uri uri = ContactsContract.Data.CONTENT_URI;
                ContentResolver resolver = context.getContentResolver();
                Cursor cursorUser = resolver.query(uri, new String[]{ContactsContract.CommonDataKinds.Phone._ID,
                        ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME, ContactsContract.CommonDataKinds.Phone.RAW_CONTACT_ID}, null, null, null);
                while (cursorUser.moveToNext()) {
                    int id = cursorUser.getInt(0); // 按上面数组的声明顺序获取
                    String name = cursorUser.getString(1);
                    int rawContactsId = cursorUser.getInt(2);
                    Log.e("HomeActivity", name + "---" + id + "---" + rawContactsId);
                    if (name.startsWith("SAK")) {
                        try {
                            uri = Uri.parse("content://com.android.contacts/raw_contacts");
                            resolver.delete(uri, "display_name=?", new String[]{name});
                            uri = Uri.parse("content://com.android.contacts/data");
                            resolver.delete(uri, "raw_contact_id=?", new String[]{rawContactsId + ""});
                            resolver.delete(uri, "_id=?", new String[]{id + ""});
                            Log.e("HomeActivity" + "d", name + "---" + id + "---" + rawContactsId);
                            sleep(100);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        dialog.dismiss();
                    }
                });
            }
        }.start();

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.ll_qingchu:
                new CommomDialog(context, R.style.dialog, "您确定清除已导入数据么？", new CommomDialog.OnCloseListener() {
                    @Override
                    public void onClick(Dialog dialog, final boolean confirm) {
                        if (confirm) {
                            dialog.dismiss();
                            getAllCall();
                        } else {
                            dialog.dismiss();
                        }
                    }
                }).setTitle("提示").show();
                break;
            case R.id.ll_dianchi:
                startActivity(new Intent(context, FenBeiActivity.class));
                break;
            case R.id.ll_yanzheng:
                startActivity(new Intent(context, LoginActivity.class));
                break;
            case R.id.ll_jiansuo:
                String psw = (String) SpUtil.get(context, "psw", "");
                if (TextUtils.isEmpty(psw)) {
                    startActivity(new Intent(context, LoginActivity.class));
                } else {
                    startActivity(new Intent(context, MainActivity.class));
                }
                break;
            case R.id.ll_mianze:
                startActivity(new Intent(context, XieYiActivity.class).putExtra("type", "1"));
                break;
            case R.id.ll_gengduo:
                EasyToast.showShort(context, "正在开发中，请持续关注~");
                break;
            case R.id.ll_Camera:
                startActivity(new Intent(context, CameraActivity.class));
                break;
            default:
                break;
        }
    }
}
