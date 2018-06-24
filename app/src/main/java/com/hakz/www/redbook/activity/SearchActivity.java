package com.hakz.www.redbook.activity;

import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.hakz.www.redbook.R;
import com.hakz.www.redbook.adapter.BookRecyclerAdapter;
import com.hakz.www.redbook.app.MyApplication;
import com.hakz.www.redbook.model.bean.Book;
import com.hakz.www.redbook.model.data.DataManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.litepal.crud.DataSupport;

import java.util.List;

public class SearchActivity extends AppCompatActivity {

    public static int SEARCH_LOCAL = 0;
    public static int SEARCH_NET = 1;

    private int search_type = SEARCH_LOCAL;

    private EditText etSearch;
    private SwipeRefreshLayout srl;
    private BookRecyclerAdapter adapter;
    private LinearLayoutManager manager;

    private String searchBookName;
    private int total = 10;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        search_type = getIntent().getIntExtra("search_type", SEARCH_NET);

        initView();
    }

    private void initView() {
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setElevation(0);
        }

        setTitle(search_type == SEARCH_LOCAL ? "查找" : "搜索");
        etSearch = (EditText) findViewById(R.id.et_search);
        etSearch.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH || (event != null && event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) {
                    click();
                    return true;
                }
                return false;
            }
        });

        findViewById(R.id.ib_search).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                click();
            }
        });
        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.fragment_search_book_recycler);
        manager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(manager);
        adapter = new BookRecyclerAdapter(this);
        recyclerView.setAdapter(adapter);

        srl = (SwipeRefreshLayout) findViewById(R.id.fragment_search_book_swipe);
        srl.setColorSchemeResources(R.color.google_blue, R.color.google_red, R.color.google_green, R.color.google_yellow);
        srl.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                search();
            }
        });

        recyclerView.setOnScrollListener(new RecyclerView.OnScrollListener() {
            boolean isSlidingToLast = false;

            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                    int lastVisibleItem = manager.findLastCompletelyVisibleItemPosition();
                    int totalItemCount = manager.getItemCount();
                    if (lastVisibleItem == (totalItemCount - 1) && isSlidingToLast) {
                        if (!srl.isRefreshing() && adapter.getItemCount() < total) {
                            srl.setRefreshing(true);
                            get();
                        }
                    }
                }
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if (dx > 0) {
                    isSlidingToLast = false;
                } else {
                    isSlidingToLast = true;
                }
            }
        });
    }

    private void click() {
        closeKeyboard();
        search();
    }

    private void search() {
        if (!etSearch.getText().toString().trim().isEmpty()) {
            searchBookName = etSearch.getText().toString().trim().replace(" ", "\b");
            adapter.clear();
            adapter.notifyDataSetChanged();
            startLoadingAnim();
            get();
        } else {
            Toast.makeText(this, "请输入要搜索的内容", Toast.LENGTH_SHORT).show();
            srl.setRefreshing(false);
        }
    }

    private void get() {
        if (search_type == SEARCH_LOCAL) {
            List<Book> books = DataSupport.where("title like ?", "%" + searchBookName + "%").find(Book.class);
            adapter.setData(books);
            adapter.notifyDataSetChanged();
            srl.setRefreshing(false);
            stopLoadingAnim();

            if (books.size() == 0) {
                Toast.makeText(MyApplication.getContext(), "找不到图书", Toast.LENGTH_SHORT).show();
            }
        } else if (search_type == SEARCH_NET) {
            if (adapter.getItemCount() < total) {
                DataManager.getBookSearch(searchBookName, adapter.getItemCount(), new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            total = response.getInt("total");
                            JSONArray array = response.getJSONArray("books");
                            if (total == 0) {
                                Toast.makeText(MyApplication.getContext(), "找不到图书", Toast.LENGTH_SHORT).show();
                            }
                            for (int j = 0; j < array.length(); j++) {
                                Book data = DataManager.doubanBook2Book(DataManager.jsonObject2DoubanBook(array.getJSONObject(j)));
                                adapter.add(data);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        adapter.notifyDataSetChanged();
                        srl.setRefreshing(false);
                        stopLoadingAnim();
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(MyApplication.getContext(), "搜索失败，请检查网络连接", Toast.LENGTH_SHORT).show();
                        srl.setRefreshing(false);
                        stopLoadingAnim();
                    }
                });
            }
        }
    }

    private void startLoadingAnim() {
        findViewById(R.id.loadView).setVisibility(View.VISIBLE);
    }

    private void stopLoadingAnim() {
        findViewById(R.id.loadView).setVisibility(View.GONE);
    }

    private void closeKeyboard() {
        etSearch.clearFocus();

        // 关闭输入法
        ((InputMethodManager) getSystemService(INPUT_METHOD_SERVICE))
                .hideSoftInputFromWindow(SearchActivity.this.getCurrentFocus().getWindowToken(),
                        InputMethodManager.HIDE_NOT_ALWAYS);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                this.finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
