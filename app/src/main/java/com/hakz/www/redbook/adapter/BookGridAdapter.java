package com.hakz.www.redbook.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.hakz.www.redbook.R;
import com.hakz.www.redbook.model.bean.Book;

import java.util.ArrayList;
import java.util.List;


public class BookGridAdapter extends BaseAdapter {

    private List<Book> list;
    private LayoutInflater inflater;
    Context context;

    public BookGridAdapter(Context context) {
        this.context = context;
        list = new ArrayList<>();
        inflater = LayoutInflater.from(context);
    }

    public void setData(List<Book> list) {
        this.list = list;
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return list.get(position).getId();
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        BookGridViewHolder viewHolder;
        if (convertView == null) {
            viewHolder = new BookGridViewHolder();
            convertView = inflater.inflate(R.layout.fragment_book_grid_item, null);
            viewHolder.ivCover = (ImageView) convertView.findViewById(R.id.iv_cover);
            viewHolder.tvTitle = (TextView) convertView.findViewById(R.id.tv_title);
            viewHolder.rbRate = (RatingBar) convertView.findViewById(R.id.rb_rate);
            viewHolder.tvRate = (TextView) convertView.findViewById(R.id.tv_rate);
            convertView.setTag(viewHolder);
        }

        viewHolder = (BookGridViewHolder) convertView.getTag();
        Book bean = list.get(position);

        // 设置图片
        Glide.with(viewHolder.ivCover.getContext())
                .load(bean.getImage())
                .centerCrop()
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(viewHolder.ivCover);

        // 设置其他
        viewHolder.tvTitle.setText(bean.getTitle());
        viewHolder.rbRate.setRating((Float.parseFloat(bean.getAverage())/2));
        viewHolder.tvRate.setText(bean.getAverage());

        return convertView;
    }

    public static class BookGridViewHolder {
        public ImageView ivCover;
        public TextView tvTitle;
        public RatingBar rbRate;
        public TextView tvRate;
    }
}


