package com.szxb.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.szxb.model.PakageMod;
import com.szxb.util.MyUtils;
import com.szxb.view.R;

import java.util.List;

/**
 * Created by 斩断三千烦恼丝 on 2017/9/19.
 */

public class MyAdapter extends BaseAdapter {
    private List<PakageMod> ar;
    private Context con;
    private LayoutInflater inflater;

    public MyAdapter(Context con, List<PakageMod> list) {
        this.con = con;
        this.ar = list;
        inflater = LayoutInflater.from(con);
    }

    @Override
    public int getCount() {
        return ar.size();
    }

    @Override
    public Object getItem(int position) {
        return ar.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View v, ViewGroup parent) {
        ViewHolder vh;
        if (v == null) {
            v = inflater.inflate(R.layout.show_appp, null);
            vh = new ViewHolder();
            vh.img = (ImageView) v.findViewById(R.id.myimge);
            vh.tv = (TextView) v.findViewById(R.id.mytvs);
            v.setTag(vh);
        } else {
            vh = (ViewHolder) v.getTag();
        }

        if (ar.get(position).icon == null)
            MyUtils.Loge("icon is null");

        vh.img.setImageDrawable(ar.get(position).icon);
        vh.tv.setText(ar.get(position).appName);
        vh.tv.setTag(ar.get(position).pakageName);

        return v;
    }

    //刷新ar里面的数据
    public void updateData(List<PakageMod> ar) {
        this.ar = null;
        this.ar = ar;
        notifyDataSetChanged();
    }

    class ViewHolder {
        private ImageView img;
        private TextView tv;
    }
}
