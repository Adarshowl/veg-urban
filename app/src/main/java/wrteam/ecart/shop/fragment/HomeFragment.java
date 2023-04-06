package wrteam.ecart.shop.fragment;

import static android.content.Context.LOCATION_SERVICE;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Rect;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.widget.NestedScrollView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.viewpager.widget.ViewPager;

import com.facebook.shimmer.ShimmerFrameLayout;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.tabs.TabLayout;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import wrteam.ecart.shop.R;
import wrteam.ecart.shop.activity.MainActivity;
import wrteam.ecart.shop.adapter.CategoryAdapter;
import wrteam.ecart.shop.adapter.OfferAdapter;
import wrteam.ecart.shop.adapter.SectionAdapter;
import wrteam.ecart.shop.adapter.SliderAdapter;
import wrteam.ecart.shop.helper.ApiConfig;
import wrteam.ecart.shop.helper.Constant;
import wrteam.ecart.shop.helper.Session;
import wrteam.ecart.shop.model.Category;
import wrteam.ecart.shop.model.Slider;
import wrteam.ecart.shop.ui.WrapContentViewPager;


public class HomeFragment extends Fragment {

    public static final String TAG = HomeFragment.class.getSimpleName();
    public static ArrayList<Category> categoryArrayList, sectionList;
    public Session session;
    ArrayList<Slider> sliderArrayList, newSliderArrayList;
    Activity activity;
    NestedScrollView nestedScrollView;
    SwipeRefreshLayout swipeLayout;
    View root;

    int timerDelay = 0, timerWaiting = 0;
    EditText searchView;
    RecyclerView categoryRecyclerView, sectionView, offerView;
    TabLayout tabLayout;
    ViewPager mPager, newSliderViewPager;
    WrapContentViewPager viewPager;
    LinearLayout mMarkersLayout, new_layout_markers, ll_slider;
    int size;
    Timer swipeTimer;
    Handler handler;
    Runnable Update;
    int currentPage = 0;
    LinearLayout lytCategory, lytSearchView, lytLocation;
    Menu menu;
    TextView tvMore, tvMoreFlashSale;
    boolean searchVisible = false;
    FloatingActionButton fabSearch;
    // location start
    TextView tvCurrentLocation;
    private ShimmerFrameLayout mShimmerViewContainer;
    private Location location;
    private List<Address> addresses;
    private Geocoder geocoder;

    private double mLastLatitude = 0.0;
    private double mLastLongitude = 0.0;

// location end

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        root = inflater.inflate(R.layout.fragment_home, container, false);
        activity = getActivity();
        session = new Session(activity);

        timerDelay = 3000;
        timerWaiting = 3000;
        setHasOptionsMenu(true);


        swipeLayout = root.findViewById(R.id.swipeLayout);

        categoryRecyclerView = root.findViewById(R.id.categoryRecyclerView);

        sectionView = root.findViewById(R.id.sectionView);
        sectionView.setLayoutManager(new LinearLayoutManager(activity));
        sectionView.setNestedScrollingEnabled(false);

        offerView = root.findViewById(R.id.offerView);
        offerView.setLayoutManager(new LinearLayoutManager(activity));
        offerView.setNestedScrollingEnabled(false);

        tabLayout = root.findViewById(R.id.tabLayout);
        viewPager = root.findViewById(R.id.viewPager);

        nestedScrollView = root.findViewById(R.id.nestedScrollView);
        mMarkersLayout = root.findViewById(R.id.layout_markers);
        new_layout_markers = root.findViewById(R.id.new_layout_markers);
        ll_slider = root.findViewById(R.id.ll_slider);
        lytCategory = root.findViewById(R.id.lytCategory);
        lytSearchView = root.findViewById(R.id.lytSearchView);
        lytLocation = root.findViewById(R.id.lytLocation);
        tvMoreFlashSale = root.findViewById(R.id.tvMoreFlashSale);
        tvMore = root.findViewById(R.id.tvMore);
        tvCurrentLocation = root.findViewById(R.id.tvCurrentLocation);
        mShimmerViewContainer = root.findViewById(R.id.mShimmerViewContainer);
        fabSearch = root.findViewById(R.id.fab_search);
        searchView = root.findViewById(R.id.searchView);

