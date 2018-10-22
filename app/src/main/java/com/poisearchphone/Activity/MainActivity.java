package com.poisearchphone.Activity;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.VolleyError;
import com.bigkoo.pickerview.OptionsPickerView;
import com.google.gson.Gson;
import com.poisearchphone.Base.BaseActivity;
import com.poisearchphone.Bean.JsonBean;
import com.poisearchphone.Bean.POIBean;
import com.poisearchphone.CommomDialog;
import com.poisearchphone.R;
import com.poisearchphone.Utils.EasyToast;
import com.poisearchphone.Utils.GetJsonDataUtil;
import com.poisearchphone.Utils.SpUtil;
import com.poisearchphone.Utils.Utils;
import com.poisearchphone.Volley.VolleyInterface;
import com.poisearchphone.Volley.VolleyRequest;

import org.json.JSONArray;
import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.x;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends BaseActivity implements View.OnClickListener {

    @BindView(R.id.et_city)
    EditText etCity;
    @BindView(R.id.et_keyword)
    EditText etKeyword;
    @BindView(R.id.btn_submit)
    Button btnSubmit;
    @BindView(R.id.cb_zuoji)
    CheckBox cbZuoji;
    private String keyword;
    private Dialog dialog;
    private ArrayList<JsonBean> options1Items = new ArrayList<>();
    private ArrayList<ArrayList<String>> options2Items = new ArrayList<>();
    private ArrayList<ArrayList<ArrayList<String>>> options3Items = new ArrayList<>();
    private Thread thread;
    private static final int MSG_LOAD_DATA = 0x0001;
    private static final int MSG_LOAD_SUCCESS = 0x0002;
    private static final int MSG_LOAD_FAILED = 0x0003;
    private boolean isLoaded = false;

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MSG_LOAD_DATA:
                    if (thread == null) {//如果已创建就不再重新创建子线程了
                        thread = new Thread(new Runnable() {
                            @Override
                            public void run() {
                                // 写子线程中的操作,解析省市区数据
                                initJsonData();
                            }
                        });
                        thread.start();
                    }
                    break;
                case MSG_LOAD_SUCCESS:
                    isLoaded = true;
                    break;
                case MSG_LOAD_FAILED:
                    break;
                default:
                    break;

            }
        }
    };

    public ArrayList<JsonBean> parseData(String result) {//Gson 解析
        ArrayList<JsonBean> detail = new ArrayList<>();
        try {
            JSONArray data = new JSONArray(result);
            Gson gson = new Gson();
            for (int i = 0; i < data.length(); i++) {
                JsonBean entity = gson.fromJson(data.optJSONObject(i).toString(), JsonBean.class);
                detail.add(entity);
            }
        } catch (Exception e) {
            e.printStackTrace();
            mHandler.sendEmptyMessage(MSG_LOAD_FAILED);
        }
        return detail;
    }

    private void initJsonData() {//解析数据

        /**
         * 注意：assets 目录下的Json文件仅供参考，实际使用可自行替换文件
         * 关键逻辑在于循环体
         *
         * */
        String JsonData = new GetJsonDataUtil().getJson(this, "province.json");//获取assets目录下的json文件数据

        ArrayList<JsonBean> jsonBean = parseData(JsonData);//用Gson 转成实体

        /**
         * 添加省份数据
         *
         * 注意：如果是添加的JavaBean实体，则实体类需要实现 IPickerViewData 接口，
         * PickerView会通过getPickerViewText方法获取字符串显示出来。
         */
        options1Items = jsonBean;

        for (int i = 0; i < jsonBean.size(); i++) {//遍历省份
            ArrayList<String> CityList = new ArrayList<>();//该省的城市列表（第二级）
            ArrayList<ArrayList<String>> Province_AreaList = new ArrayList<>();//该省的所有地区列表（第三极）

            for (int c = 0; c < jsonBean.get(i).getCityList().size(); c++) {//遍历该省份的所有城市
                String CityName = jsonBean.get(i).getCityList().get(c).getName();
                CityList.add(CityName);//添加城市

                ArrayList<String> City_AreaList = new ArrayList<>();//该城市的所有地区列表

                //如果无地区数据，建议添加空字符串，防止数据为null 导致三个选项长度不匹配造成崩溃
                if (jsonBean.get(i).getCityList().get(c).getArea() == null
                        || jsonBean.get(i).getCityList().get(c).getArea().size() == 0) {
                    City_AreaList.add("");
                } else {

                    for (int d = 0; d < jsonBean.get(i).getCityList().get(c).getArea().size(); d++) {//该城市对应地区所有数据
                        String AreaName = jsonBean.get(i).getCityList().get(c).getArea().get(d);

                        City_AreaList.add(AreaName);//添加该城市所有地区数据
                    }
                }
                Province_AreaList.add(City_AreaList);//添加该省所有地区数据
            }

            /**
             * 添加城市数据
             */
            options2Items.add(CityList);

            /**
             * 添加地区数据
             */
            // options3Items.add(Province_AreaList);
        }
        mHandler.sendEmptyMessage(MSG_LOAD_SUCCESS);

    }

    private String province;
    private String city;

    private void ShowPickerView() {// 弹出选择器
        if (!options1Items.isEmpty()) {
            OptionsPickerView pvOptions = new OptionsPickerView.Builder(this, new OptionsPickerView.OnOptionsSelectListener() {
                @Override
                public void onOptionsSelect(int options1, int options2, int options3, View v) {
                    //返回的分别是三个级别的选中位置
                    province = options1Items.get(options1).getPickerViewText();
                    city = options2Items.get(options1).get(options2);
                    etCity.setText(city);
                }
            })
                    .setTitleBgColor(getResources().getColor(R.color.pressedColor))
                    .setCancelColor(getResources().getColor(R.color.text))
                    .setSubmitColor(getResources().getColor(R.color.text))
                    .setTitleText("选择城市")
                    .setSelectOptions(15)//默认选中项
                    .setTitleColor(getResources().getColor(R.color.text))
                    .setDividerColor(Color.BLACK)
                    .setTextColorCenter(Color.BLACK) //设置选中项文字颜色
                    .setContentTextSize(20)
                    .build();

            //pvOptions.setPicker(options1Items);//一级选择器
            pvOptions.setPicker(options1Items, options2Items);//二级选择器*/
            //   pvOptions.setPicker(options1Items, options2Items, options3Items);//三级选择器
            pvOptions.show();
        }

    }


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
        mHandler.sendEmptyMessage(MSG_LOAD_DATA);
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

                ShowPickerView();

                final String psw = (String) SpUtil.get(context, "psw", "");

                RequestParams params = new RequestParams("http://43.251.116.250:8080/sakura.txt");
                x.http().get(params, new Callback.CommonCallback<String>() {
                    @Override
                    public void onSuccess(String result) {
                        if (result.contains(psw)) {
                            String[] split = result.split("#");
                            for (int i = 0; i < split.length; i++) {
                                String s = split[i].toString();
                                Log.e("aaaa", "onSuccess: " + s);
                                if (split[i].contains(psw)) {
                                    Log.e("aaaa", "true: " + s);
                                }
                            }
                        } else {
                            EasyToast.showShort(context, "注册码已失效,请联系管理员");
                            return;
                        }
                    }

                    @Override
                    public void onError(Throwable ex, boolean isOnCallback) {
                        Toast.makeText(x.app(), ex.getMessage(), Toast.LENGTH_LONG).show();
                    }

                    @Override
                    public void onCancelled(CancelledException cex) {
                        Toast.makeText(x.app(), "cancelled", Toast.LENGTH_LONG).show();
                    }

                    @Override
                    public void onFinished() {

                    }
                });


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
                                                .putExtra("zuoji", cbZuoji.isChecked())
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
