package io.github.zyngjaku.tmsfrontend.ui.employee;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.mapbox.mapboxsdk.annotations.MarkerOptions;
import com.mapbox.mapboxsdk.camera.CameraUpdateFactory;
import com.mapbox.mapboxsdk.geometry.LatLng;
import com.mapbox.mapboxsdk.geometry.LatLngBounds;
import com.mapbox.mapboxsdk.maps.MapboxMap;
import com.mapbox.mapboxsdk.maps.OnMapReadyCallback;
import com.squareup.picasso.Picasso;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import io.github.zyngjaku.tmsfrontend.R;
import io.github.zyngjaku.tmsfrontend.entity.User;
import io.github.zyngjaku.tmsfrontend.utils.Utils;

public class EmployeeFragment extends Fragment {

    private FloatingActionButton localizationSnapFloatingActionButton;
    private ListView employeesListView;
    private EmployeeListViewAdapter usersListViewAdapter;

    private EditText employeeEditText;

    private ArrayList<User> listOfUsers;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.employee_fragment, container, false);

        employeesListView = root.findViewById(R.id.employeesListView);
        employeeEditText = root.findViewById(R.id.employeeEditText);

        listOfUsers = getListOfUsers();

        usersListViewAdapter = new EmployeeListViewAdapter(listOfUsers, getContext());
        employeesListView.setAdapter(usersListViewAdapter);

        localizationSnapFloatingActionButton = root.findViewById(R.id.addEmployeeFloatingActionButton);
        localizationSnapFloatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

        employeeEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                usersListViewAdapter.getFilter().filter(s.toString());
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });


        /*
        usersListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(getContext(), CreateGameActivity.class);

                switch (tabLayout.getSelectedTabPosition()) {
                    case 1:
                        Group group = listOfGroups.get(position);

                        ArrayList<String> tmpListName = new ArrayList<>();
                        ArrayList<String> tmpListId = new ArrayList<>();

                        for (User user : group.getListOfUsers()) {
                            tmpListName.add(user.getName());
                            tmpListId.add(String.valueOf(user.getId()));
                        }

                        intent.putStringArrayListExtra("playersId", tmpListId);
                        intent.putStringArrayListExtra("playersName", tmpListName);

                        usersListView.setAdapter(groupsListViewAdapter);
                        break;
                    default:
                        User user = listOfUsers.get(position);

                        intent.putStringArrayListExtra("playersId", new ArrayList<>(Arrays.asList(String.valueOf(System.getProperty("userId")), String.valueOf(user.getId()))));
                        intent.putStringArrayListExtra("playersName", new ArrayList<>(Arrays.asList(System.getProperty("userName"), user.getName())));

                        break;
                }

                startActivity(intent);
            }
        });

         */

        employeesListView.setAdapter(usersListViewAdapter);


/*
        searchUserEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                switch (tabLayout.getSelectedTabPosition()) {
                    case 1:
                        groupsListViewAdapter.getFilter().filter(s.toString());
                        break;
                    default:
                        usersListViewAdapter.getFilter().filter(s.toString());
                        break;
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });
 */
/*
        pullToRefresh.setRefreshing(true);
        pullToRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                switch (tabLayout.getSelectedTabPosition()) {
                    case 1:
                        getListOfUsers();
                        break;
                    default:
                        getListOfGroups();
                        break;
                }
            }
        });
*/
        return root;
    }

    public ArrayList<User> getListOfUsers() {
        final ArrayList<User> listOfUsers = new ArrayList<>();

        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                StringBuilder res = new StringBuilder();
                int returnCode = 500;

                try {
                    URL url = new URL(Utils.API_ADDRESS + "/employees");
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
                    User[] users = g.fromJson(res.toString(), User[].class);

                    for (final User user : users) {
                        try {
                            Bitmap bitmap = Picasso.get().load(user.getAvatarUrl()).priority(Picasso.Priority.HIGH).get();
                            user.setAvatarBitmap(bitmap);

                            listOfUsers.add(user);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }

                    conn.disconnect();
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    if (returnCode == 200) {
                        getActivity().runOnUiThread(new Runnable() {
                            public void run() {
                                usersListViewAdapter.notifyDataSetChanged();
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

        return listOfUsers;
    }
}