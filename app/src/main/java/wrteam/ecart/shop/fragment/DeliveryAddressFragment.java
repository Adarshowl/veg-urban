//package wrteam.ecart.shop.fragment;
//
//import android.content.Context;
//import android.os.Bundle;
//import android.util.Log;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.ViewGroup;
//import android.widget.AdapterView;
//import android.widget.ArrayAdapter;
//import android.widget.AutoCompleteTextView;
//import android.widget.Filter;
//import android.widget.Filterable;
//import android.widget.LinearLayout;
//import android.widget.TextView;
//import android.widget.Toast;
//
//import androidx.annotation.NonNull;
//import androidx.fragment.app.Fragment;
//import androidx.recyclerview.widget.RecyclerView;
//
//import com.google.android.libraries.places.api.Places;
//
//import org.json.JSONArray;
//import org.json.JSONException;
//import org.json.JSONObject;
//
//import java.io.IOException;
//import java.io.InputStreamReader;
//import java.net.HttpURLConnection;
//import java.net.MalformedURLException;
//import java.net.URL;
//import java.net.URLEncoder;
//import java.util.ArrayList;
//
//import wrteam.ecart.shop.R;
//import wrteam.ecart.shop.activity.MainActivity;
//import wrteam.ecart.shop.helper.Constant;
//
//
//public class DeliveryAddressFragment extends Fragment implements AdapterView.OnItemClickListener {
//    private static final String LOG_TAG = "Google Places Autocomplete";
//    private static final String PLACES_API_BASE = "https://maps.googleapis.com/maps/api/place";
//    private static final String TYPE_AUTOCOMPLETE = "/autocomplete";
//    private static final String OUT_JSON = "/json";
//    private static final String API_KEY = "AIzaSyAudWLtLNHEjuCZUGJiDEbEMBjwKlbkO6c";
//    private final String apiKey = "AIzaSyAudWLtLNHEjuCZUGJiDEbEMBjwKlbkO6c";
//    private View root;
//
//
//    private LinearLayout lytChooseView;
//
//    private AutoCompleteTextView etSearch;
//
//    public DeliveryAddressFragment() {
//        // Required empty public constructor
//    }
//
//    public static ArrayList autocomplete(String input) {
//        Log.d(LOG_TAG, "autocomplete: input " + input);
//        ArrayList resultList = null;
//
//        HttpURLConnection conn = null;
//        StringBuilder jsonResults = new StringBuilder();
//        try {
//            StringBuilder sb = new StringBuilder(PLACES_API_BASE + TYPE_AUTOCOMPLETE + OUT_JSON);
//            sb.append("?key=" + API_KEY);
//            sb.append("&components=country:in");
//            sb.append("&input=" + URLEncoder.encode(input, "utf8"));
//            Log.d(LOG_TAG, "autocomplete: sb " + sb.toString());
//
//            URL url = new URL(sb.toString());
//            conn = (HttpURLConnection) url.openConnection();
//            InputStreamReader in = new InputStreamReader(conn.getInputStream());
//
//            // Load the results into a StringBuilder
//            int read;
//            char[] buff = new char[1024];
//            while ((read = in.read(buff)) != -1) {
//                jsonResults.append(buff, 0, read);
//            }
//        } catch (MalformedURLException e) {
//            Log.e(LOG_TAG, "Error processing Places API URL", e);
//            return resultList;
//        } catch (IOException e) {
//            Log.e(LOG_TAG, "Error connecting to Places API", e);
//            return resultList;
//        } finally {
//            if (conn != null) {
//                conn.disconnect();
//            }
//        }
//
//        try {
//            // Create a JSON object hierarchy from the results
//            JSONObject jsonObj = new JSONObject(jsonResults.toString());
//            JSONArray predsJsonArray = jsonObj.getJSONArray("predictions");
//
//            // Extract the Place descriptions from the results
//            resultList = new ArrayList(predsJsonArray.length());
//            Log.d(LOG_TAG, "autocomplete: predsJsonArray " + predsJsonArray);
//            for (int i = 0; i < predsJsonArray.length(); i++) {
//                Log.d(LOG_TAG, "autocomplete description : " + predsJsonArray.getJSONObject(i).getString("description"));
//                System.out.println(predsJsonArray.getJSONObject(i).getString("description"));
//
//                resultList.add(predsJsonArray.getJSONObject(i).getString("description"));
//            }
//        } catch (JSONException e) {
//            Log.e(LOG_TAG, "Cannot process JSON results", e);
//        }
//
//        return resultList;
//    }
//
//    @Override
//    public View onCreateView(LayoutInflater inflater, ViewGroup container,
//                             Bundle savedInstanceState) {
//        // Inflate the layout for this fragment
//        root = inflater.inflate(R.layout.fragment_delivery_address, container, false);
//        etSearch = root.findViewById(R.id.searchView);
//
////        etSearch.setAdapter(new GooglePlacesAutocompleteAdapter(getContext(), R.layout.list_item));
//        etSearch.setAdapter(new GooglePlacesAutocompleteAdapter(getContext(), R.layout.list_item));
//        etSearch.setOnItemClickListener(this);
//
//        lytChooseView = root.findViewById(R.id.lytChooseView);
//
//        lytChooseView.setOnClickListener(v -> {
//            Fragment fragment = new DeliveryAddressMapFragment();
//            Bundle bundle1 = new Bundle();
//            bundle1.putString(Constant.FROM, "address");
//            bundle1.putDouble("latitude", 22.1545);
//            bundle1.putDouble("longitude", 76.4545);
//            fragment.setArguments(bundle1);
//            MainActivity.fm.beginTransaction().add(R.id.container, fragment).addToBackStack(null).commit();
//        });
//
//        if (!Places.isInitialized()) {
//            Places.initialize(getContext(), apiKey);
//        }
//        return root;
//    }
//
//    @Override
//    public void onResume() {
//        super.onResume();
//        Constant.TOOLBAR_TITLE = getString(R.string.choose_delivery_address);
//    }
//
//    public void onItemClick(AdapterView adapterView, View view, int position, long id) {
//        String str = (String) adapterView.getItemAtPosition(position);
//        Toast.makeText(getContext(), str, Toast.LENGTH_SHORT).show();
//    }
//
//    class GooglePlacesAutocompleteAdapter extends ArrayAdapter implements Filterable {
//        private ArrayList resultList;
//
//        public GooglePlacesAutocompleteAdapter(Context context, int textViewResourceId) {
//            super(context, textViewResourceId);
//        }
//
//        @Override
//        public int getCount() {
//            return resultList.size();
//        }
//
//        @Override
//        public String getItem(int index) {
//            return (String) resultList.get(index);
//        }
//
//        @Override
//        public Filter getFilter() {
//            Filter filter = new Filter() {
//                @Override
//                protected FilterResults performFiltering(CharSequence constraint) {
//                    FilterResults filterResults = new FilterResults();
//                    if (constraint != null) {
//                        // Retrieve the autocomplete results.
//                        resultList = autocomplete(constraint.toString());
//
//                        // Assign the data to the FilterResults
//                        filterResults.values = resultList;
//                        filterResults.count = resultList.size();
//                    }
//                    return filterResults;
//                }
//
//                @Override
//                protected void publishResults(CharSequence constraint, FilterResults results) {
//                    if (results != null && results.count > 0) {
//                        notifyDataSetChanged();
//                    } else {
//                        notifyDataSetInvalidated();
//                    }
//                }
//            };
//            return filter;
//        }
//
//
//    }
//
//    class GooglePlacesAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements Filterable {
//        private ArrayList resultList;
//
//        @NonNull
//        @Override
//        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
//            View view = LayoutInflater.from(getContext()).inflate(R.layout.address_item_layout, parent, false);
//            return new GooglePlacesViewHolder(view);
//        }
//
//        @Override
//        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
//
//        }
//
//        @Override
//        public int getItemCount() {
//            return resultList.size();
//        }
//
//        @Override
//        public Filter getFilter() {
//            Filter filter = new Filter() {
//                @Override
//                protected FilterResults performFiltering(CharSequence constraint) {
//                    FilterResults filterResults = new FilterResults();
//                    if (constraint != null) {
//                        // Retrieve the autocomplete results.
//                        resultList = autocomplete(constraint.toString());
//
//                        // Assign the data to the FilterResults
//                        filterResults.values = resultList;
//                        filterResults.count = resultList.size();
//                    }
//                    return filterResults;
//                }
//
//                @Override
//                protected void publishResults(CharSequence constraint, FilterResults results) {
//                    if (results != null && results.count > 0) {
//                        notifyDataSetChanged();
//                    } else {
//                    }
//                }
//            };
//            return filter;
//        }
//
//
//        class GooglePlacesViewHolder extends RecyclerView.ViewHolder {
//            TextView mainText, secondaryText;
//
//            public GooglePlacesViewHolder(@NonNull View itemView) {
//                super(itemView);
//                mainText = itemView.findViewById(R.id.mainText);
//                secondaryText = itemView.findViewById(R.id.secondaryText);
//            }
//
//        }
//
//
//    }
//}
//
//  running code******************************************************************************************************

