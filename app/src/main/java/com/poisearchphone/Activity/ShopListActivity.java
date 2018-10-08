package com.poisearchphone.Activity;

import android.app.Dialog;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.VolleyError;
import com.google.gson.Gson;
import com.poisearchphone.Adapter.ShopListAdapter;
import com.poisearchphone.Base.BaseActivity;
import com.poisearchphone.Bean.POIBean;
import com.poisearchphone.R;
import com.poisearchphone.Utils.EasyToast;
import com.poisearchphone.Utils.Utils;
import com.poisearchphone.View.ProgressView;
import com.poisearchphone.View.SakuraLinearLayoutManager;
import com.poisearchphone.View.WenguoyiRecycleView;
import com.poisearchphone.Volley.VolleyInterface;
import com.poisearchphone.Volley.VolleyRequest;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * com.lingqiapp.Activity
 *
 * @author 赵磊
 * @date 2018/9/15
 * 功能描述：
 */
public class ShopListActivity extends BaseActivity {
    ShopListAdapter adapter;
    @BindView(R.id.ce_shi_lv)
    WenguoyiRecycleView ceShiLv;
    @BindView(R.id.rl_back)
    FrameLayout rlBack;
    @BindView(R.id.tv_title)
    TextView tvTitle;
    private int p = 1;
    private SakuraLinearLayoutManager line;
    private String city;
    private String keyword;
    private Dialog dialog;

    @Override
    protected int setthislayout() {
        return R.layout.activity_shoplist_layout;
    }

    @Override
    protected void initview() {
        city = getIntent().getStringExtra("city");
        keyword = getIntent().getStringExtra("keyword");
        line = new SakuraLinearLayoutManager(context);
        line.setOrientation(LinearLayoutManager.VERTICAL);
        ceShiLv.setLayoutManager(line);
        ceShiLv.setItemAnimator(new DefaultItemAnimator());
        ProgressView progressView = new ProgressView(context);
        progressView.setIndicatorId(ProgressView.BallRotate);
        progressView.setIndicatorColor(getResources().getColor(R.color.colorAccent));
        ceShiLv.setFootLoadingView(progressView);
    }

    @Override
    protected void initListener() {

        rlBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        tvTitle.setText(city + "-" + keyword);

    }

    @Override
    protected void initData() {
        if (Utils.isConnected(context)) {
            dialog = Utils.showLoadingDialog(context);
            dialog.show();
            getNewsList();
        } else {
            EasyToast.showShort(context, R.string.Networkexception);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // TODO: add setContentView(...) invocation
        ButterKnife.bind(this);
    }

    /**
     * 新闻列表获取
     */
    private void getNewsList() {
        VolleyRequest.RequestGet(context, "https://restapi.amap.com/v3/place/text?keywords=" + keyword + "&city=" + city + "&output=json&offset=50&page=" + p + "&key=b40c90fb307c2002ea03d28da8b487e5&extensions=all", city, new VolleyInterface(context) {
            @Override
            public void onMySuccess(String result) {
                try {
                    dialog.dismiss();
                    Log.e("NewsListFragment", result.toString());
                    if (result.contains("[]")) {
                        Log.e("NewsListFragment", "has[]");
                        result = result.replace("[]", "暂无");
                    }
                    Log.e("NewsListFragment", result.toString());

                    if (result.contains("\"count\":\"0\"")) {
                        if (p != 1) {
                            p = p - 1;
                            Toast.makeText(context, "没有更多了", Toast.LENGTH_SHORT).show();
                        } else {

                        }
                        ceShiLv.setCanloadMore(false);
                        ceShiLv.loadMoreEnd();
                        return;
                    }

                    POIBean poiBean = new Gson().fromJson(result, POIBean.class);
                    if (ceShiLv != null) {
                        ceShiLv.setEnabled(true);
                        ceShiLv.loadMoreComplete();
                        ceShiLv.setCanloadMore(true);
                    }
                    if (p == 1) {
                        adapter = new ShopListAdapter(ShopListActivity.this, poiBean);
                        ceShiLv.setAdapter(adapter);
                        if (poiBean.getPois().size() < 10) {
                            ceShiLv.setCanloadMore(false);
                            ceShiLv.loadMoreEnd();
                        } else {
                            ceShiLv.setCanloadMore(true);
                        }
                    } else {
                        adapter.setDatas((ArrayList) poiBean.getPois());
                    }

                    ceShiLv.smoothScrollToPosition(adapter.getItemCount() - 1);

                    p = p + 1;
                    initData();
                    poiBean = null;
                    result = null;
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
