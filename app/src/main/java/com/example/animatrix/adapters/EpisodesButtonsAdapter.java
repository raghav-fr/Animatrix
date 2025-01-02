package com.example.animatrix.adapters;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.media3.common.util.Log;
import androidx.media3.common.util.UnstableApi;
import androidx.recyclerview.widget.RecyclerView;

import com.example.animatrix.Dialogs.EpisodeOptionsDialog;
import com.example.animatrix.R;

import org.json.JSONArray;

import java.nio.Buffer;
import java.util.stream.StreamSupport;

@UnstableApi
public class EpisodesButtonsAdapter extends RecyclerView.Adapter<EpisodesButtonsAdapter.MyCustomViewHolder> {

    private Activity activity;
    private String malId;

    public EpisodesButtonsAdapter(Activity activity, JSONArray allEpisodesArray, String animeId, String malId) {
        this.activity = activity;
        this.allEpisodesArray = allEpisodesArray;
        this.animeId = animeId;
        this.malId = malId;
    }

    private JSONArray  allEpisodesArray;
    private String animeId;

    @NonNull
    @Override
    public EpisodesButtonsAdapter.MyCustomViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(activity);
        View view = layoutInflater.inflate( R.layout.sample_episode_button_design,parent,false);
        return new EpisodesButtonsAdapter.MyCustomViewHolder(view);

    }

    @Override
    public void onBindViewHolder(@NonNull EpisodesButtonsAdapter.MyCustomViewHolder holder, int position) {
            try {
                String episodeId = allEpisodesArray.getJSONObject(holder.getBindingAdapterPosition()).getString("episodeId");
                String episodeNum = allEpisodesArray.getJSONObject(holder.getBindingAdapterPosition()).getString("episodeNum");

                holder.episodeButton.setText(episodeNum);
                holder.episodeButton.setOnClickListener(view -> new EpisodeOptionsDialog(activity,episodeId,malId));


            } catch (Exception e){
                Log.e("MADARA","onBindViewHolder", e);
            }
    }

    @Override
    public int getItemCount() {
        return allEpisodesArray.length();
    }

    public static class MyCustomViewHolder extends RecyclerView.ViewHolder{

        Button episodeButton;

        public MyCustomViewHolder(@NonNull View itemView) {
            super(itemView);
            episodeButton = itemView.findViewById(R.id.episodeButton);
        }
    }
}
