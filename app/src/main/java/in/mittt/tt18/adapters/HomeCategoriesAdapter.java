package in.mittt.tt18.adapters;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.List;

import in.mittt.tt18.R;
import in.mittt.tt18.activities.CategoryActivity;
import in.mittt.tt18.models.categories.CategoryModel;
import in.mittt.tt18.utilities.IconCollection;

/**
 * Created by Saptarshi on 12/24/2017.
 */


public class HomeCategoriesAdapter extends RecyclerView.Adapter<HomeCategoriesAdapter.HomeViewHolder> {
    String TAG = "HomeCategoriesAdapter";
    Activity activity;
    private List<CategoryModel> categoriesList;
    private Context context;

    public HomeCategoriesAdapter(List<CategoryModel> categoriesList, Activity activity) {
        this.categoriesList = categoriesList;
        this.activity = activity;
    }

    @Override
    public HomeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_home_categories, parent, false);
        context = parent.getContext();
        return new HomeViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull HomeViewHolder holder, int position) {
        CategoryModel category = categoriesList.get(position);
        holder.onBind(category);
        IconCollection icons = new IconCollection();
        holder.categoryLogo.setImageResource(icons.getIconResource(activity, category.getCategoryName()));
    }

    @Override
    public int getItemCount() {
        return categoriesList.size();
    }

    public class HomeViewHolder extends RecyclerView.ViewHolder {
        public ImageView categoryLogo;
        public TextView categoryName;
        public RelativeLayout categoryItem;

        public HomeViewHolder(View view) {
            super(view);
            initializeViews(view);
        }

        public void onBind(final CategoryModel category) {
            categoryName.setText(category.getCategoryName());
            categoryItem.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(context, CategoryActivity.class);
                    intent.putExtra("catName", category.getCategoryName());
                    intent.putExtra("catID", category.getCategoryID());
                    intent.putExtra("catDesc", category.getCategoryDescription());
                    context.startActivity(intent);
                }
            });
        }

        public void initializeViews(View view) {
            categoryLogo = view.findViewById(R.id.home_category_logo_image_view);
            categoryName = view.findViewById(R.id.home_category_name_text_view);
            categoryItem = view.findViewById(R.id.home_categories_item);
        }
    }
}