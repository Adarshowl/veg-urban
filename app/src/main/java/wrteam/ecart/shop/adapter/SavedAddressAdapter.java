package wrteam.ecart.shop.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import wrteam.ecart.shop.R;
import wrteam.ecart.shop.model.SavedLocationAddress;

public class SavedAddressAdapter  extends RecyclerView.Adapter<SavedAddressAdapter.ViewHolder> {

    private static final String TAG = SavedAddressAdapter.class.getSimpleName();
    // creating a variable for array list and context.
    private ArrayList<SavedLocationAddress> courseModalArrayList;
    private Context context;

    // creating a constructor for our variables.
    public SavedAddressAdapter(ArrayList<SavedLocationAddress> courseModalArrayList, Context context) {
        this.courseModalArrayList = courseModalArrayList;
        this.context = context;
    }

    @NonNull
    @Override
    public SavedAddressAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // below line is to inflate our layout.
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.saved_location_name_view, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SavedAddressAdapter.ViewHolder holder, int position) {
        // setting data to our views of recycler view.
        SavedLocationAddress modal = courseModalArrayList.get(position);
        Log.d(TAG, "onBindViewHolder saved address +: "+ modal.getPlaceName());
        holder.courseNameTV.setText(modal.getPlaceName());
    }

    @Override
    public int getItemCount() {
        // returning the size of array list.
        return courseModalArrayList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        // creating variables for our views.
        private TextView courseNameTV;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            // initializing our views with their ids.
            courseNameTV = itemView.findViewById(R.id.tv_save_place_name);
        }
    }
}