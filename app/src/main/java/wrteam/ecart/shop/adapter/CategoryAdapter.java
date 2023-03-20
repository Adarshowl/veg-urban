package wrteam.ecart.shop.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import wrteam.ecart.shop.R;
import wrteam.ecart.shop.fragment.SubCategoryFragment;
import wrteam.ecart.shop.helper.Constant;
import wrteam.ecart.shop.model.Category;

public class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.ViewHolder> {
    private static final String TAG = CategoryAdapter.class.getSimpleName();
    public final ArrayList<Category> categoryList;
    final int layout;
    final Context context;
    final String from;
    final int visibleNumber;


    public CategoryAdapter(Context context, ArrayList<Category> categoryList, int layout, String from, int visibleNumber) {
        this.context = context;
        this.categoryList = categoryList;
        this.layout = layout;
        this.from = from;
        this.visibleNumber = visibleNumber;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(layout, parent, false);
        return new ViewHolder(view);
    }


    @SuppressLint("ResourceAsColor")
    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {
        final Category model = categoryList.get(position);
        holder.tvTitle.setText(model.getName());

//        if (model.getName().trim().equals("Herbs")) {
//            int currentColor = context.getResources().getColor(R.color.Vegetable);
//            int currentColorText = context.getResources().getColor(R.color.Vegetable_name);
//            int currentColorHead = context.getResources().getColor(R.color.Vegetable_heading);
//            holder.tvTitle.setTextColor(currentColorText);
//            holder.tvHead.setTextColor(currentColorHead);
//            holder.lytMain.setBackgroundColor(currentColor);
//        } else if (model.getName().trim().equals("Ready to Cook")) {
//            int currentColor = context.getResources().getColor(R.color.Fruits);
//            int currentColorText = context.getResources().getColor(R.color.Fruits_name);
//            int currentColorHead = context.getResources().getColor(R.color.Fruits_heading);
//            holder.tvTitle.setTextColor(currentColorText);
//            holder.tvHead.setTextColor(currentColorHead);
//            holder.lytMain.setBackgroundColor(currentColor);
//        } else if (model.getName().trim().equals("Seasonal")) {
//            int currentColor = context.getResources().getColor(R.color.Medicine);
//            int currentColorText = context.getResources().getColor(R.color.Medicine_name);
//            int currentColorHead = context.getResources().getColor(R.color.Medicine_heading);
//            holder.tvTitle.setTextColor(currentColorText);
//            holder.tvHead.setTextColor(currentColorHead);
//            holder.lytMain.setBackgroundColor(currentColor);
//        } else if (model.getName().trim().equals("Salad")) {
//            int currentColor = context.getResources().getColor(R.color.Salads);
//            int currentColorText = context.getResources().getColor(R.color.Salads_name);
//            int currentColorHead = context.getResources().getColor(R.color.Salads_heading);
//            holder.tvTitle.setTextColor(currentColorText);
//            holder.tvHead.setTextColor(currentColorHead);
//            holder.lytMain.setBackgroundColor(currentColor);
//        } else if (model.getName().trim().equals("Exotic Veg")) {
//            int currentColor = context.getResources().getColor(R.color.Product);
//            int currentColorText = context.getResources().getColor(R.color.Product_name);
//            int currentColorHead = context.getResources().getColor(R.color.Product_heading);
//            holder.tvTitle.setTextColor(currentColorText);
//            holder.tvHead.setTextColor(currentColorHead);
//            holder.lytMain.setBackgroundColor(currentColor);
//        } else if (model.getName().trim().equals("Vegetables")) {
//            int currentColor = context.getResources().getColor(R.color.Chicken);
//            int currentColorText = context.getResources().getColor(R.color.Chicken_name);
//            int currentColorHead = context.getResources().getColor(R.color.Chicken_heading);
//            holder.tvTitle.setTextColor(currentColorText);
//            holder.tvHead.setTextColor(currentColorHead);
//            holder.lytMain.setBackgroundColor(currentColor);
//        } else if (model.getName().trim().equals("Fruits")) {
//            int currentColor = context.getResources().getColor(R.color.Fishes);
//            int currentColorText = context.getResources().getColor(R.color.Fishes_name);
//            int currentColorHead = context.getResources().getColor(R.color.Fishes_heading);
//            holder.tvTitle.setTextColor(currentColorText);
//            holder.tvHead.setTextColor(currentColorHead);
//            holder.lytMain.setBackgroundColor(currentColor);
//        } else if (model.getName().trim().equals("Exotic Fruits")) {
//            int currentColor = context.getResources().getColor(R.color.Bakery);
//            int currentColorText = context.getResources().getColor(R.color.Bakery_name);
//            int currentColorHead = context.getResources().getColor(R.color.Bakery_heading);
//            holder.tvTitle.setTextColor(currentColorText);
//            holder.tvHead.setTextColor(currentColorHead);
//            holder.lytMain.setBackgroundColor(currentColor);
//        } else if (model.getName().trim().equals("Leafy Green")) {
//            int currentColor = context.getResources().getColor(R.color.Foods);
//            int currentColorText = context.getResources().getColor(R.color.Foods_name);
//            int currentColorHead = context.getResources().getColor(R.color.Foods_heading);
//            holder.tvTitle.setTextColor(currentColorText);
//            holder.tvHead.setTextColor(currentColorHead);
//            holder.lytMain.setBackgroundColor(currentColor);
//        } else if (model.getName().trim().equals("Sprouts")) {
//            int currentColor = context.getResources().getColor(R.color.Pizza);
//            int currentColorText = context.getResources().getColor(R.color.Pizza_name);
//            int currentColorHead = context.getResources().getColor(R.color.Pizza_heading);
//            holder.tvTitle.setTextColor(currentColorText);
//            holder.tvHead.setTextColor(currentColorHead);
//            holder.lytMain.setBackgroundColor(currentColor);
//        } else if (model.getName().trim().equals("Cut Fruits")) {
//            int currentColor = context.getResources().getColor(R.color.Goods);
//            int currentColorText = context.getResources().getColor(R.color.Goods_name);
//            int currentColorHead = context.getResources().getColor(R.color.Goods_heading);
//            holder.tvTitle.setTextColor(currentColorText);
//            holder.tvHead.setTextColor(currentColorHead);
//            holder.lytMain.setBackgroundColor(currentColor);
//        } else {
//            int currentColor = context.getResources().getColor(R.color.Good);
//            int currentColorText = context.getResources().getColor(R.color.Good_name);
//            int currentColorHead = context.getResources().getColor(R.color.Good_heading);
//            holder.tvTitle.setTextColor(currentColorText);
//            holder.tvHead.setTextColor(currentColorHead);
//            holder.lytMain.setBackgroundColor(currentColor);
//        }


        Picasso.get()
                .load(model.getImage())
                .fit()
                .placeholder(R.drawable.placeholder)
                .error(R.drawable.placeholder)
                .into(holder.imgCategory);

        holder.lytMain.setOnClickListener(v -> {
            Fragment fragment = new SubCategoryFragment();
            Bundle bundle = new Bundle();
            bundle.putString(Constant.ID, model.getId());
            bundle.putString(Constant.NAME, model.getName());
            bundle.putString(Constant.FROM, "category");
            fragment.setArguments(bundle);
            ((AppCompatActivity) context).getSupportFragmentManager().beginTransaction().add(R.id.container, fragment).addToBackStack(null).commit();
        });
    }

    @Override
    public int getItemCount() {
//        int categories;
//        if (categoryList.size() > visibleNumber && from.equals("home")) {
//            categories = visibleNumber;
//        } else {
//            categories = categoryList.size();
//        }
//        return categories;
        return categoryList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        public final TextView tvTitle;
        final ImageView imgCategory;
        //        final LinearLayout lytMain;
        final RelativeLayout lytMain;
        CardView cat_card;

        public ViewHolder(View itemView) {
            super(itemView);
            lytMain = itemView.findViewById(R.id.lytMain);
//            cat_card = itemView.findViewById(R.id.cat_card);
            imgCategory = itemView.findViewById(R.id.imgCategory);
            tvTitle = itemView.findViewById(R.id.tvTitle);
//            tvHead = itemView.findViewById(R.id.tvHead);
        }

    }
}
