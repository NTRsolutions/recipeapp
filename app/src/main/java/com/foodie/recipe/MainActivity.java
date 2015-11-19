package com.foodie.recipe;

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
import android.widget.Button;

import com.foodie.recipe.data.AnalyticsHandler;
import com.foodie.recipe.data.RecipeDataStore;
import com.foodie.recipe.data.ShoppingListDataStore;
import com.foodie.recipe.rateus.AppRate;
import com.foodie.recipe.ui.BrowseFragment;
import com.foodie.recipe.ui.CategoryFragment;
import com.foodie.recipe.ui.RecipeDetailFragment;
import com.foodie.recipe.ui.RecipeListFragment;
import com.foodie.recipe.ui.ShoppingListFragment;
import com.foodie.recipe.utility.AppPreference;
import com.tapjoy.Tapjoy;


public class MainActivity extends AppCompatActivity implements MainActivityListener {
    public static final String TAG = "MainActivity";
    ViewPager mviewPager;
    TabLayout mTabLayout;
    SearchFragment mSearchFragment;
    private DrawerLayout mDrawer;
    private Toolbar toolbar;
    ScreenSlidePagerAdapter mPagerAdapter;
    private Button mButton ;
    public enum Pages {
        RECOMMENDED,
        CATEGORIES,
        FAVOURITE,
        RECENT,
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        AnalyticsHandler.getInstance(this).sendScreenName(this.getClass().getSimpleName());
        mviewPager = (ViewPager) findViewById(R.id.pager);
        mPagerAdapter = new ScreenSlidePagerAdapter(getSupportFragmentManager());
        mviewPager.setAdapter(mPagerAdapter);



        mTabLayout = (TabLayout) findViewById(R.id.tablayout);
        mTabLayout.setupWithViewPager(mviewPager);

        // Set a Toolbar to replace the ActionBar.
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //load shopping fragment
        loadPreferences();

        // Find our drawer view
        mDrawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        NavigationView navigationView = (NavigationView) findViewById(R.id.nvView);
        navigationView.setNavigationItemSelectedListener(
                new OnNavigationItemSelectedListenerImpl());

        // Set the menu icon instead of the launcher icon.
        final ActionBar actionBar = getSupportActionBar();
        actionBar.setHomeAsUpIndicator(R.drawable.ic_menu);
        actionBar.setDisplayHomeAsUpEnabled(true);
        Log.d(TAG, "onCreate ");
        setUpRateAppPopUp();

        mTabLayout.postDelayed(new Runnable() {
            @Override
            public void run() {
                RecipeDataStore.getsInstance(MainActivity.this).checkAndUnZipJsonData();
            }
        }, 2000);


    }

    void setUpRateAppPopUp() {
        new AppRate(this)
                .setShowIfAppHasCrashed(false)
                .setMinDaysUntilPrompt(1)
                .setMinLaunchesUntilPrompt(4)
                .init();
    }

    @Override
     public void onPause() {
        super.onPause();
        createSharedPreference();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        RecipeDataStore.getsInstance(this).dispose();
    }

    protected void loadPreferences(){
        String shoppingList = AppPreference.getInstance(this)
                .getString(ShoppingListDataStore.SAVED_SHOPPING_LIST, "");
        ShoppingListDataStore.createFromJson(shoppingList);
    }

    private void createSharedPreference(){
        String shoppingList = ShoppingListDataStore.getInstance().getJsonStr();
        AppPreference.getInstance(this).putString(
                ShoppingListDataStore.SAVED_SHOPPING_LIST, shoppingList);
    }

    private class OnNavigationItemSelectedListenerImpl implements
            NavigationView.OnNavigationItemSelectedListener {

        @Override
        public boolean onNavigationItemSelected(MenuItem menuItem) {
            int id = menuItem.getItemId();
            // The action bar home/up action should open or close the drawer.
            switch (menuItem.getItemId()) {
                case R.id.menu_favorite:
                    mDrawer.closeDrawer(Gravity.LEFT);
                    showFavoriteFragment();
                    return true;
                case R.id.menu_recent:
                    mDrawer.closeDrawer(Gravity.LEFT);
                    showRecentFragment();
                    return true;
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

    //session start
    @Override
    protected void onStart() {
        super.onStart();
        Tapjoy.onActivityStart(this);
    }

    //session end
    @Override
    protected void onStop() {
        Tapjoy.onActivityStop(this);
        super.onStop();
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


    private void showRecentFragment() {
        Log.d("TAG", "showRecentFragment");
        RecipeListFragment fragment = RecipeListFragment.getInstance(Pages.RECENT);
        getSupportFragmentManager().beginTransaction().add(
                R.id.full_screen_view, fragment, "Recent History Fragment")
                .addToBackStack(RecipeListFragment.class.getSimpleName())
                .commit();
    }

    private void showFavoriteFragment() {
        Log.d("TAG", "showFavoriteFragment");
        RecipeListFragment fragment = RecipeListFragment.getInstance(Pages.FAVOURITE);
        getSupportFragmentManager().beginTransaction().add(
                R.id.full_screen_view, fragment, "Favorite Fragment")
                .addToBackStack(RecipeListFragment.class.getSimpleName())
                .commit();
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

    public void showDetailView(int recipeInfoId) {
        Log.d("TAG", "showDetailView");
        Bundle bundle = new Bundle();
        bundle.putInt(RecipeDetailFragment.RECIPE_DETAIL_KEY, recipeInfoId);
        RecipeDetailFragment rFrag = new RecipeDetailFragment();
        rFrag.setArguments(bundle);
        getSupportFragmentManager().beginTransaction().add(
                R.id.full_screen_view, rFrag, "Detail Fragment")
                .addToBackStack(RecipeDetailFragment.class.getSimpleName())
                .commit();
    }

    public void showDetailViewBrowseFragment(String query){
        BrowseFragment rFragment = new BrowseFragment();
        Bundle bundle = new Bundle();
        bundle.putString(BrowseFragment.SOURCE_KEY, BrowseFragment.ORIGIN_FROM_TAGS);
        bundle.putString(BrowseFragment.SEARCH_QUERY_KEY, query);
        rFragment.setArguments(bundle);
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();

        if (rFragment == null) {
            rFragment = new BrowseFragment();
        }

        transaction.replace(R.id.full_screen_view, rFragment, "TAG").
                    addToBackStack("TAG");;

        transaction.commit();
    }

    public void showCategoryBrowseFragment(String categoryType, String metaData){
        BrowseFragment rFragment = new BrowseFragment();
        Bundle bundle = new Bundle();
        bundle.putString(BrowseFragment.SOURCE_KEY, BrowseFragment.ORIGIN_FROM_CATEGORY);
        bundle.putString(BrowseFragment.CATEGORY_TYPE_KEY, categoryType);
        bundle.putString(BrowseFragment.CATEGORY_METADATA_KEY, metaData);
        rFragment.setArguments(bundle);
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();

        if (rFragment == null) {
            rFragment = new BrowseFragment();
        }

        transaction.replace(R.id.full_screen_view, rFragment, "TAG").
                addToBackStack("TAG");;

        transaction.commit();
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
            // disabled Favourate and Recent for now as it was very slow
            return 2;
//          return Pages.values().length;
        }

        @Override
        public Fragment getItem(int position) {
            Pages page = Pages.values()[position];
            Fragment fragment = null;
            switch (page) {
                case CATEGORIES:
                    fragment = new CategoryFragment();
                    break;
                case RECOMMENDED:
                case FAVOURITE:
                case RECENT:
                    fragment = RecipeListFragment.getInstance(page);
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
