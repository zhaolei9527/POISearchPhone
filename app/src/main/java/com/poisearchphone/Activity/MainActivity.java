package com.poisearchphone.Activity;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;

import com.android.volley.VolleyError;
import com.google.gson.Gson;
import com.poisearchphone.Base.BaseActivity;
import com.poisearchphone.Bean.POIBean;
import com.poisearchphone.CommomDialog;
import com.poisearchphone.R;
import com.poisearchphone.Utils.EasyToast;
import com.poisearchphone.Utils.Utils;
import com.poisearchphone.Volley.VolleyInterface;
import com.poisearchphone.Volley.VolleyRequest;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends BaseActivity implements View.OnClickListener {

    @BindView(R.id.et_city)
    EditText etCity;
    @BindView(R.id.et_keyword)
    EditText etKeyword;
    @BindView(R.id.btn_submit)
    Button btnSubmit;
    private String city;
    private String keyword;
    private Dialog dialog;

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
        return R.layout.activity_main;
    }

    @Override
    protected void initview() {

    }

    @Override
    protected void initListener() {
        btnSubmit.setOnClickListener(this);
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

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_submit:
                city = etCity.getText().toString().trim();
                keyword = etKeyword.getText().toString().trim();
                if (TextUtils.isEmpty(city)) {
                    EasyToast.showShort(context, etCity.getHint().toString());
                    return;
                }
                if (TextUtils.isEmpty(keyword)) {
                    EasyToast.showShort(context, etKeyword.getHint().toString());
                    return;
                }

                if (Utils.isConnected(context)) {
                    dialog = Utils.showLoadingDialog(context);
                    dialog.show();
                    getSearch();
                } else {
                    EasyToast.showShort(context, R.string.Networkexception);
                }
                break;
            default:
                break;
        }
    }


    /**
     * 登录获取
     */
    private void getSearch() {
        VolleyRequest.RequestGet(context, "https://restapi.amap.com/v3/place/text?keywords=" + keyword + "&city=" + city + "&output=json&offset=1&page=1&key=b40c90fb307c2002ea03d28da8b487e5&extensions=all", city, new VolleyInterface(context) {
            @Override
            public void onMySuccess(String result) {
                Log.e("LoginActivity", result);
                try {
                    dialog.dismiss();

                    if (result.contains("\"count\":\"0\"")) {
                        new CommomDialog(context, R.style.dialog, "暂无相关，请尝试更改关键词~", new CommomDialog.OnCloseListener() {
                            @Override
                            public void onClick(Dialog dialog, final boolean confirm) {
                                if (confirm) {
                                    dialog.dismiss();

                                } else {
                                    dialog.dismiss();
                                }
                            }
                        }).setTitle("检索完成").show();
                        return;
                    }

                    if (result.contains("[]")) {
                        Log.e("NewsListFragment", "has[]");
                        result = result.replace("[]", "暂无");
                    }
                    POIBean poiBean = new Gson().fromJson(result, POIBean.class);
                    if ("1".equals(poiBean.getStatus())) {
                        String count = poiBean.getCount();
                        Integer integer = Integer.parseInt(count);
                        if (integer > 0) {
                            new CommomDialog(context, R.style.dialog, "查询结果数：" + count + "户\n点击确认查看~", new CommomDialog.OnCloseListener() {
                                @Override
                                public void onClick(Dialog dialog, final boolean confirm) {
                                    if (confirm) {
                                        dialog.dismiss();
                                        startActivity(new Intent(context, ShopListActivity.class)
                                                .putExtra("keyword", keyword)
                                                .putExtra("city", city)
                                        );
                                    } else {
                                        dialog.dismiss();
                                    }
                                }
                            }).setTitle("检索成功").show();
                        } else {
                            new CommomDialog(context, R.style.dialog, "暂无相关，请尝试更改关键词~", new CommomDialog.OnCloseListener() {
                                @Override
                                public void onClick(Dialog dialog, final boolean confirm) {
                                    if (confirm) {
                                        dialog.dismiss();

                                    } else {
                                        dialog.dismiss();
                                    }
                                }
                            }).setTitle("检索完成").show();
                        }
                    } else {
                        EasyToast.showShort(context, "请联系管理员");
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onMyError(VolleyError error) {
                dialog.dismiss();
                error.printStackTrace();
            }
        });
    }

}