package wrteam.ecart.shop.fragment;

import static android.content.Context.INPUT_METHOD_SERVICE;
import static android.content.Context.MODE_PRIVATE;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.LayoutRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.libraries.places.api.Places;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import wrteam.ecart.shop.R;
import wrteam.ecart.shop.activity.MainActivity;
import wrteam.ecart.shop.adapter.SavedAddressAdapter;
import wrteam.ecart.shop.helper.Constant;
import wrteam.ecart.shop.model.AddressList;
import wrteam.ecart.shop.model.SavedLocationAddress;

public class DeliveryAddressFragment extends Fragment implements AdapterView.OnItemClickListener {
    public static final String PREFER_NAME = "eKart";
    private static final String LOG_TAG = "Google Places Autocomplete";
    private static final String PLACES_API_BASE = "https://maps.googleapis.com/maps/api/place";
    private static final String TYPE_AUTOCOMPLETE = "/autocomplete";
    private static final String OUT_JSON = "/json";
    private static final String API_KEY = "AIzaSyAudWLtLNHEjuCZUGJiDEbEMBjwKlbkO6c";
    static ArrayList resultList = null;
    private final String apiKey = "AIzaSyAudWLtLNHEjuCZUGJiDEbEMBjwKlbkO6c";
    ArrayList<AddressList> addressList;
    ArrayList<SavedLocationAddress> savedLocationAddressesArrayList;
    private View root;
    private LinearLayout lytChooseView;
    private AutoCompleteTextView etSearch;
    private RecyclerView rv_recent_search;
    private SavedAddressAdapter adapter;
    private LinearLayout lytRecentView;
    private TextView tvClear;

