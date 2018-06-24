package com.hakz.www.redbook.activity;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.hakz.www.redbook.R;
import com.hakz.www.redbook.fragment.BookCoverFragment;
import com.hakz.www.redbook.fragment.BookInfoItemFragment;
import com.hakz.www.redbook.fragment.BookIntroFragment;
import com.hakz.www.redbook.fragment.BookNoteFragment;
import com.hakz.www.redbook.model.bean.Book;
import com.mikepenz.iconics.context.IconicsContextWrapper;

import org.litepal.crud.DataSupport;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class BookInfoActivity extends AppCompatActivity {
    private Context context;

    private TabLayout tab;
    private ViewPager viewPager;

    private List<Fragment> fragments = new ArrayList<>();
    private FragmentPagerAdapter adapter;

    private List<String> titles = Arrays.asList("基本信息", "图书简介", "我的笔记");
    private int bookId;
    private Book book;

    private int iconFavorite[] = {R.drawable.ic_favorite_border_white_24dp, R.drawable.ic_favorite_white_24dp};

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(IconicsContextWrapper.wrap(newBase));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book_info);

        bookId = getIntent().getIntExtra("id", -1);
        book = DataSupport.find(Book.class, bookId);
        context = this;
        initView();
    }

    private void initView() {
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        setTitle(book.getTitle());

        tab = (TabLayout) findViewById(R.id.tab);

        fragments.add(BookInfoItemFragment.newInstance(book));
        fragments.add(BookIntroFragment.newInstance(book));
        fragments.add(BookNoteFragment.newInstance(bookId));

        viewPager = (ViewPager) findViewById(R.id.view_pager);
        viewPager.setAdapter(adapter = new  FragmentPagerAdapter(getSupportFragmentManager()) {
            @Override
            public Fragment getItem(int position) {
                return fragments.get(position);
            }

            @Override
            public int getCount() {
                return fragments.size();
            }

            @Override
            public CharSequence getPageTitle(int position) {
                return titles.get(position);
            }
        });
        tab.setupWithViewPager(viewPager);

        Fragment bookCoverragment = BookCoverFragment.newInstance(bookId);
        getSupportFragmentManager().beginTransaction().add(R.id.fragment_book_cover, bookCoverragment).commit();

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
            case R.id.action_favorite:
                book.setFavourite(!book.isFavourite());
                book.save();
                invalidateOptionsMenu();
                new SweetAlertDialog(context, SweetAlertDialog.SUCCESS_TYPE)
                        .setTitleText(book.isFavourite() ? "收藏成功" : "取消收藏")
                        .setContentText(book.isFavourite() ? "图书已收藏" : "图书已取消收藏")
                        .setConfirmText("确定")
                        .show();
                return true;
            case R.id.action_browser:
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse(book.getAlt()));
                startActivity(intent);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.book_info_menu, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        MenuItem menuItem = menu.findItem(R.id.action_favorite);
        menuItem.setIcon(iconFavorite[book.isFavourite() ? 1 : 0]);
        return super.onPrepareOptionsMenu(menu);
    }
}
