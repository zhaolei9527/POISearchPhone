package com.poisearchphone.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;

import com.poisearchphone.Base.BaseActivity;
import com.poisearchphone.R;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * com.poisearchphone.Activity
 *
 * @author 赵磊
 * @date 2018/10/9
 * 功能描述：
 */
public class XieYiActivity extends BaseActivity {
    @BindView(R.id.btn_submit)
    Button btnSubmit;
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
        return R.layout.xieyi_layout;
    }

    @Override
    protected void initview() {

    }

    @Override
    protected void initListener() {

        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(context, MainActivity.class));
                finish();
            }
        });

    }

    @Override
    protected void initData() {

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // TODO: add setContentView(...) invocation
        ButterKnife.bind(this);
    }
}
