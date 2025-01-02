package com.example.animatrix.activities;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.media3.common.util.UnstableApi;
import androidx.recyclerview.widget.GridLayoutManager;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.example.animatrix.R;
import com.example.animatrix.adapters.EpisodesButtonsAdapter;
import com.example.animatrix.callbacks.AnimeDetailsCallback;
import com.example.animatrix.databinding.ActivityDetailsBinding;
import com.example.animatrix.helper.CustomMethods;
import com.example.animatrix.helper.GetMALId;
import com.example.animatrix.helper.Scrapper;
import com.google.firebase.perf.util.ScreenTraceUtil;

import org.json.JSONArray;
import org.json.JSONObject;

@UnstableApi
public class DetailsActivity extends AppCompatActivity {
    private static final String TAG = "MADARA";
    private ActivityDetailsBinding binding;
    private EpisodesButtonsAdapter episodesButtonsAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityDetailsBinding.inflate(getLayoutInflater());
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(binding.getRoot());


        Intent intent = getIntent();
        String animeId = intent.getStringExtra("animeId");
        String episodeId = null;
        if (intent.hasExtra("episodeId")){
            episodeId=intent.getStringExtra("episodeId");
        }

//        Toast.makeText(this, animeId+"- epid -"+episodeId, Toast.LENGTH_SHORT).show();

        Scrapper  scrapper = new Scrapper(DetailsActivity.this, new AnimeDetailsCallback() {
            @SuppressLint("ResourceType")
            @Override
            public void onScrapeingComplete(JSONObject animeDetails) {
                try {

                    binding.progressBarContainer.setVisibility(View.GONE);
                    binding.detailsContainerScrollView.setVisibility(View.VISIBLE);
                    Glide.with(DetailsActivity.this)
                            .load(animeDetails.getString("animeImg"))
                            .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
                            .placeholder(DetailsActivity.this.getColor(R.color.white))
                            .into(binding.thumbnailImageView);

                    String animeTitle = animeDetails.getString("animeTitle");
                    String animeStatus = animeDetails.getString("status");
                    String animeType = animeDetails.getString("type");
                    String animeReleasedDate = animeDetails.getString("releasedDate");
                    String animeTotalEpisodes = animeDetails.getString("totalEpisodes");
                    String synopsis = animeDetails.getString("synopsis");

                    binding.animeTitleTV.setText(animeTitle);
                    binding.statusTV.setText(animeStatus);
                    binding.animeTypeTV.setText(animeType);
                    binding.releaseDateTV.setText(animeReleasedDate);
                    binding.totalEpisodesTV.setText(animeTotalEpisodes);
                    binding.synopsisTV.setText(synopsis);

                    StringBuilder genre = new StringBuilder();
                    JSONArray genreArray = animeDetails.getJSONArray("genres");
                    for (int i = 0; i < genreArray.length(); i++) {
                        genre.append(", ").append(CustomMethods.capitalize(genreArray.getString(i)));
                    }
                    binding.genresTV.setText(genre.substring(1).trim());

                    //-----------------------------------------------------------------

                    JSONArray episodeListArray = animeDetails.getJSONArray("episodesList");
                    if (episodeListArray.length()>20){
//                        Toast.makeText(DetailsActivity.this,String.valueOf(width), Toast.LENGTH_SHORT).show();
                        binding.layoutHeight.setLayoutParams(new LinearLayout.LayoutParams(WindowManager.LayoutParams.MATCH_PARENT,1200));
                    }
                    if (episodeListArray.length()>30){
//                        Toast.makeText(DetailsActivity.this,String.valueOf(width), Toast.LENGTH_SHORT).show();
                        binding.layoutHeight.setLayoutParams(new LinearLayout.LayoutParams(WindowManager.LayoutParams.MATCH_PARENT,1500));
                    }

                    GetMALId.getId(animeTitle, malID -> {

                        binding.loadingEpisodesContainer.setVisibility(View.GONE);
                        binding.episodesBtnRecyclerView.setVisibility(View.VISIBLE);
                        Log.d(TAG, "malID: " + malID);

                        episodesButtonsAdapter = new EpisodesButtonsAdapter(DetailsActivity.this, episodeListArray, animeId, malID);
                        binding.episodesBtnRecyclerView.setAdapter(episodesButtonsAdapter);

                        GridLayoutManager layoutManager = new GridLayoutManager(DetailsActivity.this, 3);
                        binding.episodesBtnRecyclerView.setLayoutManager(layoutManager);
                    });


                } catch (Exception e){
                    Log.e(TAG, "onScrapingComplete: ", e);
                }
            }

            @Override
            public void onScrapeingFailed(String error) {
                Log.e("anime",animeId.toString());
                CustomMethods.errorAlert(DetailsActivity.this, "Error (Json DT)", error, "OK", true);
                Toast.makeText(DetailsActivity.this, error.toString(), Toast.LENGTH_SHORT).show();
            }
        });
        scrapper.scrapeDetails(animeId,episodeId);
        binding.backBtn.setOnClickListener(view -> onBackPressed());
        binding.searchBtn.setOnClickListener(view -> {
            startActivity(new Intent(this, SearchActivity.class));
            finish();
        });

    }

    @SuppressLint("NotifyDataSetChanged")
    @Override
    protected void onResume() {
        super.onResume();

        if (episodesButtonsAdapter != null) {
            episodesButtonsAdapter.notifyDataSetChanged();
        }
    }
}
