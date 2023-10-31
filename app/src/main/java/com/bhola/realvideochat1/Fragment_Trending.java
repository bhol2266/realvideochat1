package com.bhola.realvideochat1;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;


public class Fragment_Trending extends Fragment {

    Context context;
    View view;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {



        view = inflater.inflate(R.layout.fragment_trending, container, false);

        context = getContext();


        return view;
    }




}





