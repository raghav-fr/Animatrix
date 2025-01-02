package com.example.animatrix.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.animatrix.R;
import com.example.animatrix.adapters.GenreCardAdapter;

import java.util.zip.Inflater;

public class GenresFragment extends Fragment {
    private final String[] genresArray = {
            "action","adventure","adult-cast","cars","comedy","crime","dementia",
            "demons","detective","drama","dub","ecchi","family","fantasy","game","gourmet","harem",
            "historical","horror","josei","kids","magic","martial-arts","mecha",
            "military","mystery","parody","police","psychological","romance",
            "samurai","school","sci-fi","seinen","shoujo","shoujo-ai","shounen",
            "shounen-ai","space","sports","super-power","supernatural",
            "suspense","thriller","vampire","yaoi","yuri","isekai"};


    private RecyclerView recyclerView;
    private int[] colorArray;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View allViews = inflater.inflate(R.layout.fragment_genres,container,false);
        initVars(allViews);

        GenreCardAdapter genreCardAdapter = new GenreCardAdapter(requireContext(),genresArray,colorArray);
        recyclerView.setAdapter(genreCardAdapter);

        GridLayoutManager gridLayoutManager = new GridLayoutManager(requireContext(),3);
        recyclerView.setLayoutManager(gridLayoutManager);
        return allViews;

    }

    private void initVars(View allViews) {
        colorArray = getResources().getIntArray(R.array.genre_card_colors);
        recyclerView = allViews.findViewById(R.id.recyclerView);
    }
}
