package com.poisearchphone.Adapter;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.poisearchphone.Bean.POIBean;
import com.poisearchphone.R;

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

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        holder.tvShoptitle.setText("店铺名：" + datas.get(position).getName());
        holder.tvCity.setText("城市：" + datas.get(position).getPname() + datas.get(position).getCityname() + datas.get(position).getAdname());
        holder.tvAddress.setText("地址：" + datas.get(position).getAddress());
        holder.tvShoptel.setText("电话：" + datas.get(position).getTel());

        holder.tvCall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (!datas.get(position).getTel().contains("暂无")){
                    Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + datas.get(position).getTel()));
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    mContext.startActivity(intent);
                }

            }
        });


        holder.tvAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

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
        @BindView(R.id.ll_goods)
        LinearLayout llGoods;

        public ViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }

}
