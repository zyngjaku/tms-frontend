package io.github.zyngjaku.tmsfrontend.ui.vehicle;

import android.app.Dialog;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

import io.github.zyngjaku.tmsfrontend.R;
import io.github.zyngjaku.tmsfrontend.entity.Vehicle;
import io.github.zyngjaku.tmsfrontend.utils.Utils;

public class VehicleFragment extends Fragment {
    View root;
    private FloatingActionButton addVehicleFloatingActionButton;
    private ListView vehicleListView;
    private VehicleListViewAdapter vehicleListViewAdapter;

    private EditText vehicleEditText;

    private ArrayList<Vehicle> listOfVehicles;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
         root = inflater.inflate(R.layout.vehicle_fragment, container, false);

        vehicleListView = root.findViewById(R.id.vehicleListView);
        vehicleEditText = root.findViewById(R.id.vehicleEditText);

        listOfVehicles = getListOfVehicles();


        addVehicleFloatingActionButton = root.findViewById(R.id.addVehicleFloatingActionButton);
        addVehicleFloatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDialog();
            }
        });

        vehicleListViewAdapter = new VehicleListViewAdapter(listOfVehicles, getContext());
        vehicleListView.setAdapter(vehicleListViewAdapter);

        vehicleEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                vehicleListViewAdapter.getFilter().filter(s.toString());
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });

        //vehicleListView.setAdapter(vehicleListViewAdapter);

        return root;
    }


    public void showDialog(){
       final Dialog dialog = Utils.createDialog(getActivity(), R.layout.dialog_template, true, "Add new vehicle", R.layout.vehicle_dialog_edit);

        Button dialogSaveButton = (Button) dialog.findViewById(R.id.submitButton);
        dialogSaveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getContext(), "New vehicle has been added", Toast.LENGTH_SHORT).show();
                dialog.dismiss();
            }
        });

        Button dialogCancelButton = (Button) dialog.findViewById(R.id.cancelButton);
        dialogCancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        dialog.show();
    }

    public ArrayList<Vehicle> getListOfVehicles() {
        final ArrayList<Vehicle> listOfVehicles = new ArrayList<>();

        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                StringBuilder res = new StringBuilder();
                int returnCode = 500;

                try {
                    URL url = new URL(Utils.API_ADDRESS + "/vehicles");
                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                    conn.setRequestMethod("GET");
                    conn.setRequestProperty("Content-Type", "application/json;charset=UTF-8");
                    conn.setRequestProperty("Accept", "application/json");
                    conn.addRequestProperty("Authorization", "Bearer " + System.getProperty("token"));

                    returnCode = conn.getResponseCode();

                    BufferedReader br = new BufferedReader(new InputStreamReader((conn.getInputStream())));
                    String output;
                    while ((output = br.readLine()) != null) {
                        res.append(output);
                    }

                    System.out.println(res.toString());

                    Gson g = new GsonBuilder()
                            .setDateFormat("HH:mm dd-MM-yyy")
                            .create();
                    Vehicle[] vehicles = g.fromJson(res.toString(), Vehicle[].class);

                    for (final Vehicle vehicle : vehicles) {
                        listOfVehicles.add(vehicle);
                    }

                    conn.disconnect();
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    if (returnCode == 200) {
                        getActivity().runOnUiThread(new Runnable() {
                            public void run() {
                                vehicleListViewAdapter.notifyDataSetChanged();
                            }
                        });
                    } else {
                        getActivity().runOnUiThread(new Runnable() {
                            public void run() {
                                Toast.makeText(getContext(), "There are some troubles with server!", Toast.LENGTH_LONG).show();
                            }
                        });
                    }
                }
            }
        });

        thread.start();

        return listOfVehicles;
    }
}