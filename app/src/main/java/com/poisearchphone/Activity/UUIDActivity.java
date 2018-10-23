package com.poisearchphone.Activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.poisearchphone.R;
import com.poisearchphone.Utils.SpUtil;

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

        if (TextUtils.isEmpty(String.valueOf(SpUtil.get(this, "androidId", "")))) {
            androidId = createRandom(false, 18);
            SpUtil.putAndApply(this, "androidId", androidId);
        } else {
            androidId = String.valueOf(SpUtil.get(this, "androidId", ""));
        }

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

    /**
     * 创建指定数量的随机字符串
     *
     * @param numberFlag 是否是数字
     * @param length
     * @return
     */
    public static String createRandom(boolean numberFlag, int length) {
        String retStr = "";
        String strTable = numberFlag ? "1234567890" : "1234567890abcdefghijkmnpqrstuvwxyz";
        int len = strTable.length();
        boolean bDone = true;
        do {
            retStr = "";
            int count = 0;
            for (int i = 0; i < length; i++) {
                double dblR = Math.random() * len;
                int intR = (int) Math.floor(dblR);
                char c = strTable.charAt(intR);
                if (('0' <= c) && (c <= '9')) {
                    count++;
                }
                retStr += strTable.charAt(intR);
            }
            if (count >= 2) {
                bDone = false;
            }
        } while (bDone);

        return retStr;
    }


}
