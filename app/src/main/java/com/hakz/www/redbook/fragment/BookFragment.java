package com.hakz.www.redbook.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import com.hakz.www.redbook.R;
import com.hakz.www.redbook.activity.BookInfoActivity;
import com.hakz.www.redbook.adapter.BookGridAdapter;
import com.hakz.www.redbook.model.bean.Book;
import com.mikepenz.iconics.IconicsDrawable;
import com.mikepenz.material_design_iconic_typeface_library.MaterialDesignIconic;

import org.litepal.crud.DataSupport;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class BookFragment extends Fragment {

    public static final int TYPE_ALL = 1;
    public static final int TYPE_FAVORITE = 2;

    private static final String ARG_TYPE = "type";
    private int type = TYPE_ALL;

    private GridView gridView;
    private BookGridAdapter adapter;
    private int gridPosition = -1;

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        if (this.isVisible()) {
            if (isVisibleToUser) {
                fetchData();
                adapter.notifyDataSetChanged();
            }
        }
        super.setUserVisibleHint(isVisibleToUser);
    }

    private void fetchData() {
        if (type == TYPE_FAVORITE) {
            adapter.setData(DataSupport.where("favourite = ?", "1").order("id desc").find(Book.class));
        } else {
            adapter.setData(DataSupport.order("id desc").find(Book.class));
        }
    }


    public static BookFragment newInstance(int type) {
        BookFragment fragment = new BookFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_TYPE, type);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            type = getArguments().getInt(ARG_TYPE);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_book, container, false);
        gridView = (GridView) view.findViewById(R.id.gridView);
        initGridView();
        View emptyView = view.findViewById(R.id.empty);
        ImageView ivIcon = (ImageView) emptyView.findViewById(R.id.iv_icon);
        TextView tvText = (TextView) emptyView.findViewById(R.id.tv_text);
        if (type == TYPE_FAVORITE) {
            ivIcon.setImageDrawable(new IconicsDrawable(getContext()).icon(MaterialDesignIconic
                    .Icon.gmi_favorite).colorRes(R.color.grid_empty_icon).sizeDp(48));
            tvText.setText("暂无收藏");
        } else {
            ivIcon.setImageDrawable(new IconicsDrawable(getContext()).icon(MaterialDesignIconic
                    .Icon.gmi_book).colorRes(R.color.grid_empty_icon).sizeDp(48));
            tvText.setText("暂无图书");
        }
        gridView.setEmptyView(emptyView);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        adapter = new BookGridAdapter(getActivity());
        fetchData();
        adapter.notifyDataSetChanged();
        gridView.setAdapter(adapter);
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public void onResume() {
        super.onResume();
        fetchData();
        adapter.notifyDataSetChanged();
    }

    private void initGridView() {
        registerForContextMenu(gridView);
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(getActivity(), BookInfoActivity.class);
                intent.putExtra("id", (int) adapter.getItemId(position));
                startActivity(intent);
            }
        });
        gridView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long
                    id) {
                gridPosition = position;
                return false;
            }
        });
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        menu.add(1, 1, 1, "删除选中");
        menu.add(1, 2, 1, "删除全部");
        super.onCreateContextMenu(menu, v, menuInfo);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        if (item.getItemId() == 1 && gridPosition != -1) {
            // 删除选中
            new SweetAlertDialog(getContext(), SweetAlertDialog.WARNING_TYPE)
                    .setTitleText("确定删除这本图书吗")
                    .setContentText("删除后将无法恢复。")
                    .setConfirmText("是的")
                    .setCancelText("取消")
                    .showCancelButton(true)
                    .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                        @Override
                        public void onClick(SweetAlertDialog sDialog) {
                            // 刷新数据
                            Handler handler = new Handler();
                            handler.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    DataSupport.delete(Book.class, adapter.getItemId(gridPosition));
                                    fetchData();
                                    adapter.notifyDataSetChanged();
                                }
                            }, 800);

                            sDialog
                                    .setTitleText("删除成功")
                                    .setContentText("该本图书已被成功删除。")
                                    .setConfirmText("确定")
                                    .showCancelButton(false)
                                    .setConfirmClickListener(null)
                                    .changeAlertType(SweetAlertDialog.SUCCESS_TYPE);
                        }
                    })
                    .show();
        } else if (item.getItemId() == 2) {
            // 删除全部
            new SweetAlertDialog(getContext(), SweetAlertDialog.WARNING_TYPE)
                    .setTitleText("确定删除全部图书吗")
                    .setContentText("删除后将无法恢复。")
                    .setConfirmText("是的")
                    .setCancelText("取消")
                    .showCancelButton(true)
                    .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                        @Override
                        public void onClick(SweetAlertDialog sDialog) {
                            // 刷新数据
                            Handler handler = new Handler();
                            handler.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    DataSupport.deleteAll(Book.class);
                                    fetchData();
                                    adapter.notifyDataSetChanged();
                                }
                            }, 1000);
                            sDialog
                                    .setTitleText("删除成功")
                                    .setContentText("全部图书已被成功删除。")
                                    .setConfirmText("确定")
                                    .showCancelButton(false)
                                    .setConfirmClickListener(null)
                                    .changeAlertType(SweetAlertDialog.SUCCESS_TYPE);
                        }
                    })
                    .show();
        } else {
            return super.onContextItemSelected(item);
        }
        return true;
    }
}
