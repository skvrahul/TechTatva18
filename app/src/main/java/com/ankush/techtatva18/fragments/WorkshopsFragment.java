package com.ankush.techtatva18.fragments;


import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ankush.techtatva18.R;

/**
 * A {@link Fragment} for displaying the workshops.
 */
public class WorkshopsFragment extends Fragment {


    public WorkshopsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_workshops, container, false);
    }

}
