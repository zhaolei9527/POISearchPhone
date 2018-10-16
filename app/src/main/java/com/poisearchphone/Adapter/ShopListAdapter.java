package com.poisearchphone.Adapter;

import android.app.Activity;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Intent;
import android.net.Uri;
import android.provider.ContactsContract.CommonDataKinds.Email;
import android.provider.ContactsContract.CommonDataKinds.Phone;
import android.provider.ContactsContract.CommonDataKinds.StructuredName;
import android.provider.ContactsContract.Data;
import android.provider.ContactsContract.RawContacts;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.poisearchphone.Activity.ShopListActivity;
import com.poisearchphone.Bean.POIBean;
import com.poisearchphone.R;
import com.poisearchphone.Utils.EasyToast;
import com.poisearchphone.Utils.Validator;

import java.io.File;
import java.io.RandomAccessFile;
import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * com.wenguoyi.Adapter
 *
 * @author 赵磊
 * @date 2018/5/15
 * 功能描述：首页商品列表适配器，包括了头部，轮播，和列表
 */
public class ShopListAdapter extends RecyclerView.Adapter<ShopListAdapter.ViewHolder> {

    private Activity mContext;

    private ArrayList<POIBean.PoisBean> datas = new ArrayList();

    public ArrayList<POIBean.PoisBean> getDatas() {
        return datas;
    }

    public ShopListAdapter(Activity context, POIBean poiBean) {
        this.mContext = context;
        datas.addAll(poiBean.getPois());
    }

    public void setDatas(ArrayList<POIBean.PoisBean> datas) {
        this.datas.addAll(datas);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_home_shop_layout, parent, false);
        ViewHolder vp = new ViewHolder(view);
        return vp;
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        String[] split = datas.get(position).getTel().split(";");
        if (ShopListActivity.zuoji) {
            for (int i = 0; i < split.length; i++) {
                if (datas.get(position).getTel().contains("暂无") || !Validator.isMobile(split[i])) {
                    ViewGroup.LayoutParams layoutParams = holder.llGoods.getLayoutParams();
                    layoutParams.height = 0;
                    holder.llGoods.setLayoutParams(layoutParams);
                } else {
                    holder.llGoods.measure(0, 0);
                    holder.tvShoptitle.setText("店铺名：" + datas.get(position).getName());
                    holder.tvNum.setText(String.valueOf(position));
                    holder.tvCity.setText("城市：" + datas.get(position).getPname() + datas.get(position).getCityname() + datas.get(position).getAdname());
                    holder.tvAddress.setText("地址：" + datas.get(position).getAddress());
                    if (Validator.isMobile(split[i])) {
                        holder.tvShoptel.setText("电话：" + split[i]);
                    } else {
                        holder.tvShoptel.setText("电话：" + split[0]);
                    }
                    holder.tvCall.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            if (!datas.get(position).getTel().contains("暂无")) {
                                Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + datas.get(position).getTel()));
                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                mContext.startActivity(intent);
                            }
                        }
                    });
                    holder.tvAdd.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            addContact(datas.get(position).getName(), datas.get(position).getTel());
                            EasyToast.showShort(mContext, "添加成功");
                        }
                    });
                }
            }
        } else {
            holder.llGoods.measure(0, 0);
            holder.tvNum.setText(String.valueOf(position));
            holder.tvShoptitle.setText("店铺名：" + datas.get(position).getName());
            holder.tvCity.setText("城市：" + datas.get(position).getPname() + datas.get(position).getCityname() + datas.get(position).getAdname());
            holder.tvAddress.setText("地址：" + datas.get(position).getAddress());
            for (int i1 = 0; i1 < split.length; i1++) {
                if (Validator.isMobile(split[i1])) {
                    holder.tvShoptel.setText("电话：" + split[i1]);
                } else {
                    holder.tvShoptel.setText("电话：" + split[0]);
                }
            }
            holder.tvCall.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (!datas.get(position).getTel().contains("暂无")) {
                        Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + datas.get(position).getTel()));
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        mContext.startActivity(intent);
                    }
                }
            });

            holder.tvAdd.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    addContact(datas.get(position).getName(), datas.get(position).getTel());
                    EasyToast.showShort(mContext, "添加成功");
                }
            });
        }

    }

    @Override
    public int getItemCount() {
        return datas.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.tv_shoptitle)
        TextView tvShoptitle;
        @BindView(R.id.tv_shoptel)
        TextView tvShoptel;
        @BindView(R.id.tv_city)
        TextView tvCity;
        @BindView(R.id.tv_address)
        TextView tvAddress;
        @BindView(R.id.tv_add)
        TextView tvAdd;
        @BindView(R.id.tv_call)
        TextView tvCall;
        @BindView(R.id.tv_num)
        TextView tvNum;
        @BindView(R.id.ll_goods)
        FrameLayout llGoods;

        public ViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }

    // 一个添加联系人信息的例子
    public void addContact(String name, String phoneNumber) {
        // 创建一个空的ContentValues
        ContentValues values = new ContentValues();
        // 向RawContacts.CONTENT_URI空值插入，
        // 先获取Android系统返回的rawContactId
        // 后面要基于此id插入值
        Uri rawContactUri = mContext.getContentResolver().insert(RawContacts.CONTENT_URI, values);
        long rawContactId = ContentUris.parseId(rawContactUri);
        values.clear();
        values.put(Data.RAW_CONTACT_ID, rawContactId);
        // 内容类型
        values.put(Data.MIMETYPE, StructuredName.CONTENT_ITEM_TYPE);
        // 联系人名字
        values.put(StructuredName.GIVEN_NAME, name);
        // 向联系人URI添加联系人名字
        mContext.getContentResolver().insert(Data.CONTENT_URI, values);
        values.clear();
        values.put(Data.RAW_CONTACT_ID, rawContactId);
        values.put(Data.MIMETYPE, Phone.CONTENT_ITEM_TYPE);
        // 联系人的电话号码
        values.put(Phone.NUMBER, phoneNumber);
        // 电话类型
        values.put(Phone.TYPE, Phone.TYPE_MOBILE);
        // 向联系人电话号码URI添加电话号码
        mContext.getContentResolver().insert(Data.CONTENT_URI, values);
        values.clear();
        values.put(Data.RAW_CONTACT_ID, rawContactId);
        values.put(Data.MIMETYPE, Email.CONTENT_ITEM_TYPE);
        // 联系人的Email地址
        values.put(Email.DATA, "");
        // 电子邮件的类型
        values.put(Email.TYPE, Email.TYPE_WORK);
        // 向联系人Email URI添加Email数据
        mContext.getContentResolver().insert(Data.CONTENT_URI, values);
    }


    private void writeData() {
        String filePath = "/sdcard/Test/";
        String fileName = "log.txt";

        writeTxtToFile("txt content", filePath, fileName);
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