        if (nestedScrollView != null) {
            nestedScrollView.setNestedScrollingEnabled(false);
            nestedScrollView.setOnScrollChangeListener((NestedScrollView.OnScrollChangeListener) (v, scrollX, scrollY, oldScrollX, oldScrollY) -> {
                Rect scrollBounds = new Rect();
                nestedScrollView.getHitRect(scrollBounds);
                if (!lytSearchView.getLocalVisibleRect(scrollBounds) || scrollBounds.height() > lytSearchView.getHeight()) {
                    searchVisible = true;
                    menu.findItem(R.id.toolbar_search).setVisible(true);
                } else {
                    searchVisible = false;
                    menu.findItem(R.id.toolbar_search).setVisible(false);
                }
                activity.invalidateOptionsMenu();
            });
        }

        tvMore.setOnClickListener(v -> {
            if (!MainActivity.categoryClicked) {
                MainActivity.fm.beginTransaction().add(R.id.container, MainActivity.categoryFragment).show(MainActivity.categoryFragment).hide(MainActivity.active).commit();
                MainActivity.categoryClicked = true;
            } else {
                MainActivity.fm.beginTransaction().show(MainActivity.categoryFragment).hide(MainActivity.active).commit();
            }
            MainActivity.bottomNavigationView.setSelectedItemId(R.id.navCategory);
            MainActivity.active = MainActivity.categoryFragment;
        });

        tvMoreFlashSale.setOnClickListener(v -> {
            Fragment fragment = new ProductListFragment();
            Bundle bundle = new Bundle();
            bundle.putString("id", "");
            bundle.putString("cat_id", "");
            bundle.putString(Constant.FROM, "flash_sale_all");
            bundle.putString("name", activity.getString(R.string.flash_sales));
            fragment.setArguments(bundle);
            MainActivity.fm.beginTransaction().add(R.id.container, fragment).addToBackStack(null).commit();
        });

        searchView.setOnTouchListener((View v, MotionEvent event) -> {
            Fragment fragment = new ProductListFragment();
            Bundle bundle = new Bundle();
            bundle.putString(Constant.FROM, "search");
            bundle.putString(Constant.NAME, activity.getString(R.string.search));
            bundle.putString(Constant.ID, "");
            fragment.setArguments(bundle);
            MainActivity.fm.beginTransaction().add(R.id.container, fragment).addToBackStack(null).commit();
            return false;
        });

        lytSearchView.setOnClickListener(v -> {
            Fragment fragment = new ProductListFragment();
            Bundle bundle = new Bundle();
            bundle.putString(Constant.FROM, "search");
            bundle.putString(Constant.NAME, activity.getString(R.string.search));
            bundle.putString(Constant.ID, "");
            fragment.setArguments(bundle);
            MainActivity.fm.beginTransaction().add(R.id.container, fragment).addToBackStack(null).commit();
        });

        fabSearch.setOnClickListener(v -> {
            Fragment fragment = new ProductListFragment();
            Bundle bundle = new Bundle();
            bundle.putString(Constant.FROM, "search");
            bundle.putString(Constant.NAME, activity.getString(R.string.search));
            bundle.putString(Constant.ID, "");
            fragment.setArguments(bundle);
            MainActivity.fm.beginTransaction().add(R.id.container, fragment).addToBackStack(null).commit();
        });

        lytLocation.setOnClickListener(v -> {
            if (tvCurrentLocation.getText().toString() != "No location permission" && tvCurrentLocation.getText().toString() != null) {
                Fragment fragment = new DeliveryAddressFragment();
                Bundle bundle = new Bundle();
                bundle.putDouble("lastLatitude", mLastLatitude);
                bundle.putDouble("lastLongitude", mLastLongitude);
                fragment.setArguments(bundle);
                MainActivity.fm.beginTransaction().add(R.id.container, fragment).addToBackStack(null).commit();
            } else {
                Toast.makeText(getContext(), "Please allow location permission from phone setting", Toast.LENGTH_SHORT).show();
            }
        });

        // location code start

        LocationManager locationManager = (LocationManager) getContext().getSystemService(LOCATION_SERVICE);

