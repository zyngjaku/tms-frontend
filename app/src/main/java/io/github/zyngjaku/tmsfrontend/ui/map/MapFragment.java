package io.github.zyngjaku.tmsfrontend.ui.map;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;

import io.github.zyngjaku.tmsfrontend.R;
import io.github.zyngjaku.tmsfrontend.entity.User;
import io.github.zyngjaku.tmsfrontend.utils.Utils;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.mapbox.android.gestures.MoveGestureDetector;
import com.mapbox.mapboxsdk.Mapbox;
import com.mapbox.mapboxsdk.annotations.Icon;
import com.mapbox.mapboxsdk.annotations.IconFactory;
import com.mapbox.mapboxsdk.annotations.MarkerOptions;
import com.mapbox.mapboxsdk.camera.CameraUpdateFactory;
import com.mapbox.mapboxsdk.geometry.LatLng;
import com.mapbox.mapboxsdk.geometry.LatLngBounds;
import com.mapbox.mapboxsdk.maps.MapView;
import com.mapbox.mapboxsdk.maps.MapboxMap;
import com.mapbox.mapboxsdk.maps.OnMapReadyCallback;
import com.mapbox.mapboxsdk.maps.Style;
import com.squareup.picasso.Picasso;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class MapFragment extends Fragment {

    private MapView mapView;
    private FloatingActionButton localizationSnapFloatingActionButton;
    private boolean isLocalizationSnapped = true;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Mapbox.getInstance(getActivity().getApplicationContext(),"pk.eyJ1IjoienluZ2kiLCJhIjoiY2wyZDhkMGVkMHd2dzNkcDk4a3R6eHIyZyJ9.dXMk-1iDSzdtvlr8Ff-8gw");
        View view = inflater.inflate(R.layout.map_fragment,container,false);

        localizationSnapFloatingActionButton = view.findViewById(R.id.localizationSnapFloatingActionButton);
        localizationSnapFloatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                autoChangeSnapMap();
            }
        });

        mapView = view.findViewById(R.id.mapView);
        mapView.onCreate(savedInstanceState);
        mapView.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(@NonNull final MapboxMap mapboxMap) {
                getDriversLocalizations();

                mapboxMap.setStyle(Style.MAPBOX_STREETS);
                mapboxMap.addOnMapClickListener(new MapboxMap.OnMapClickListener() {
                    @Override
                    public boolean onMapClick(@NonNull LatLng point) {

                        return true;
                    }
                });
                mapboxMap.addOnMoveListener(new MapboxMap.OnMoveListener() {
                    @Override
                    public void onMoveBegin(@NonNull MoveGestureDetector detector) {
                        setSnapMarkerDisable();
                    }

                    @Override
                    public void onMove(@NonNull MoveGestureDetector detector) {

                    }

                    @Override
                    public void onMoveEnd(@NonNull MoveGestureDetector detector) {

                    }
                });
            }
        });

        return view;
    }



    @Override
    public void onResume() {
        super.onResume();
        mapView.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        mapView.onPause();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mapView.onSaveInstanceState(outState);
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mapView.onDestroy();
    }

    private void autoChangeSnapMap() {
        if (isLocalizationSnapped) {
            setSnapMarkerDisable();
        } else {
            setSnapMarkerEnable();
        }
    }

    private void setSnapMarkerEnable() {
        localizationSnapFloatingActionButton.setImageResource(R.drawable.localization_snap_enable);
        isLocalizationSnapped = true;
    }

    private void setSnapMarkerDisable() {
        localizationSnapFloatingActionButton.setImageResource(R.drawable.localization_snap_disabled);
        isLocalizationSnapped = false;
    }

    public void getDriversLocalizations() {
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                StringBuilder res = new StringBuilder();
                int returnCode = 500;

                try {
                    URL url = new URL(Utils.API_ADDRESS + "/users");
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

                    conn.disconnect();
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    if (returnCode == 200) {
                        final List<MarkerOptions> markerOptions = new LinkedList<>();
                        LatLngBounds.Builder latLngBoundsBuilder = new LatLngBounds.Builder();

                        Gson g = new GsonBuilder()
                                .setDateFormat("HH:mm dd-MM-yyy")
                                .create();
                        User[] users = g.fromJson(res.toString(), User[].class);

                        for (final User user : users) {
                            if (user.getLocalization() != null && user.getLocalization().getLatitude() != null && user.getLocalization().getLongitude() != null) {
                                latLngBoundsBuilder.include(new LatLng(user.getLocalization().getLatitude(), user.getLocalization().getLongitude()));

                                try {
                                    Bitmap bitmap = Picasso.get().load(user.getAvatarUrl()).priority(Picasso.Priority.HIGH).get();

                                    markerOptions.add(new MarkerOptions()
                                            .title(user.getFirstName() + " " + user.getLastName())
                                            .snippet(user.getLocalization().toString())
                                            .position(new LatLng(user.getLocalization().getLatitude(), user.getLocalization().getLongitude()))
                                            .setIcon(createMarker(bitmap)));
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                        
                        final LatLngBounds latLngBounds = latLngBoundsBuilder.build();
                        
                        getActivity().runOnUiThread(new Runnable() {
                            public void run() {
                                mapView.getMapAsync(new OnMapReadyCallback() {
                                    @Override
                                    public void onMapReady(@NonNull final MapboxMap mapboxMap) {
                                        mapboxMap.clear();
                                        mapboxMap.addMarkers(markerOptions);

                                        if (isLocalizationSnapped) {
                                            mapboxMap.easeCamera(CameraUpdateFactory.newLatLngBounds(latLngBounds, 100), 2000);
                                        }
                                    }
                                });
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
        };

        ScheduledExecutorService service = Executors.newSingleThreadScheduledExecutor();
        service.scheduleAtFixedRate(runnable, 0, 5, TimeUnit.SECONDS);
    }
    
    private Icon createMarker(Bitmap avatar) {
        Bitmap customMarker = BitmapFactory.decodeResource(getResources(), R.drawable.maekr);
        Bitmap scaledMarker = Bitmap.createScaledBitmap(customMarker, (int) Math.round(0.5 * customMarker.getWidth()), (int) Math.round(0.5 * customMarker.getHeight()), true);
        Bitmap scaledAvatar = Bitmap.createScaledBitmap(avatar, (int) ((int) scaledMarker.getWidth() * 0.8), (int) ((int) scaledMarker.getWidth() * 0.8), true);

        Bitmap createdMarker = Bitmap.createBitmap(scaledMarker.getWidth(), scaledMarker.getHeight(), Bitmap.Config.ARGB_8888);

        Canvas comboImages = new Canvas(createdMarker);
        comboImages.drawBitmap(scaledMarker, 0f, 0f, null);
        comboImages.drawBitmap(scaledAvatar, scaledMarker.getWidth() / 2 - scaledAvatar.getWidth() / 2, scaledMarker.getWidth() / 2 - scaledAvatar.getWidth() / 2, null);
        
        return IconFactory.getInstance(getActivity()).fromBitmap(createdMarker);
    }
}