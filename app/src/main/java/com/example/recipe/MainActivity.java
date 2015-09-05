package com.example.recipe;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.example.recipe.data.CategoryDataStore;
import com.example.recipe.ui.FeedsFragment;
import com.example.recipe.ui.CategoryFragment;
import com.example.recipe.ui.FavouriteFragment;
import com.example.recipe.ui.RecipeFragment;
import com.example.recipe.utility.Config;

import java.util.List;


public class MainActivity extends AppCompatActivity implements MainActivityListener {

    ViewPager mviewPager;
    TabLayout mTabLayout;
    SearchFragment mSearchFragment;
    CategoryDataStore mDataStore;

    private DrawerLayout mDrawer;
    private Toolbar toolbar;

    @Override
    public List<CategoryDataStore.CategoryDataItem> getData() {
        return   mDataStore.getAllData();
    }

    enum Pages {
        FEED,
        CATEGORIES,
        FAVOURITE
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mviewPager = (ViewPager) findViewById(R.id.pager);
        mTabLayout = (TabLayout) findViewById(R.id.tablayout);

        MyPagerAdapter pagerAdapter = new MyPagerAdapter(getSupportFragmentManager());
        mviewPager.setAdapter(pagerAdapter);
        mTabLayout.setupWithViewPager(mviewPager);

        mDataStore = new CategoryDataStore();

        // Set a Toolbar to replace the ActionBar.
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Find our drawer view
        mDrawer = (DrawerLayout) findViewById(R.id.drawer_layout);

        // Set the menu icon instead of the launcher icon.
        final ActionBar ab = getSupportActionBar();
        //ab.setHomeAsUpIndicator(R.drawable.ic_menu);
        ab.setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        // The action bar home/up action should open or close the drawer.
        switch (item.getItemId()) {
            case android.R.id.home:
                mDrawer.openDrawer(GravityCompat.START);
                return true;

            case R.id.action_search:
                handleSearchClick();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private class MyPagerAdapter extends FragmentStatePagerAdapter {

        public MyPagerAdapter(FragmentManager fm) {
            super(fm);
        }


        @Override
        public CharSequence getPageTitle(int position) {
            Pages page = Pages.values()[position];
            return page.name().toString();
        }

        @Override
        public Fragment getItem(int position) {
            Pages page = Pages.values()[position];
            Fragment fragment = null;
            switch (page){
                case FEED:{
                    fragment =  new FeedsFragment();
                    break;
                }
                case CATEGORIES:{
                    fragment =  new CategoryFragment();
                    break;
                }
                case FAVOURITE:{
                    fragment =  new FavouriteFragment();
                    break;
                }
            }
            return fragment;
        }

        @Override
        public int getCount() {
            return Pages.values().length;
        }
    }

    @Override
    public void showDetailView(String s) {
        Log.d("TAG", "showDetailView" + s);
        Bundle bundle = new Bundle();
        bundle.putString("Recipe", s);
        RecipeFragment rFrag = new RecipeFragment();
        rFrag.setArguments(bundle);
        getSupportFragmentManager().beginTransaction().add(
                R.id.full_screen_view, rFrag, "Detail Fragment").commit();
    }



    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
    }

    private void handleSearchClick() {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();

        if (mSearchFragment == null) {
            mSearchFragment = new SearchFragment();
        }

        if (mSearchFragment.isAdded()){
            transaction.show(mSearchFragment);
        } else {
            transaction.add(R.id.full_screen_view, mSearchFragment, "TAG").
                    addToBackStack("TAG");;
        }

        transaction.commit();
    }


}
