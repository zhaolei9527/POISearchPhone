package com.poisearchphone.Activity;

import android.Manifest;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.mylhyl.acp.Acp;
import com.mylhyl.acp.AcpListener;
import com.mylhyl.acp.AcpOptions;
import com.poisearchphone.Base.BaseActivity;
import com.poisearchphone.R;
import com.poisearchphone.Utils.EasyToast;
import com.poisearchphone.Utils.SpUtil;
import com.poisearchphone.Utils.Utils;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by 赵磊 on 2017/7/13.
 */

public class LoginActivity extends BaseActivity implements View.OnClickListener {

    @BindView(R.id.et_account)
    EditText etAccount;
    @BindView(R.id.rl)
    LinearLayout rl;
    @BindView(R.id.et_passwd)
    EditText etPasswd;
    @BindView(R.id.rl2)
    LinearLayout rl2;
    @BindView(R.id.tv_lookuuid)
    TextView tvLookuuid;
    @BindView(R.id.rl3)
    RelativeLayout rl3;
    @BindView(R.id.btn_login)
    Button btnLogin;
    private String str;

    @Override
    protected void ready() {
        super.ready();
       /*set it to be no title*/
        requestWindowFeature(Window.FEATURE_NO_TITLE);
       /*set it to be full screen*/
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
    }

    @Override
    protected int setthislayout() {
        return R.layout.activcity_login;
    }

    @Override
    protected void initview() {
        btnLogin.setOnClickListener(this);

    }

    @Override
    protected void initListener() {
        tvLookuuid.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(context, UUIDActivity.class));
            }
        });
    }

    @Override
    protected void initData() {
        Acp.getInstance(context).request(new AcpOptions.Builder()
                        .setPermissions(Manifest.permission.WRITE_EXTERNAL_STORAGE
                                , Manifest.permission.READ_CONTACTS
                                , Manifest.permission.WRITE_CONTACTS
                                , Manifest.permission.RECORD_AUDIO
                        )
                        .setDeniedMessage(getString(R.string.requstPerminssions))
                        .build(),
                new AcpListener() {
                    @Override
                    public void onGranted() {
                        String psw = (String) SpUtil.get(context, "psw", "");

                        if (!TextUtils.isEmpty(psw)) {
                            etPasswd.setText(psw);
                        }

                    }

                    @Override
                    public void onDenied(List<String> permissions) {
                        Toast.makeText(context, R.string.Thepermissionapplicationisrejected, Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void gotoMain() {
        startActivity(new Intent(context, XieYiActivity.class));
        finish();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_login:
                String psw = etPasswd.getText().toString().trim();

                if (psw.length() != 6) {
                    Toast.makeText(context, etPasswd.getHint().toString(), Toast.LENGTH_SHORT).show();
                    return;
                }

                if (TextUtils.isEmpty(psw)) {
                    Toast.makeText(context, etPasswd.getHint().toString(), Toast.LENGTH_SHORT).show();
                    return;
                }

                String str = String.valueOf(SpUtil.get(this, "androidId", ""));
                str = str.substring(str.length() - 6, str.length()); // or str=str.Remove(str.Length-i,i);
                str = Utils.md5(str);
                str = Utils.md5(str);
                str = str.substring(str.length() - 6, str.length()); // or str=str.Remove(str.Length-i,i);
                if (str.equals(psw)) {
                    gotoMain();
                    EasyToast.showShort(context, "登入成功");
                    SpUtil.putAndApply(context, "psw", psw);
                } else {
                    Toast.makeText(context, etPasswd.getHint().toString(), Toast.LENGTH_SHORT).show();
                    return;
                }
                break;
            default:
                break;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // TODO: add setContentView(...) invocation
        ButterKnife.bind(this);
    }
}