    private Bundle mBundle;


    private double mLastLatitude = 0.0;
    private double mLastLongitude = 0.0;


    public DeliveryAddressFragment() {
        // Required empty public constructor
    }

    public static ArrayList autocomplete(String input) {
        Log.d(LOG_TAG, "autocomplete: input " + input);
        resultList = null;
        HttpURLConnection conn = null;
        StringBuilder jsonResults = new StringBuilder();
        try {
            StringBuilder sb = new StringBuilder(PLACES_API_BASE + TYPE_AUTOCOMPLETE + OUT_JSON);
            sb.append("?key=" + API_KEY);
            sb.append("&components=country:in");
//            sb.append("&radius=2000&strictBounds=true");
//           sb.append("&input=Indore" + URLEncoder.encode(input, "utf8"));
            sb.append("&input=" + URLEncoder.encode(input, "utf8"));
            Log.d(LOG_TAG, "autocomplete: sb " + sb.toString());
//            var url = $"https://maps.googleapis.com/maps/api/place/nearbysearch/json?location={args[0]}&radius={args[1]}&type=restaurant&keyword={args[2]}&key={args[3]}";
            URL url = new URL(sb.toString());
            conn = (HttpURLConnection) url.openConnection();
            InputStreamReader in = new InputStreamReader(conn.getInputStream());

            // Load the results into a StringBuilder
            int read;
            char[] buff = new char[1024];
            while ((read = in.read(buff)) != -1) {
                jsonResults.append(buff, 0, read);
            }
        } catch (MalformedURLException e) {
            Log.e(LOG_TAG, "Error processing Places API URL", e);
            return resultList;
        } catch (IOException e) {
            Log.e(LOG_TAG, "Error connecting to Places API", e);
            return resultList;
        } finally {
            if (conn != null) {
                conn.disconnect();
            }
        }

        try {
            JSONObject jsonObj = new JSONObject(jsonResults.toString());
            JSONArray predsJsonArray = jsonObj.getJSONArray("predictions");

            // Extract the Place descriptions from the results
            resultList = new ArrayList(predsJsonArray.length());
            Log.d(LOG_TAG, "autocomplete: predsJsonArray " + predsJsonArray);
            for (int i = 0; i < predsJsonArray.length(); i++) {
                Log.d(LOG_TAG, "autocomplete description : " + predsJsonArray.getJSONObject(i));
                resultList.add(new AddressList(predsJsonArray.getJSONObject(i).getJSONObject("structured_formatting").getString("main_text"), predsJsonArray.getJSONObject(i).getJSONObject("structured_formatting").getString("main_text")));
            }
        } catch (JSONException e) {
            Log.e(LOG_TAG, "Cannot process JSON results", e);
        }

        return resultList;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        root = inflater.inflate(R.layout.fragment_delivery_address, container, false);
        etSearch = root.findViewById(R.id.searchView);
        rv_recent_search = root.findViewById(R.id.rv_recent_search);
        lytRecentView = root.findViewById(R.id.lytRecentView);
        tvClear = root.findViewById(R.id.tvClear);

        mBundle = getArguments();
        mLastLatitude = mBundle.getDouble("lastLatitude");
        mLastLongitude = mBundle.getDouble("lastLongitude");
        Log.d("TAG", "onCreateView: mLastLatitude " + mLastLatitude + " *** "+mLastLongitude);
        addressList = new ArrayList<>();
        addressList.add(new AddressList("AAAAA", "2013"));
        addressList.add(new AddressList("BBBB Driver", "2017"));
        addressList.add(new AddressList("CCCC", "2016"));
        addressList.add(new AddressList("DDDDD", "2014"));
        addressList.add(new AddressList("EEEE Earth 1", "2013"));
        addressList.add(new AddressList("FFFFF Driver 2", "2017"));
        addressList.add(new AddressList("GGGGGG 3", "2016"));
        addressList.add(new AddressList("HHHHHH 4", "2014"));
//        etSearch.setAdapter(new GooglePlacesAutocompleteAdapter(getContext(), R.layout.list_item));
        etSearch.setAdapter(new GooglePlacesAutocompleteAdapter(getContext(), addressList));
        etSearch.setOnItemClickListener(this);

        lytChooseView = root.findViewById(R.id.lytChooseView);

        lytChooseView.setOnClickListener(v -> {
            Fragment fragment = new DeliveryAddressMapFragment();
            Bundle bundle1 = new Bundle();
            bundle1.putString(Constant.FROM, "address");
            bundle1.putString("place_name", "");
            bundle1.putDouble("latitude", 22.1545);
            bundle1.putDouble("longitude", 76.4545);

            bundle1.putDouble("lastLatitude",mLastLatitude);
            bundle1.putDouble("lastLongitude",mLastLongitude);

            fragment.setArguments(bundle1);
            MainActivity.fm.beginTransaction().add(R.id.container, fragment).addToBackStack(null).commit();
        });

        tvClear.setOnClickListener(v -> {
            clearData();
        });

        loadData();
        buildRecyclerView();

        if (savedLocationAddressesArrayList.size() == 0) {
            lytRecentView.setVisibility(View.GONE);
            rv_recent_search.setVisibility(View.GONE);
        }

        if (!Places.isInitialized()) {
            Places.initialize(getContext(), apiKey);
        }
        return root;
    }

