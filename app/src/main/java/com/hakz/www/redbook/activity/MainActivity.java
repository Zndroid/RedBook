package com.hakz.www.redbook.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.LayoutInflaterCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;

import com.github.clans.fab.FloatingActionButton;
import com.github.clans.fab.FloatingActionMenu;
import com.hakz.www.redbook.R;
import com.hakz.www.redbook.fragment.BookFragment;
import com.mikepenz.iconics.context.IconicsContextWrapper;
import com.mikepenz.iconics.context.IconicsLayoutInflater;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements NavigationView
        .OnNavigationItemSelectedListener {

    private FloatingActionMenu fabMenu;
    private DrawerLayout drawer;
    private ActionBarDrawerToggle toggle;
    private ViewPager viewPager;
    private TabLayout tab;
    private List<Fragment> fragments = new ArrayList<Fragment>();

    private String[] tabNames = {"全部", "收藏"};


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        LayoutInflaterCompat.setFactory(getLayoutInflater(), new IconicsLayoutInflater
                (getDelegate()));
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initView();
        initFlot();
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(IconicsContextWrapper.wrap(newBase));
    }

    private void initFlot() {
        fabMenu = (FloatingActionMenu) findViewById(R.id.fabmenu);
        fabMenu.setClosedOnTouchOutside(true);

        final FloatingActionButton fabBtnScanner = (FloatingActionButton) findViewById(R.id
                .fab_scanner);
        fabBtnScanner.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                fabMenu.close(true);
                Intent intent = new Intent(MainActivity.this, ScannerActivity.class);
                startActivity(intent);
            }
        });

        FloatingActionButton fabBtnAdd = (FloatingActionButton) findViewById(R.id.fab_add);
        fabBtnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                fabMenu.close(true);
                Intent intent = new Intent(MainActivity.this, SearchActivity.class);
                startActivity(intent);
            }
        });

    }

    private void initView() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, 0, 0);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        ((NavigationView) findViewById(R.id.nav_view)).setNavigationItemSelectedListener(this);

        tab = (TabLayout) findViewById(R.id.tab);

        fragments.add(BookFragment.newInstance(BookFragment.TYPE_ALL));
        fragments.add(BookFragment.newInstance(BookFragment.TYPE_FAVORITE));

        viewPager = (ViewPager) findViewById(R.id.view_pager);
        viewPager.setAdapter(new FragmentPagerAdapter(getSupportFragmentManager()) {
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
                return tabNames[position];
            }
        });
        tab.setupWithViewPager(viewPager);
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.nav_scanner:
                startActivity(new Intent(this, ScannerActivity.class));
                break;
            case R.id.nav_add:
                startActivity(new Intent(MainActivity.this, SearchActivity.class).putExtra("search_type", SearchActivity.SEARCH_NET));
                break;
            case R.id.nav_about:
                startActivity(new Intent(this, AboutActivity.class));
                break;
            case R.id.nav_search:
                startActivity(new Intent(MainActivity.this, SearchActivity.class).putExtra("search_type", SearchActivity.SEARCH_LOCAL));
                break;
        }
        drawer.closeDrawers();
        return true;
    }

    @Override
    public void onBackPressed() {
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawers();
        } else {
            if (fabMenu.isOpened()) {
                fabMenu.close(true);
            } else {
                super.onBackPressed();
            }
        }
    }
}
