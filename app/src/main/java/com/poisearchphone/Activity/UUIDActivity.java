package com.poisearchphone.Activity;

import android.os.Bundle;
import android.provider.Settings.Secure;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.poisearchphone.R;

import butterknife.BindView;
import butterknife.ButterKnife;

public class UUIDActivity extends AppCompatActivity {

    @BindView(R.id.tv_uuid)
    TextView tvUuid;
    @BindView(R.id.rl_back)
    FrameLayout rlBack;
    private String androidId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_uuid);
        ButterKnife.bind(this);
        androidId = Secure.getString(getContentResolver(), Secure.ANDROID_ID);

        if (!TextUtils.isEmpty(androidId)) {
            tvUuid.setText(androidId);
        } else {
            tvUuid.setText("请检查权限或使用标准设备");
        }

        rlBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

    }
}
