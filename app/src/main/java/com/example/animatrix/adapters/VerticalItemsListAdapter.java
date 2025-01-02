package com.example.animatrix.adapters;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.OptIn;
import androidx.cardview.widget.CardView;
import androidx.media3.common.util.UnstableApi;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.example.animatrix.R;
import com.example.animatrix.activities.DetailsActivity;

import org.json.JSONArray;
import org.json.JSONException;

public class VerticalItemsListAdapter extends RecyclerView.Adapter<VerticalItemsListAdapter.MyCustomViewHolder> {
    private final JSONArray allAnime;
    private final Activity activity;
    private final boolean isHorizontalView;
    private final String TAG = "MADARA";

    public VerticalItemsListAdapter(JSONArray allAnime, Activity activity, boolean isHorizontalView) {
        this.allAnime = allAnime;
        this.activity = activity;
        this.isHorizontalView = isHorizontalView;
    }
    @NonNull
    @Override
    public VerticalItemsListAdapter.MyCustomViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        LayoutInflater layoutInflater = LayoutInflater.from(activity);
        View view;

        if (isHorizontalView){
            view = layoutInflater.inflate(R.layout.sample_horizontal_item_card,parent,false);
        }else{
            view = layoutInflater.inflate(R.layout.sample_item_card_design,parent,false);
        }
        return new MyCustomViewHolder(view);
    }

    @OptIn(markerClass = UnstableApi.class)
    @SuppressLint("ResourceAsColor")
    @Override
    public void onBindViewHolder(@NonNull VerticalItemsListAdapter.MyCustomViewHolder holder, int position) {

        try {
            String animeTitle = allAnime.getJSONObject(holder.getBindingAdapterPosition()).getString("animeTitle");
            holder.animeTV.setText(animeTitle);

            if (allAnime.getJSONObject(holder.getBindingAdapterPosition()).has("subOrDub")){
                if (allAnime.getJSONObject(holder.getBindingAdapterPosition()).getString("subOrDub").equalsIgnoreCase("DUB")){

                    holder.isAnimeSubDub.setText("Dub");
                    holder.isAnimeSubDub.setTextColor(activity.getColor(R.color.white));
                    holder.isAnimeSubDub.setBackgroundColor(activity.getColor(R.color.black));
                }else {
                    holder.isAnimeSubDub.setTextColor(activity.getColor(R.color.black));
                    holder.isAnimeSubDub.setText("Sub");
                    holder.isAnimeSubDub.setBackgroundColor(activity.getColor(R.color.white));
                }
            }else{
                if (animeTitle.contains("(Dub)")){
                    holder.isAnimeSubDub.setTextColor(activity.getColor(R.color.white));
                    holder.isAnimeSubDub.setText("Dub");
                    holder.isAnimeSubDub.setBackgroundColor(activity.getColor(R.color.black));
                }else {
                    holder.isAnimeSubDub.setTextColor(activity.getColor(R.color.black));
                    holder.isAnimeSubDub.setText("Sub");
                    holder.isAnimeSubDub.setBackgroundColor(activity.getColor(R.color.white));
                }
            }

            Glide.with(activity)
                    .load(allAnime.getJSONObject(holder.getBindingAdapterPosition()).getString("animeImg"))
                    .placeholder(R.color.fade_white)
                    .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
                    .into(holder.imageView);


            if (allAnime.getJSONObject(holder.getBindingAdapterPosition()).has("releasedDate")) {
                holder.releaseDate.setVisibility(View.VISIBLE);
                holder.releaseDate.setText(allAnime.getJSONObject(holder.getBindingAdapterPosition()).getString("releasedDate"));
            }



        } catch (JSONException e) {
            Log.e("MADARA","Error",e);
        }

        String finalServer = "gogo";

        holder.itemView.setOnClickListener(view -> {
            try {
                Intent intent = new Intent(activity, DetailsActivity.class);
                intent.putExtra("animeId", allAnime.getJSONObject(holder.getBindingAdapterPosition()).getString("animeId"));
                intent.putExtra("episodeId", allAnime.getJSONObject(holder.getBindingAdapterPosition()).getString("episodeId"));
                activity.startActivity(intent);
            } catch (Exception e) {
                Log.e(TAG, "onBindViewHolder: ", e);
                Toast.makeText(activity, e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

    }

    @Override
    public int getItemCount() {
        return allAnime.length();
    }

    public static class MyCustomViewHolder extends RecyclerView.ViewHolder {
        CardView rootView;
        ImageView imageView;
        TextView animeTV,isAnimeSubDub,releaseDate;
        public MyCustomViewHolder(@NonNull View itemView) {
            super(itemView);
            rootView = itemView.findViewById(R.id.cardView);
            imageView = itemView.findViewById(R.id.thumb_img_view);
            animeTV = itemView.findViewById(R.id.anime_title_tv);
            isAnimeSubDub = itemView.findViewById(R.id.is_anime_sub_dub_tv);
            releaseDate = itemView.findViewById(R.id.releaseDateTV);
        }
    }
}
