package com.poisearchphone.Activity;

import android.app.Dialog;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.provider.ContactsContract.CommonDataKinds.Email;
import android.provider.ContactsContract.CommonDataKinds.Phone;
import android.provider.ContactsContract.CommonDataKinds.StructuredName;
import android.provider.ContactsContract.Data;
import android.provider.ContactsContract.RawContacts;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.VolleyError;
import com.google.gson.Gson;
import com.poisearchphone.Adapter.ShopListAdapter;
import com.poisearchphone.Base.BaseActivity;
import com.poisearchphone.Bean.POIBean;
import com.poisearchphone.R;
import com.poisearchphone.Utils.EasyToast;
import com.poisearchphone.Utils.SpUtil;
import com.poisearchphone.Utils.Utils;
import com.poisearchphone.Utils.Validator;
import com.poisearchphone.View.ProgressView;
import com.poisearchphone.View.SakuraLinearLayoutManager;
import com.poisearchphone.View.WenguoyiRecycleView;
import com.poisearchphone.Volley.VolleyInterface;
import com.poisearchphone.Volley.VolleyRequest;

import java.io.File;
import java.io.RandomAccessFile;
import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import me.fangx.haorefresh.LoadMoreListener;

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
    @BindView(R.id.img_save)
    ImageView imgSave;
    @BindView(R.id.tv_count)
    TextView tvCount;
    @BindView(R.id.tv_youxiao)
    TextView tvYouxiao;
    @BindView(R.id.tv_daorucall)
    TextView tvDaorucall;
    @BindView(R.id.tv_showall)
    TextView tvShowall;
    @BindView(R.id.tv_daochutxt)
    TextView tvDaochutxt;
    @BindView(R.id.fl_save)
    FrameLayout flSave;
    private int p = 1;
    private SakuraLinearLayoutManager line;
    private String city;
    private String keyword;
    private Dialog dialog;
    public static boolean zuoji;
    private int youxiao = 0;

    @Override
    protected int setthislayout() {
        return R.layout.activity_shoplist_layout;
    }

    @Override
    protected void initview() {

        String psw = (String) SpUtil.get(context, "psw", "");
        String account = (String) SpUtil.get(context, "account", "");

        if (TextUtils.isEmpty(psw)) {
            startActivity(new Intent(context, LoginActivity.class));
        }

        if (TextUtils.isEmpty(account)) {
            startActivity(new Intent(context, LoginActivity.class));
        }

        city = getIntent().getStringExtra("city");
        keyword = getIntent().getStringExtra("keyword");
        zuoji = getIntent().getBooleanExtra("zuoji", false);

        if (!zuoji) {
            tvYouxiao.setVisibility(View.GONE);
            tvShowall.setText("排除座机");
        } else {
            tvShowall.setText("展示全部");
        }
        line = new SakuraLinearLayoutManager(context);
        line.setOrientation(LinearLayoutManager.VERTICAL);
        ceShiLv.setLayoutManager(line);
        ceShiLv.setItemAnimator(new DefaultItemAnimator());
        ProgressView progressView = new ProgressView(context);
        progressView.setIndicatorId(ProgressView.BallRotate);
        progressView.setIndicatorColor(getResources().getColor(R.color.colorAccent));
        ceShiLv.setFootLoadingView(progressView);
        ceShiLv.setLoadMoreListener(new LoadMoreListener() {
            @Override
            public void onLoadMore() {
                p = p + 1;
                getNewsList();
            }
        });
    }

    @Override
    protected void initListener() {
        rlBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        imgSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                flSave.setVisibility(View.VISIBLE);
            }
        });

        flSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                flSave.setVisibility(View.GONE);
            }
        });

        tvDaochutxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                flSave.setVisibility(View.GONE);
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                flSave.setVisibility(View.GONE);
                                dialog.show();
                            }
                        });
                        for (int i = 0; i < adapter.getDatas().size(); i++) {
                            if (!"暂无".equals(adapter.getDatas().get(i).getTel())) {
                                if (adapter.getDatas().get(i).getTel().contains(";")) {
                                    String[] split = adapter.getDatas().get(i).getTel().split(";");
                                    for (int i1 = 0; i1 < split.length; i1++) {
                                        if (zuoji) {
                                            if (Validator.isMobile(split[i1])) {
                                                writeData("商户：" + adapter.getDatas().get(i).getName() + "--电话：" + split[i1] + "--城市：" + adapter.getDatas().get(i).getPname() + adapter.getDatas().get(i).getCityname() + adapter.getDatas().get(i).getAdname() + "--地址：" + adapter.getDatas().get(i).getAddress());
                                            }
                                        } else {
                                            writeData("商户：" + adapter.getDatas().get(i).getName() + "--电话：" + split[i1] + "--城市：" + adapter.getDatas().get(i).getPname() + adapter.getDatas().get(i).getCityname() + adapter.getDatas().get(i).getAdname() + "--地址：" + adapter.getDatas().get(i).getAddress());
                                        }
                                        continue;
                                    }
                                } else {
                                    if (zuoji) {
                                        if (Validator.isMobile(adapter.getDatas().get(i).getTel())) {
                                            writeData("商户：" + adapter.getDatas().get(i).getName() + "--电话：" + adapter.getDatas().get(i).getTel() + "--城市：" + adapter.getDatas().get(i).getPname() + adapter.getDatas().get(i).getCityname() + adapter.getDatas().get(i).getAdname() + "--地址：" + adapter.getDatas().get(i).getAddress());
                                        }
                                    } else {
                                        writeData("商户：" + adapter.getDatas().get(i).getName() + "--电话：" + adapter.getDatas().get(i).getTel() + "--城市：" + adapter.getDatas().get(i).getPname() + adapter.getDatas().get(i).getCityname() + adapter.getDatas().get(i).getAdname() + "--地址：" + adapter.getDatas().get(i).getAddress());
                                    }
                                }
                            }
                        }

                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                dialog.dismiss();
                                EasyToast.showShort(context, "导出完成：目录 /sdcard/POISearch/Search.txt");
                            }
                        });

                    }
                }).start();


            }
        });

        tvDaorucall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                flSave.setVisibility(View.GONE);
                                dialog.show();
                            }
                        });
                        for (int i = 0; i < adapter.getDatas().size(); i++) {
                            if (!"暂无".equals(adapter.getDatas().get(i).getTel())) {
                                if (adapter.getDatas().get(i).getTel().contains(";")) {
                                    String[] split = adapter.getDatas().get(i).getTel().split(";");
                                    for (int i1 = 0; i1 < split.length; i1++) {
                                        if (zuoji) {
                                            if (Validator.isMobile(split[i1])) {
                                                addContact(adapter.getDatas().get(i).getName(), split[i1]);
                                            }
                                        } else {
                                            addContact(adapter.getDatas().get(i).getName(), split[i1]);
                                        }
                                        continue;
                                    }
                                } else {
                                    if (zuoji) {
                                        if (Validator.isMobile(adapter.getDatas().get(i).getTel())) {
                                            addContact(adapter.getDatas().get(i).getName(), adapter.getDatas().get(i).getTel());
                                        }
                                    } else {
                                        addContact(adapter.getDatas().get(i).getName(), adapter.getDatas().get(i).getTel());
                                    }

                                }
                            }
                        }

                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                dialog.dismiss();
                                EasyToast.showShort(context, "导入完成");
                            }
                        });

                    }
                }).start();
            }
        });

        tvShowall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (zuoji) {
                    tvShowall.setText("排除座机");
                    tvYouxiao.setVisibility(View.GONE);
                } else {
                    tvShowall.setText("展示全部");
                    tvYouxiao.setVisibility(View.VISIBLE);
                }
                zuoji = !zuoji;
                adapter.notifyDataSetChanged();
                flSave.setVisibility(View.GONE);
            }
        });

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

                    final POIBean finalPoiBean = poiBean;

                    for (int i = 0; i < finalPoiBean.getPois().size(); i++) {
                        if (!"暂无".equals(finalPoiBean.getPois().get(i).getTel())) {
                            if (finalPoiBean.getPois().get(i).getTel().contains(";")) {
                                String[] split = finalPoiBean.getPois().get(i).getTel().split(";");
                                for (int i1 = 0; i1 < split.length; i1++) {
                                    if (Validator.isMobile(split[i1])) {
                                        youxiao = youxiao + 1;
                                        break;
                                    }
                                }
                            } else {
                                if (Validator.isMobile(finalPoiBean.getPois().get(i).getTel())) {
                                    youxiao = youxiao + 1;
                                }
                            }
                        }
                    }

                    if (p == 1) {
                        adapter = new ShopListAdapter(ShopListActivity.this, finalPoiBean);
                        ceShiLv.setAdapter(adapter);
                        ceShiLv.setCanloadMore(true);
                    } else {
                        adapter.setDatas((ArrayList) finalPoiBean.getPois());
                        adapter.notifyDataSetChanged();
                    }

                    tvCount.setText("检索总数：" + adapter.getItemCount());

                    tvYouxiao.setText("筛选结果数：" + youxiao);

                    //ceShiLv.smoothScrollToPosition(adapter.getItemCount() - 1);
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

    // 一个添加联系人信息的例子
    public void addContact(String name, String phoneNumber) {
        // 创建一个空的ContentValues
        ContentValues values = new ContentValues();
        // 向RawContacts.CONTENT_URI空值插入，
        // 先获取Android系统返回的rawContactId
        // 后面要基于此id插入值
        Uri rawContactUri = getContentResolver().insert(RawContacts.CONTENT_URI, values);
        long rawContactId = ContentUris.parseId(rawContactUri);
        values.clear();
        values.put(ContactsContract.Data.RAW_CONTACT_ID, rawContactId);
        // 内容类型
        values.put(ContactsContract.Data.MIMETYPE, StructuredName.CONTENT_ITEM_TYPE);
        // 联系人名字
        values.put(StructuredName.GIVEN_NAME, "SAK" + name);
        // 向联系人URI添加联系人名字
        getContentResolver().insert(Data.CONTENT_URI, values);
        values.clear();

        values.put(ContactsContract.Data.RAW_CONTACT_ID, rawContactId);
        values.put(ContactsContract.Data.MIMETYPE, Phone.CONTENT_ITEM_TYPE);
        // 联系人的电话号码
        values.put(Phone.NUMBER, phoneNumber);
        // 电话类型
        values.put(Phone.TYPE, Phone.TYPE_MOBILE);
        // 向联系人电话号码URI添加电话号码
        getContentResolver().insert(Data.CONTENT_URI, values);
        values.clear();
        values.put(ContactsContract.Data.RAW_CONTACT_ID, rawContactId);
        values.put(ContactsContract.Data.MIMETYPE, Email.CONTENT_ITEM_TYPE);
        // 联系人的Email地址
        values.put(Email.DATA, "");
        // 电子邮件的类型
        values.put(Email.TYPE, Email.TYPE_WORK);
        // 向联系人Email URI添加Email数据
        getContentResolver().insert(Data.CONTENT_URI, values);
    }

    private void writeData(String txt) {
        String filePath = "/sdcard/POISearch/";
        String fileName = "Search.txt";
        writeTxtToFile(txt, filePath, fileName);
    }

    // 将字符串写入到文本文件中
    public void writeTxtToFile(String strcontent, String filePath, String fileName) {
        //生成文件夹之后，再生成文件，不然会出错
        makeFilePath(filePath, fileName);
        String strFilePath = filePath + fileName;
        // 每次写入时，都换行写
        String strContent = strcontent + "\r\n";
        try {
            File file = new File(strFilePath);
            if (!file.exists()) {
                Log.d("TestFile", "Create the file:" + strFilePath);
                file.getParentFile().mkdirs();
                file.createNewFile();
            }
            RandomAccessFile raf = new RandomAccessFile(file, "rwd");
            raf.seek(file.length());
            raf.write(strContent.getBytes());
            raf.close();
        } catch (Exception e) {
            Log.e("TestFile", "Error on write File:" + e);
        }
    }

    // 生成文件
    public File makeFilePath(String filePath, String fileName) {
        File file = null;
        makeRootDirectory(filePath);
        try {
            file = new File(filePath + fileName);
            if (!file.exists()) {
                file.createNewFile();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return file;
    }

    // 生成文件夹
    public static void makeRootDirectory(String filePath) {
        File file = null;
        try {
            file = new File(filePath);
            if (!file.exists()) {
                file.mkdir();
            }
        } catch (Exception e) {
            Log.i("error:", e + "");
        }
    }
}
