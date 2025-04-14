package com.example.govcentralappointmentbooking;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridLayout;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.govcentralappointmentbooking.utils.Util;

public class ReservationsFragment extends Fragment {

    private GridLayout reservationsTableGrid;

    @Nullable
    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater,
            @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState
    ) {

        View view = inflater.inflate(R.layout.fragment_reservations, container, false);
        reservationsTableGrid = view.findViewById(R.id.reservationsTableGrid);
        return view;
    }
}