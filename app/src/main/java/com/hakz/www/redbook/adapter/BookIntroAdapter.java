package com.hakz.www.redbook.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.hakz.www.redbook.R;
import com.hakz.www.redbook.model.bean.TagItem;

import java.util.List;



public class BookIntroAdapter extends BaseAdapter {
    private List<TagItem> list;
    private LayoutInflater inflater;
    private Context context;

    public BookIntroAdapter(Context context, List<TagItem> list) {
        this.context = context;
        this.list = list;
        inflater = LayoutInflater.from(context);
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
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        BookIntroViewHolder holder;
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.fragment_book_intro_item, null);
            holder = new BookIntroViewHolder();
            holder.tvTag = (TextView) convertView.findViewById(R.id.tag);
            holder.tvContent = (TextView) convertView.findViewById(R.id.content);
            convertView.setTag(holder);
        }

        holder = (BookIntroViewHolder) convertView.getTag();
        TagItem data = list.get(position);

        holder.tvTag.setText(data.getTag());
        holder.tvContent.setText(data.getContent());

        return convertView;
    }

    public static class BookIntroViewHolder {
        TextView tvTag;
        TextView tvContent;
    }
}