        if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, 101);
        }
        try {
            Criteria criteria = new Criteria();

            String provider = locationManager.getBestProvider(criteria, true);
            Log.d(TAG, "onCreateView: provider" + provider);
//            Location location = locationManager.getLastKnownLocation(provider);
//            location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
            location = locationManager.getLastKnownLocation(provider);
            geocoder = new Geocoder(getContext());
            getCurrentLocation();

        } catch (Exception e) {

        }

        getCurrentLocation();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                try {
                    if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                        return;
                    }
                    location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                    geocoder = new Geocoder(getContext());
                    getCurrentLocation();
                } catch (Exception e) {

                }
            }
        }, 7000);
        // location code end

        mPager = root.findViewById(R.id.pager);
        newSliderViewPager = root.findViewById(R.id.newPager);
        mPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int i, float v, int i1) {
            }

            @Override
            public void onPageSelected(int position) {
                ApiConfig.addMarkers(position, sliderArrayList, mMarkersLayout, activity);
            }

            @Override
            public void onPageScrollStateChanged(int i) {
            }
        });
        newSliderViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int i, float v, int i1) {
            }

            @Override
            public void onPageSelected(int position) {
                ApiConfig.addMarkers(position, newSliderArrayList, new_layout_markers, activity);
            }

            @Override
            public void onPageScrollStateChanged(int i) {
            }
        });

        categoryArrayList = new ArrayList<>();

        swipeLayout.setColorSchemeColors(ContextCompat.getColor(activity, R.color.colorPrimary));

        swipeLayout.setOnRefreshListener(() -> {
            if (swipeTimer != null) {
                swipeTimer.cancel();
            }
            if (ApiConfig.isConnected(getActivity())) {
                ApiConfig.getWalletBalance(activity, new Session(activity));
                GetHomeData();
            }
            swipeLayout.setRefreshing(false);
        });

        if (ApiConfig.isConnected(getActivity())) {
            ApiConfig.getWalletBalance(activity, new Session(activity));
            GetHomeData();
        } else {
            nestedScrollView.setVisibility(View.VISIBLE);
            mShimmerViewContainer.setVisibility(View.GONE);
            mShimmerViewContainer.stopShimmer();
        }
        return root;
    }

    private void getCurrentLocation() {
        try {
            addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 10);
            Address address = addresses.get(0);
            try {
//            Constant.CURRENT_USER_LOCATION = address.getPostalCode() + ", " + address.getLocality();
                if (Constant.CURRENT_USER_LOCATION != "") {
                    tvCurrentLocation.setText(Constant.CURRENT_USER_LOCATION);
                } else {
                    tvCurrentLocation.setText(address.getPostalCode() + ", " + address.getLocality());
                    mLastLatitude = address.getLatitude();
                    mLastLongitude = address.getLongitude();
                }
            } catch (Exception e) {
                lytLocation.setVisibility(View.GONE);
            }
            Log.d(TAG, "onRequestPermissionsResult: " + address.getPostalCode() + " *** " +
                    address
            );
        } catch (Exception e) {
            Log.d(TAG, "getCurrentLocation: Exception -> " + e.getMessage());
//            tvCurrentLocation.setText("No location permission");
        }
    }


    public void GetHomeData() {
        nestedScrollView.setVisibility(View.GONE);
        mShimmerViewContainer.setVisibility(View.VISIBLE);
        mShimmerViewContainer.startShimmer();
        Map<String, String> params = new HashMap<>();
        if (session.getBoolean(Constant.IS_USER_LOGIN)) {
            params.put(Constant.USER_ID, session.getData(Constant.ID));
        }
        ApiConfig.RequestToVolley((result, response) -> {
            if (result) {
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    if (!jsonObject.getBoolean(Constant.ERROR)) {
                        GetSlider(jsonObject.getJSONArray(Constant.SLIDER_IMAGES));
                        Log.d(TAG, "GetHomeData: new slider image " + jsonObject.getJSONArray(Constant.NEW_SLIDER_IMAGES));
                        GetNewSlider(jsonObject.getJSONArray(Constant.NEW_SLIDER_IMAGES));
                        GetOfferImage(jsonObject.getJSONArray(Constant.OFFER_IMAGES));
                        GetFlashSale(jsonObject.getJSONArray(Constant.FLASH_SALES));
                        GetCategory(jsonObject);
                        SectionProductRequest(jsonObject.getJSONArray(Constant.SECTIONS));
                    } else {
                        nestedScrollView.setVisibility(View.VISIBLE);
                        mShimmerViewContainer.setVisibility(View.GONE);
                        mShimmerViewContainer.stopShimmer();
                    }
                } catch (JSONException e) {
                    nestedScrollView.setVisibility(View.VISIBLE);
                    mShimmerViewContainer.setVisibility(View.GONE);
                    mShimmerViewContainer.stopShimmer();

                }
            }
        }, getActivity(), Constant.GET_ALL_DATA_URL, params, false);
    }

    @SuppressWarnings("deprecation")
    public void GetFlashSale(JSONArray jsonArray) {
        try {
            tabLayout.removeAllTabs();

            for (int i = 0; i < jsonArray.length(); i++) {
                tabLayout.addTab(tabLayout.newTab().setText(jsonArray.getJSONObject(i).getString(Constant.TITLE)));
            }

            TabAdapter tabAdapter = new TabAdapter(MainActivity.fm, tabLayout.getTabCount(), jsonArray);
            viewPager.setAdapter(tabAdapter);
            viewPager.setOffscreenPageLimit(1);
            viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));

            tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
                @Override
                public void onTabSelected(TabLayout.Tab tab) {
                    viewPager.setCurrentItem(tab.getPosition());
//                    viewPager.reMeasureCurrentPage(tab.getPosition());
                }

                @Override
                public void onTabUnselected(TabLayout.Tab tab) {

                }

                @Override
                public void onTabReselected(TabLayout.Tab tab) {

                }
            });

