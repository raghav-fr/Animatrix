package com.example.animatrix.activities;

import android.os.Bundle;
import android.view.View;
import android.widget.AbsListView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.animatrix.R;
import com.example.animatrix.adapters.VerticalItemsListAdapter;
import com.example.animatrix.callbacks.AnimeScrapCallback;
import com.example.animatrix.databinding.ActivityGenreViewBinding;
import com.example.animatrix.helper.CustomMethods;
import com.example.animatrix.helper.Scrapper;

import org.json.JSONArray;
import org.json.JSONException;

public class GenreViewActivity extends AppCompatActivity {

    private ActivityGenreViewBinding binding;
    private GridLayoutManager layoutManager;
    private VerticalItemsListAdapter rvAdapter;
    private final JSONArray allAnime = new JSONArray();
    private boolean isScrolling = false;
    private boolean alreadyReachedLastPage = false;
    private int currentItems, totalItems, scrollOutItems;
    private int page = 1;
    private String genre;
    private boolean firstTimeSearch = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityGenreViewBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());



        genre = getIntent().getStringExtra("genre");

        rvAdapter = new VerticalItemsListAdapter(allAnime,GenreViewActivity.this,  true);
        binding.recyclerView.setAdapter(rvAdapter);
        layoutManager = new GridLayoutManager(GenreViewActivity.this, 3);
        binding.recyclerView.setLayoutManager(layoutManager);

        loadAnime(page);

        binding.recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {

            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {

                if (newState == AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL){
                    isScrolling = true;
                }
            }

            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {

                currentItems = layoutManager.getChildCount();
                totalItems = layoutManager.getItemCount();
                scrollOutItems = layoutManager.findFirstVisibleItemPosition();

                if (isScrolling && (currentItems + scrollOutItems == totalItems)){

                    isScrolling = false;
                    page = page + 1;
                    loadAnime(page);
                }
            }
        });

        ////////////////////////////////////////////////////////////////////////////////////////////

//        ownToolbarBinding.navbarLeftBtn.setOnClickListener(view -> onBackPressed());
    }

//    ----------------------------------------------------------------------------------------------

    private void loadAnime(int page){

        String genrePageLink = getString(R.string.gogoanime_url) + "/genre/" + genre + "?page=" + page;

        if (!alreadyReachedLastPage){

            if (page > 1){
                binding.loaderProgressBottom.setVisibility(View.VISIBLE);
            }

            Scrapper scraper = new Scrapper(GenreViewActivity.this, new AnimeScrapCallback() {
                @Override
                public void onScrapeComplete(JSONArray resultAnime) {

                    binding.loaderProgressBottom.setVisibility(View.GONE);
                    binding.loaderProgressCenter.setVisibility(View.GONE);
                    binding.recyclerView.setVisibility(View.VISIBLE);

                    try {

                        if (resultAnime.length() <= 0){
                            alreadyReachedLastPage = true;
                        }

                        firstTimeSearch = false;

                        int startPosition = rvAdapter.getItemCount(); // Get the current item count

                        CustomMethods.mergeTwoJsonArray(allAnime, resultAnime);

                        int itemCount = rvAdapter.getItemCount() - startPosition; // Calculate the number of inserted items

                        rvAdapter.notifyItemRangeInserted(startPosition, itemCount);

                    } catch (JSONException e){
                        e.printStackTrace();
                        CustomMethods.errorAlert(GenreViewActivity.this, "Error (Json)", e.getMessage(), "OK", false);
                    }
                }

                @Override
                public void onScrapeFailed(String error) {
                    alreadyReachedLastPage = true;
                    binding.loaderProgressBottom.setVisibility(View.GONE);
                    binding.loaderProgressCenter.setVisibility(View.GONE);

                    if (firstTimeSearch){
                        CustomMethods.errorAlert(GenreViewActivity.this, "Error SC", error, "OK", false);
                    }
                }
            });

            scraper.scrapeAnime(genrePageLink);
        }
    }
}