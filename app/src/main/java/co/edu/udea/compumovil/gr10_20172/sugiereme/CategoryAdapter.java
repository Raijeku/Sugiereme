package co.edu.udea.compumovil.gr10_20172.sugiereme;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Created by Davquiroga on 17/09/2017.
 */

public class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.CategoryViewHolder> {
    private static OnRecyclerItemClickListener listener;
    private List<Category> categoryList;
    private Context context;

    public CategoryAdapter(List<Category> categoryList, Context context, OnRecyclerItemClickListener onRecyclerItemClickListener){
        this.categoryList=categoryList;
        this.listener=onRecyclerItemClickListener;
        this.context=context;
    }

    public List<Category> getCategoryList() {
        return categoryList;
    }

    public void setCategoryList(List<Category> categoryList) {
        this.categoryList = categoryList;
    }

    @Override
    public int getItemCount() {
        return categoryList.size();
    }

    @Override
    public void onBindViewHolder(CategoryViewHolder holder, final int position) {
        Category category = categoryList.get(position);
        holder.titleView.setText(category.getTitle());
        StorageReference storageReference = FirebaseStorage.getInstance().getReference();
        storageReference.child(category.getImage());
        /*Glide.with(context)
                .using(new FirebaseImageLoader())
                .load(storageReference)
                .into(holder.photoView);*/
        Picasso.with(context)
                .load(category.getImage())
                .into(holder.imageView);
        //holder.photoView.setImageBitmap(apartment.getPhotos().get(0));
        holder.descriptionView.setText(category.getDescription());
        /*holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (listener!=null){
                    listener.onItemClicked(position);
                }
            }
        });*/
    }

    @Override
    public CategoryViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_layout, parent, false);
        /*itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int itemPosition =
            }
        });*/
        return new CategoryViewHolder(itemView);
    }

    public interface OnRecyclerItemClickListener{
        void onItemClicked(View v, int position);
    }

    public static class CategoryViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        protected ImageView imageView;
        protected TextView titleView;
        protected TextView descriptionView;

        public CategoryViewHolder(View itemView) {
            super(itemView);
            imageView = (ImageView) itemView.findViewById(R.id.image_view);
            titleView=(TextView) itemView.findViewById(R.id.title_view);
            descriptionView=(TextView) itemView.findViewById(R.id.description_view);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            Log.d("First","clicked");
            listener.onItemClicked(view,this.getLayoutPosition());
        }
    }

}
