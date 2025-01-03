package com.example.animatrix.fragments;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.animatrix.R;
import com.example.animatrix.adapters.VerticalItemsListAdapter;
import com.example.animatrix.callbacks.AnimeScrapCallback;
import com.example.animatrix.callbacks.RecentScrapCallback;
import com.example.animatrix.databinding.FragmentPopularBinding;
import com.example.animatrix.databinding.FragmentRecentUploadsBinding;
import com.example.animatrix.helper.CustomMethods;
import com.example.animatrix.helper.Scrapper;

import org.json.JSONArray;

public class RecentFragment extends Fragment {
    private static final String TAG = "MADARA";
    private FragmentRecentUploadsBinding binding;
    private Activity activity;
    private GridLayoutManager layoutManager;
    private final JSONArray allAnime = new JSONArray();
    private VerticalItemsListAdapter rvAdapter;
    private boolean isScrolling = false;
    private int currentItems, totalItems, scrollOutItems;
    private int page = 1;
    private int type;
    private boolean alreadyReachedLastPage = false;
    private boolean firstTimeSearch = true;

    public RecentFragment(int type) {
        this.type = type;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        activity = getActivity();

        if (activity != null) {

            rvAdapter = new VerticalItemsListAdapter( allAnime,activity, false);
            binding.recyclerView.setAdapter(rvAdapter);
            layoutManager = new GridLayoutManager(activity, 3);
            binding.recyclerView.setLayoutManager(layoutManager);

            if (CustomMethods.isInternetOn(activity)) {
                loadAnime(page);
            } else {
                Toast.makeText(activity, "No internet connection.", Toast.LENGTH_LONG).show();
                activity.finish();
            }
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        binding = FragmentRecentUploadsBinding.inflate(inflater, container, false);

        binding.recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {

                if (newState == AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL) {
                    isScrolling = true;
                }
            }

            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {

                currentItems = layoutManager.getChildCount();
                totalItems = layoutManager.getItemCount();
                scrollOutItems = layoutManager.findFirstVisibleItemPosition();

                if (isScrolling && (currentItems + scrollOutItems == totalItems)) {

                    isScrolling = false;
                    page = page + 1;
                    loadAnime(page);
                }
            }
        });

        return binding.getRoot();
    }

//    ----------------------------------------------------------------------------------------------


    private void loadAnime(int page) {

        if (!alreadyReachedLastPage) {

            if (page > 1) {
                binding.loaderProgressBottom.setVisibility(View.VISIBLE);
            }

            Scrapper scraper = new Scrapper(activity, new RecentScrapCallback() {
                @Override
                public void onScrapeComplete(JSONArray resultAnime) {

                    binding.loaderProgressCenter.setVisibility(View.GONE);
                    binding.loaderProgressBottom.setVisibility(View.GONE);
                    binding.recyclerView.setVisibility(View.VISIBLE);

                    try {
                        if (resultAnime.length() <= 0){
                            alreadyReachedLastPage = true;
                        }

                        firstTimeSearch = false;

                        int startPosition = rvAdapter.getItemCount(); // Get the current item count

//                        Toast.makeText(activity, String.valueOf(allAnime.length()), Toast.LENGTH_SHORT).show();
                        CustomMethods.mergeTwoJsonArray(allAnime, resultAnime);

                        int itemCount = rvAdapter.getItemCount() - startPosition; // Calculate the number of inserted items

                        rvAdapter.notifyItemRangeInserted(startPosition, itemCount);

                    } catch (Exception e){
                        Log.e(TAG, "onScrapeComplete: ", e);
                        CustomMethods.errorAlert(activity, "Json Error", e.getMessage(), "OK", true);
                    }
                }

                @Override
                public void onScrapeFailed(String error) {
                    alreadyReachedLastPage = true;
                    binding.loaderProgressCenter.setVisibility(View.GONE);
                    binding.loaderProgressBottom.setVisibility(View.GONE);

                    if (firstTimeSearch){
                        CustomMethods.errorAlert(activity, "Error", error, "OK", false);
                    }
                }
            });

            scraper.scrapeRecent(page, type);
        }
    }

}