    private void buildRecyclerView() {
        // initializing our adapter class.
        adapter = new SavedAddressAdapter(savedLocationAddressesArrayList, getContext());

        // adding layout manager to our recycler view.
        LinearLayoutManager manager = new LinearLayoutManager(getContext());
        rv_recent_search.setHasFixedSize(true);

        // setting layout manager to our recycler view.
        rv_recent_search.setLayoutManager(manager);

        // setting adapter to our recycler view.
        rv_recent_search.setAdapter(adapter);
    }

    @Override
    public void onResume() {
        super.onResume();
        Constant.TOOLBAR_TITLE = getString(R.string.choose_delivery_address);
        loadData();
        buildRecyclerView();
    }

    public void onItemClick(AdapterView adapterView, View view, int position, long id) {
        hideKeyboard();
        AddressList addressList = (AddressList) adapterView.getItemAtPosition(position);

        savedLocationAddressesArrayList.add(new SavedLocationAddress(((AddressList) adapterView.getItemAtPosition(position)).getMainText()));
        Log.d(LOG_TAG, "onItemClick: savedLocationAddressesArrayList.size() " + savedLocationAddressesArrayList.size());
        adapter.notifyItemInserted(savedLocationAddressesArrayList.size());
        saveData();
//        Log.d("TAG", "onItemClick: addressList" + addressList.getMainText());
//        Toast.makeText(getContext(), addressList.getMainText(), Toast.LENGTH_SHORT).show();
        etSearch.setText(addressList.getMainText());
        Fragment fragment = new DeliveryAddressMapFragment();
        Bundle bundle1 = new Bundle();
        bundle1.putString(Constant.FROM, "address");
        bundle1.putString("place_name", addressList.getMainText());
        fragment.setArguments(bundle1);
        MainActivity.fm.beginTransaction().add(R.id.container, fragment).addToBackStack(null).commit();
    }

