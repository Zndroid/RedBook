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
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.hakz.www.redbook.R;
import com.hakz.www.redbook.fragment.BookCoverFragment;
import com.hakz.www.redbook.fragment.BookInfoItemFragment;
import com.hakz.www.redbook.fragment.BookIntroFragment;
import com.hakz.www.redbook.model.bean.Book;
import com.hakz.www.redbook.model.data.DataManager;

import org.json.JSONObject;
import org.litepal.crud.DataSupport;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class BookInfoAddActivity extends AppCompatActivity {
    private Context context;

    private TabLayout tab;
    private ViewPager viewPager;
    private RelativeLayout loadView;
    private RelativeLayout errorView;

    private List<Fragment> fragments = new ArrayList<>();
    private FragmentPagerAdapter adapter;

    private List<String> titles = Arrays.asList("基本信息", "图书简介");
    private String isbn;
    private Book book;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book_info_add);

        isbn = getIntent().getStringExtra("ISBN");
        context = this;
        initView();

        DataManager.getBookInfoFromISBN(isbn, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                loadView.setVisibility(View.GONE);
                book = DataManager.doubanBook2Book(DataManager.jsonObject2DoubanBook(response));

                Fragment bookCoverragment = BookCoverFragment.newInstance(book);
                getSupportFragmentManager().beginTransaction().add(R.id.fragment_book_cover, bookCoverragment).commit();

                fragments.add(BookInfoItemFragment.newInstance(book));
                fragments.add(BookIntroFragment.newInstance(book));
                adapter.notifyDataSetChanged();

                findViewById(R.id.btn_add).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        saveBook(book);
                    }
                });

                setTitle(book.getTitle());
            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(context, "图书不存在或网络连接错误", Toast.LENGTH_SHORT).show();
                loadView.setVisibility(View.GONE);
                errorView.setVisibility(View.VISIBLE);
            }
        });

    }

    private void initView() {
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
        setTitle("图书详情");

        loadView = (RelativeLayout) findViewById(R.id.loadView);
        errorView = (RelativeLayout) findViewById(R.id.errorView);

        tab = (TabLayout) findViewById(R.id.tab);

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

    }

    public void saveBook(Book book) {
        Boolean isAdded = false;

        // 遍历当前数据库中所有的书籍，用来判断是否已经添加过这本书
        List<Book> books = DataSupport.findAll(Book.class);
        for (int i = 0; i < books.size(); i++) {
            Book book_db = books.get(i);
            if ((book_db.getAuthor() + book_db.getTitle()).equals(book.getAuthor() + book.getTitle())) {
                isAdded = true;
                break;
            } else {
                isAdded = false;
            }
        }

        if (isAdded) {
            Toast.makeText(context, "你已经添加过了哦～", Toast.LENGTH_SHORT).show();
        } else {
            if (book.save()) {
                Toast.makeText(context, "保存成功", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(this, BookInfoActivity.class);
                intent.putExtra("id", book.getId());
                startActivity(intent);
            } else {
                Toast.makeText(context, "保存失败", Toast.LENGTH_SHORT).show();
            }
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
        menuItem.setVisible(false);
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                this.finish();
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
}
