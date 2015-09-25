package com.example.recipe;

import android.os.Bundle;
import android.support.design.widget.NavigationView;
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
import android.util.SparseArray;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;

import com.example.recipe.data.DataUtility;
import com.example.recipe.data.DownloadAndUnzipFile;
import com.example.recipe.data.RecipeDescription;
import com.example.recipe.data.ShoppingListDataStore;
import com.example.recipe.ui.CategoryFragment;
import com.example.recipe.ui.FavouriteFragment;
import com.example.recipe.ui.FeedsFragment;
import com.example.recipe.ui.RecipeDetailFragment;
import com.example.recipe.ui.ShoppingListFragment;


public class MainActivity extends AppCompatActivity implements MainActivityListener {
    public static final String TAG = "MainActivity";
    ViewPager mviewPager;
    TabLayout mTabLayout;
    SearchFragment mSearchFragment;
    private DrawerLayout mDrawer;
    private Toolbar toolbar;
    ScreenSlidePagerAdapter mPagerAdapter;

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
        mPagerAdapter = new ScreenSlidePagerAdapter(getSupportFragmentManager());
        mviewPager.setAdapter(mPagerAdapter);


        mTabLayout = (TabLayout) findViewById(R.id.tablayout);
        mTabLayout.setupWithViewPager(mviewPager);

        // Set a Toolbar to replace the ActionBar.
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Find our drawer view
        mDrawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        NavigationView navigationView = (NavigationView) findViewById(R.id.nvView);
        navigationView.setNavigationItemSelectedListener(
                new OnNavigationItemSelectedListenerImpl());

        // Set the menu icon instead of the launcher icon.
        final ActionBar actionBar = getSupportActionBar();
        //ab.setHomeAsUpIndicator(R.drawable.ic_menu);
        actionBar.setDisplayHomeAsUpEnabled(true);
        Log.d(TAG, "onCreate ");

        String url = "http://virtualcook.parseapp.com/json/json.zip";
        String tempPath = DataUtility.getInstance(this).getExternalFilesDirPath() + "/json.zip";
        String finalPath =  DataUtility.getInstance(this).getExternalFilesDirPath()  + "/" + "json/";
        DownloadAndUnzipFile downloadAndUnzipFile = new DownloadAndUnzipFile(
                url, tempPath, finalPath);
        downloadAndUnzipFile.execute("DownloadAndUnzipFile");

    }

    private class OnNavigationItemSelectedListenerImpl implements
            NavigationView.OnNavigationItemSelectedListener {

        @Override
        public boolean onNavigationItemSelected(MenuItem menuItem) {
            int id = menuItem.getItemId();
            // The action bar home/up action should open or close the drawer.
            switch (menuItem.getItemId()) {
                case R.id.shoppingList:
                    mDrawer.closeDrawer(Gravity.LEFT);
                    showShoppingList();
                    return true;
            }

            return false;
        }
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (item.getItemId()) {
            case android.R.id.home:
                mDrawer.openDrawer(GravityCompat.START);
                return true;

            case R.id.action_settings :
                return true;

            case R.id.action_search:
                handleSearchClick();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void showShoppingList() {
        Log.d("TAG", "showShoppingList");
        ShoppingListDataStore store = ShoppingListDataStore.getInstance();
        Bundle bundle = new Bundle();
        bundle.putSerializable(ShoppingListDataStore.SHOPPING_IST_DATA_STORE_KEY, store);
        ShoppingListFragment sFrag = new ShoppingListFragment();
        sFrag.setArguments(bundle);
        getSupportFragmentManager().beginTransaction().add(
                R.id.full_screen_view, sFrag, "ShoppingList Fragment")
                .addToBackStack(ShoppingListFragment.class.getSimpleName())
                .commit();
    }

    public void showDetailView(RecipeDescription recipeDescription) {
        Log.d("TAG", "showDetailView");
        Bundle bundle = new Bundle();
        bundle.putSerializable(RecipeDetailFragment.RECIPE_DETAIL_KEY, recipeDescription);
        RecipeDetailFragment rFrag = new RecipeDetailFragment();
        rFrag.setArguments(bundle);
        getSupportFragmentManager().beginTransaction().add(
                R.id.full_screen_view, rFrag, "Detail Fragment")
                .addToBackStack(RecipeDetailFragment.class.getSimpleName())
                .commit();
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

    @Override
    public void onBackPressed() {
        boolean handled = false;
        Fragment fragment = mPagerAdapter.getRegisteredFragment(mviewPager.getCurrentItem());
        if (fragment instanceof CategoryFragment) {
            CategoryFragment cFragment = (CategoryFragment)fragment;
            handled = cFragment.onBackPressed();
        }

        if (!handled) {
            super.onBackPressed();
        }

    }

    private class ScreenSlidePagerAdapter extends FragmentStatePagerAdapter {
        SparseArray<Fragment> registeredFragments = new SparseArray<>();

        public ScreenSlidePagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public int getCount() {
            return Pages.values().length;
        }

        @Override
        public Fragment getItem(int position) {
            Pages page = Pages.values()[position];
            Fragment fragment = null;
            switch (page) {
                case FAVOURITE:
                    fragment = new FavouriteFragment();
                    break;
                case CATEGORIES:
                    fragment = new CategoryFragment();
                    break;


                case FEED:
                    fragment = new FeedsFragment();
                    break;
            }
            return fragment;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            Pages tabItemType = Pages.values()[position];
            return tabItemType.toString();
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            Fragment fragment =
                    (Fragment) super.instantiateItem(container, position);
            registeredFragments.put(position, fragment);
            return fragment;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            registeredFragments.remove(position);
            super.destroyItem(container, position, object);
        }

        public Fragment getRegisteredFragment(int position) {
            return registeredFragments.get(position);
        }
    }
}