    private void loadData() {
        // method to load arraylist from shared prefs
        // initializing our shared prefs with name as
        // shared preferences.
        SharedPreferences sharedPreferences = getContext().getSharedPreferences(PREFER_NAME, MODE_PRIVATE);

        // creating a variable for gson.
        Gson gson = new Gson();

        // below line is to get to string present from our
        // shared prefs if not present setting it as null.
        String json = sharedPreferences.getString("location", null);

        // below line is to get the type of our array list.
        Type type = new TypeToken<ArrayList<SavedLocationAddress>>() {
        }.getType();

        // in below line we are getting data from gson
        // and saving it to our array list
        savedLocationAddressesArrayList = gson.fromJson(json, type);

        // checking below if the array list is empty or not
        if (savedLocationAddressesArrayList == null) {
            // if the array list is empty
            // creating a new array list.
            savedLocationAddressesArrayList = new ArrayList<>();
        }
    }

    private void saveData() {
        // method for saving the data in array list.
        // creating a variable for storing data in
        // shared preferences.
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences(PREFER_NAME, MODE_PRIVATE);

        // creating a variable for editor to
        // store data in shared preferences.
        SharedPreferences.Editor editor = sharedPreferences.edit();

        // creating a new variable for gson.
        Gson gson = new Gson();

        // getting data from gson and storing it in a string.
        String json = gson.toJson(savedLocationAddressesArrayList);

        // below line is to save data in shared
        // prefs in the form of string.
        editor.putString("location", json);

        // below line is to apply changes
        // and save data in shared prefs.
        editor.apply();

        // after saving data we are displaying a toast message.
//        Toast.makeText(getContext(), "Saved Array List to Shared preferences. ", Toast.LENGTH_SHORT).show();
    }

    private void clearData() {
        // method for saving the data in array list.
        // creating a variable for storing data in
        // shared preferences.
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences(PREFER_NAME, MODE_PRIVATE);

        // creating a variable for editor to
        // store data in shared preferences.
        SharedPreferences.Editor editor = sharedPreferences.edit();

        // creating a new variable for gson.

        // getting data from gson and storing it in a string.

        // below line is to save data in shared
        // prefs in the form of string.
        editor.putString("location", "");
        lytRecentView.setVisibility(View.GONE);
        rv_recent_search.setVisibility(View.GONE);


        // below line is to apply changes
        // and save data in shared prefs.
        editor.apply();

        // after saving data we are displaying a toast message.
//        Toast.makeText(getContext(), "Saved Array List to Shared preferences. ", Toast.LENGTH_SHORT).show();
    }