//            tabLayout.setupWithViewPager(viewPager);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void GetOfferImage(JSONArray jsonArray) {
        ArrayList<String> offerList = new ArrayList<>();
        try {
            if (jsonArray != null && jsonArray.length() > 0) {
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject object = jsonArray.getJSONObject(i);
                    offerList.add(object.getString(Constant.IMAGE));
                }
                offerView.setAdapter(new OfferAdapter(offerList, R.layout.offer_lyt));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    void GetCategory(JSONObject object) {
        categoryArrayList = new ArrayList<>();
        try {
            int visible_count;
            int column_count;
            JSONArray jsonArray = object.getJSONArray(Constant.CATEGORIES);
            if (jsonArray.length() > 0) {
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                    Category category = new Gson().fromJson(jsonObject.toString(), Category.class);
                    categoryArrayList.add(category);
                }

//                if (!object.getString("style").equals("")) {
//                    if (object.getString("style").equals("style_1")) {
//                        visible_count = Integer.parseInt(object.getString("visible_count"));
//                        column_count = Integer.parseInt(object.getString("column_count"));
//                        categoryRecyclerView.setLayoutManager(new GridLayoutManager(activity, column_count));
//                        categoryRecyclerView.setAdapter(new CategoryAdapter(activity, categoryArrayList, R.layout.lyt_category_grid, "home", visible_count));
//                    } else if (object.getString("style").equals("style_2")) {
//                        visible_count = Integer.parseInt(object.getString("visible_count"));
//                        categoryRecyclerView.setLayoutManager(new LinearLayoutManager(activity, LinearLayoutManager.HORIZONTAL, false));
//                        categoryRecyclerView.setAdapter(new CategoryAdapter(activity, categoryArrayList, R.layout.lyt_category_list, "home", visible_count));
//                    }
//                } else {
                categoryRecyclerView.setLayoutManager(new LinearLayoutManager(activity, LinearLayoutManager.HORIZONTAL, false));
                categoryRecyclerView.setAdapter(new CategoryAdapter(activity, categoryArrayList, R.layout.lyt_category_list, "home", 15));
//                }
            } else {
                lytCategory.setVisibility(View.GONE);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void SectionProductRequest(JSONArray jsonArray) {  //json request for product search
        sectionList = new ArrayList<>();
        try {
            for (int j = 0; j < jsonArray.length(); j++) {
                Category section = new Category();
                JSONObject jsonObject = jsonArray.getJSONObject(j);
                section.setName(jsonObject.getString(Constant.TITLE));
                section.setId(jsonObject.getString(Constant.ID));
                section.setStyle(jsonObject.getString(Constant.SECTION_STYLE));
                section.setSubtitle(jsonObject.getString(Constant.SHORT_DESC));
                JSONArray productArray = jsonObject.getJSONArray(Constant.PRODUCTS);
                section.setProductList(ApiConfig.GetProductList(productArray));
                sectionList.add(section);
            }
            sectionView.setVisibility(View.VISIBLE);
            SectionAdapter sectionAdapter = new SectionAdapter(activity, getActivity(), sectionList);
            sectionView.setAdapter(sectionAdapter);
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    void GetSlider(JSONArray jsonArray) {
        sliderArrayList = new ArrayList<>();
        try {
            size = jsonArray.length();
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                sliderArrayList.add(new Slider(jsonObject.getString(Constant.TYPE), jsonObject.getString(Constant.TYPE_ID), jsonObject.getString(Constant.NAME), jsonObject.getString(Constant.IMAGE)));
            }
            mPager.setAdapter(new SliderAdapter(sliderArrayList, getActivity(), R.layout.lyt_slider, "home"));
            ApiConfig.addMarkers(0, sliderArrayList, mMarkersLayout, activity);
            handler = new Handler();
            Update = () -> {
                if (currentPage == size) {
                    currentPage = 0;
                }
                try {
                    mPager.setCurrentItem(currentPage++, true);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            };
            swipeTimer = new Timer();
            swipeTimer.schedule(new TimerTask() {
                @Override
                public void run() {
                    handler.post(Update);
                }
            }, timerDelay, timerWaiting);

        } catch (JSONException e) {
            e.printStackTrace();
        }
        nestedScrollView.setVisibility(View.VISIBLE);
        mShimmerViewContainer.setVisibility(View.GONE);
        mShimmerViewContainer.stopShimmer();
    }

    void GetNewSlider(JSONArray jsonArray) {
        newSliderArrayList = new ArrayList<>();
        try {
            size = jsonArray.length();
            if (jsonArray.length() > 0) {
                Log.d(TAG, "GetNewSlider: size - " + size);
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                    newSliderArrayList.add(new Slider("type", "type_" + i, "name_" + i, jsonObject.getString(Constant.IMAGE)));
                }
                newSliderViewPager.setAdapter(new SliderAdapter(newSliderArrayList, getActivity(), R.layout.lyt_slider, "home"));
                ApiConfig.addMarkers(0, newSliderArrayList, new_layout_markers, activity);
                handler = new Handler();
                Update = () -> {
                    if (currentPage == size) {
                        currentPage = 0;
                    }
                    try {
                        newSliderViewPager.setCurrentItem(currentPage++, true);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                };
                swipeTimer = new Timer();
                swipeTimer.schedule(new TimerTask() {
                    @Override
                    public void run() {
                        handler.post(Update);
                    }
                }, timerDelay, timerWaiting);
            } else {
                ll_slider.setVisibility(View.GONE);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        nestedScrollView.setVisibility(View.VISIBLE);
        mShimmerViewContainer.setVisibility(View.GONE);
        mShimmerViewContainer.stopShimmer();
    }


    @Override
    public void onResume() {
        super.onResume();
        activity.invalidateOptionsMenu();
        ApiConfig.GetSettings(activity);
        hideKeyboard();
    }

    public void hideKeyboard() {
        try {
            InputMethodManager inputMethodManager = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
            assert inputMethodManager != null;
            inputMethodManager.hideSoftInputFromWindow(root.getApplicationWindowToken(), 0);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onPrepareOptionsMenu(@NonNull Menu menu) {
        menu.findItem(R.id.toolbar_layout).setVisible(false);
        this.menu = menu;
        super.onPrepareOptionsMenu(menu);
//        menu.findItem(R.id.toolbar_cart).setVisible(true);
        menu.findItem(R.id.toolbar_profile).setVisible(true);
        menu.findItem(R.id.toolbar_sort).setVisible(false);
        menu.findItem(R.id.toolbar_search).setVisible(searchVisible);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        Log.d(TAG, "onRequestPermissionsResult:  requestcode " + requestCode + grantResults[0] + " ==  + " + PackageManager.PERMISSION_GRANTED);

        switch (requestCode) {
            case 101:
                Log.d(TAG, "onRequestPermissionsResult: " + grantResults[0] + " ==  + " + PackageManager.PERMISSION_GRANTED);

                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
//                    if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
//                        return;
//                    }
                    try {
                        addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 10);
                        Address address = addresses.get(0);
                        Log.d(TAG, "onRequestPermissionsResult: " + address.getPostalCode() + " *** " +
                                address
                        );
                        getCurrentLocation();
                    } catch (IOException e) {
                        e.printStackTrace();
                        Constant.CURRENT_USER_LOCATION = "No location permission";
                    }
                } else {
                    Toast.makeText(getContext(), "Location permission denied", Toast.LENGTH_SHORT);
                    Constant.CURRENT_USER_LOCATION = "No location permission";
                }
                break;
            default: {
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
                Constant.CURRENT_USER_LOCATION = "No location permission";
            }
        }
    }

    // for location permission start

    @SuppressWarnings("deprecation")
    public static class TabAdapter extends FragmentStatePagerAdapter {

        final int mNumOfTabs;
        final JSONArray jsonArray;

        @SuppressWarnings("deprecation")
        public TabAdapter(FragmentManager fm, int NumOfTabs, JSONArray jsonArray) {
            super(fm);
            this.mNumOfTabs = NumOfTabs;
            this.jsonArray = jsonArray;
        }

        @NonNull
        @Override
        public Fragment getItem(int position) {
            Fragment fragment = null;
            try {
                fragment = FlashSaleFragment.AddFragment(jsonArray.getJSONObject(position));
            } catch (JSONException e) {
                e.printStackTrace();
            }
            assert fragment != null;
            return fragment;
        }

        @Override
        public int getCount() {
            return mNumOfTabs;
        }
    }

    // for location permission end

}
