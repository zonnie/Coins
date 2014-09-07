package com.moneyifyapp.activities.analytics.fragments;


import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.moneyifyapp.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class EmptyFragment extends Fragment
{
    public static final String EMPTY_TEXT_KEY = "emptyText";

    /**
     */
    public static EmptyFragment newInstance(String emptyText)
    {
        EmptyFragment fragment = new EmptyFragment();
        Bundle args = new Bundle();
        args.putString(EMPTY_TEXT_KEY, emptyText);
        fragment.setArguments(args);
        return fragment;
    }

    public EmptyFragment()
    {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.fragment_empty, container, false);

        if (getArguments() != null)
        {
            TextView textView = (TextView) view.findViewById(R.id.graph_empty_hint_textview);
            textView.setText(getArguments().getString(EMPTY_TEXT_KEY));
        }

        return view;
    }
}
