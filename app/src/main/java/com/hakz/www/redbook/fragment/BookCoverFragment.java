package com.hakz.www.redbook.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.hakz.www.redbook.R;
import com.hakz.www.redbook.model.bean.Book;

import org.litepal.crud.DataSupport;

import jp.wasabeef.glide.transformations.BlurTransformation;


public class BookCoverFragment extends Fragment {

    private static final String ARG_BOOK_ID = "book_id";
    private static final String ARG_BOOK = "book";

    private Book book;

    public static BookCoverFragment newInstance(int bookId) {
        BookCoverFragment fragment = new BookCoverFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_BOOK_ID, bookId);
        fragment.setArguments(args);
        return fragment;
    }

    public static BookCoverFragment newInstance(Book book) {
        BookCoverFragment fragment = new BookCoverFragment();
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
        View view = inflater.inflate(R.layout.fragment_book_cover, container, false);

        ImageView ivBookCover = view.findViewById(R.id.book_cover);
        ImageView ivBookCoverBg =  view.findViewById(R.id.book_cover_bg);

        Glide.with(ivBookCover.getContext())
                .load(book.getImage())
                .centerCrop()
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(ivBookCover);

        // 背景
        Glide.with(ivBookCoverBg.getContext())
                .load(book.getImage())
                .centerCrop()
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .bitmapTransform(new BlurTransformation(ivBookCoverBg.getContext(), 25, 3))
                .into(ivBookCoverBg);

        TextView tvRate =  view.findViewById(R.id.tv_cover_rate);
        RatingBar rbRate =  view.findViewById(R.id.rb_cover_rate);

        // 图书评分
        tvRate.setText(book.getAverage());
        rbRate.setRating((Float.parseFloat(book.getAverage())/2));


        return view;
    }
}
