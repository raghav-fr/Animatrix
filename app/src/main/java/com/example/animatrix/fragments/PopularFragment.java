package com.example.animatrix.fragments;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.animatrix.R;
import com.example.animatrix.adapters.VerticalItemsListAdapter;
import com.example.animatrix.callbacks.AnimeScrapCallback;
import com.example.animatrix.databinding.FragmentMoviesBinding;
import com.example.animatrix.databinding.FragmentPopularBinding;
import com.example.animatrix.helper.CustomMethods;
import com.example.animatrix.helper.Scrapper;

import org.json.JSONArray;

public class PopularFragment extends Fragment {

    private static final String TAG = "MADARA";
    private FragmentPopularBinding binding;
    private Activity activity;
    private VerticalItemsListAdapter rvAdapter;
    private GridLayoutManager layoutManager;
    private boolean isScrolling = false;
    private int currentItems, totalItems, scrollOutItems;
    private final JSONArray allAnime = new JSONArray();
    private int page = 1;
    private boolean alreadyReachedLastPage = false;
    private boolean firstTimeSearch = true;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentPopularBinding.inflate(inflater , container,false);
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
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        activity = getActivity();
        if (activity!=null){
            rvAdapter = new VerticalItemsListAdapter(allAnime,activity,true);
            binding.recyclerView.setAdapter(rvAdapter);
            layoutManager = new GridLayoutManager(activity, 3);
            binding.recyclerView.setLayoutManager(layoutManager);

            loadAnime(page);
        }
    }


    private void loadAnime(int page) {
        String pageLink = activity.getString(R.string.gogoanime_url)+"/popular.html?page="+page;

        if (!alreadyReachedLastPage){
            if (page>1){
                binding.loaderProgressBottom.setVisibility(View.VISIBLE);
            }
            Scrapper scrapper = new Scrapper(activity, new AnimeScrapCallback() {
                @Override
                public void onScrapeComplete(JSONArray results) {
                    binding.loaderProgressBottom.setVisibility(View.GONE);
                    binding.loaderProgressCenter.setVisibility(View.GONE);
                    binding.recyclerView.setVisibility(View.VISIBLE);

                    try {
                        if (results.length() <=0){
                            alreadyReachedLastPage=true;
                        }
                        firstTimeSearch = false;
                        int startPosition = rvAdapter.getItemCount();
                        CustomMethods.mergeTwoJsonArray(allAnime,results);
                        int itemCount = rvAdapter.getItemCount() - startPosition;
                        rvAdapter.notifyItemRangeInserted(startPosition,itemCount);

                    }
                    catch (Exception e){
                        Log.e(TAG, "onScrapeComplete: ", e);
                    }
                }
                @Override
                public void onScrapeFailed(String error) {
                    alreadyReachedLastPage = true;
                    binding.loaderProgressCenter.setVisibility(View.GONE);
                    binding.loaderProgressBottom.setVisibility(View.GONE);

                    if (firstTimeSearch){
                        CustomMethods.errorAlert(activity, "Error MV", error, "OK", false);
                    }
                }
            });

            scrapper.scrapeAnime(pageLink);

        }
    }
}