    public void hideKeyboard() {
        try {
            InputMethodManager inputMethodManager = (InputMethodManager) getActivity().getSystemService(INPUT_METHOD_SERVICE);
            assert inputMethodManager != null;
            inputMethodManager.hideSoftInputFromWindow(root.getApplicationWindowToken(), 0);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    class GooglePlacesAutocompleteAdapter extends ArrayAdapter<AddressList> implements Filterable {

        private Context mContext;
        private List<AddressList> resultList = new ArrayList<>();

//        public GooglePlacesAutocompleteAdapter(Context context, int textViewResourceId) {
//            super(context, textViewResourceId);
//        }

        public GooglePlacesAutocompleteAdapter(@NonNull Context context, @SuppressLint("SupportAnnotationUsage") @LayoutRes ArrayList<AddressList> list) {
            super(context, 0, list);
            mContext = context;
            resultList = list;
        }

        @Override
        public int getCount() {
            return resultList.size();
        }

        //
        @Override
        public AddressList getItem(int index) {
            return resultList.get(index);
        }

        @Override
        public Filter getFilter() {
            Filter filter = new Filter() {
                @Override
                protected FilterResults performFiltering(CharSequence constraint) {
                    FilterResults filterResults = new FilterResults();
                    if (constraint != null) {
                        // Retrieve the autocomplete results.
                        resultList = autocomplete(constraint.toString());

                        // Assign the data to the FilterResults
                        filterResults.values = resultList;
                        filterResults.count = resultList.size();
                    }
                    return filterResults;
                }

                @Override
                protected void publishResults(CharSequence constraint, FilterResults results) {
                    if (results != null && results.count > 0) {

                        notifyDataSetChanged();
                    } else {
                        notifyDataSetInvalidated();
                    }
                }
            };
            return filter;
        }

        @NonNull
        @Override
        public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

            View listItem = convertView;
            if (listItem == null)
                listItem = LayoutInflater.from(mContext).inflate(R.layout.address_item_layout, parent, false);
            AddressList currentMovie = resultList.get(position);
            LinearLayout lyt_address_item = (LinearLayout) listItem.findViewById(R.id.lyt_address_item);
            TextView name = (TextView) listItem.findViewById(R.id.mainText);
            name.setText(currentMovie.getMainText());

            TextView release = (TextView) listItem.findViewById(R.id.secondaryText);
            release.setText(currentMovie.getSecondaryText());

//            lyt_address_item.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    etSearch.setText(currentMovie.getMainText());
//                }
//            });

            return listItem;
        }
    }

    class GooglePlacesAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements Filterable {
        private ArrayList resultList;

        @NonNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(getContext()).inflate(R.layout.address_item_layout, parent, false);
            return new GooglePlacesViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {

        }

        @Override
        public int getItemCount() {
            return resultList.size();
        }

        @Override
        public Filter getFilter() {
            Filter filter = new Filter() {
                @Override
                protected FilterResults performFiltering(CharSequence constraint) {
                    FilterResults filterResults = new FilterResults();
                    if (constraint != null) {
                        // Retrieve the autocomplete results.
                        resultList = autocomplete(constraint.toString());

                        // Assign the data to the FilterResults
                        filterResults.values = resultList;
                        filterResults.count = resultList.size();
                    }
                    return filterResults;
                }

                @Override
                protected void publishResults(CharSequence constraint, FilterResults results) {
                    if (results != null && results.count > 0) {
                        notifyDataSetChanged();
                    } else {
                    }
                }
            };
            return filter;
        }


        class GooglePlacesViewHolder extends RecyclerView.ViewHolder {
            TextView mainText, secondaryText;

            public GooglePlacesViewHolder(@NonNull View itemView) {
                super(itemView);
                mainText = itemView.findViewById(R.id.mainText);
                secondaryText = itemView.findViewById(R.id.secondaryText);
            }
        }
    }
}





