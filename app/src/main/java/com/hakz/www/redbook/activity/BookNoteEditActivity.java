package com.hakz.www.redbook.activity;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.EditText;

import com.hakz.www.redbook.R;
import com.hakz.www.redbook.model.bean.Book;

import org.litepal.crud.DataSupport;

import java.text.SimpleDateFormat;
import java.util.Date;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class BookNoteEditActivity extends AppCompatActivity {

    private Context context;
    private Book book;
    private EditText etNote;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book_note_edit);

        context = this;

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        int itemId = getIntent().getIntExtra("id", -1);

        book = DataSupport.find(Book.class, itemId);

        setTitle("编辑笔记");

        etNote = (EditText) findViewById(R.id.et_note);
        etNote.setText(book.getNote());
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                this.finish();
                return true;
            case R.id.action_save:
                book.setNote(etNote.getText().toString().trim());
                book.setNote_date(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
                book.save();
                new SweetAlertDialog(context, SweetAlertDialog.SUCCESS_TYPE)
                        .setTitleText("保存成功")
                        .setContentText("笔记已保存")
                        .setConfirmText("确定")
                        .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                            @Override
                            public void onClick(SweetAlertDialog sweetAlertDialog) {
                                finish();
                            }
                        })
                        .show();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.book_note_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }
}
