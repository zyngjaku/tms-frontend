package io.github.zyngjaku.tmsfrontend.ui.orders;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;

import java.util.Objects;

import io.github.zyngjaku.tmsfrontend.R;

public class OrdersFragment extends Fragment {

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.orders_fragment, container, false);

        TextView textView = root.findViewById(R.id.text_slideshow);
        textView.setText("Orders");

        return root;
    }
}