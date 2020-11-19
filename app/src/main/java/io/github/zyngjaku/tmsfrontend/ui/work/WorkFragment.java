package io.github.zyngjaku.tmsfrontend.ui.work;

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

public class WorkFragment extends Fragment {

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.work_fragment, container, false);

        TextView textView = root.findViewById(R.id.text_gallery);
        textView.setText("Work");

        return root;
    }
}