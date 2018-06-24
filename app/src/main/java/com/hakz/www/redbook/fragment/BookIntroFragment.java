package com.hakz.www.redbook.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.hakz.www.redbook.R;
import com.hakz.www.redbook.adapter.BookIntroAdapter;
import com.hakz.www.redbook.model.bean.Book;
import com.hakz.www.redbook.model.bean.TagItem;

import org.litepal.crud.DataSupport;

import java.util.ArrayList;
import java.util.List;

public class BookIntroFragment extends Fragment {

    private static final String ARG_BOOK_ID = "book_id";
    private static final String ARG_BOOK = "book";

    private Book book;
    private List<TagItem> data;

    public static BookIntroFragment newInstance(int bookId) {
        BookIntroFragment fragment = new BookIntroFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_BOOK_ID, bookId);
        fragment.setArguments(args);
        return fragment;
    }

    public static BookIntroFragment newInstance(Book book) {
        BookIntroFragment fragment = new BookIntroFragment();
        Bundle args = new Bundle();
        args.putSerializable(ARG_BOOK, book);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            if (getArguments().containsKey(ARG_BOOK_ID)) {
                book = DataSupport.find(Book.class, getArguments().getInt(ARG_BOOK_ID));
            } else if (getArguments().containsKey(ARG_BOOK)) {
                book = (Book) getArguments().getSerializable(ARG_BOOK);
            }
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        ListView lv = (ListView) inflater.inflate(R.layout.fragment_book_intro_list, container, false);

        // 数据
        data = new ArrayList<>();
        if (!book.getSummary().isEmpty()) data.add(new TagItem("内容简介", book.getSummary()));
        if (!book.getAuthor_intro().isEmpty()) data.add(new TagItem("作者简介", book.getAuthor_intro()));
        if (!book.getCatalog().isEmpty()) data.add(new TagItem("图书目录", book.getCatalog()));

        // 列表适配器
        BookIntroAdapter lvBaseAdapter = new BookIntroAdapter(getContext(), data);

        // 列表
        lv.setAdapter(lvBaseAdapter);

        return lv;
    }
}
