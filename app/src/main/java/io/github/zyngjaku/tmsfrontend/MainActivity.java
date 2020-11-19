package io.github.zyngjaku.tmsfrontend;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.Menu;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.navigation.NavigationView;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.squareup.picasso.Picasso;

import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import io.github.zyngjaku.tmsfrontend.entity.User;
import io.github.zyngjaku.tmsfrontend.utils.Utils;

import static io.github.zyngjaku.tmsfrontend.entity.User.Role.FORWARDER;
import static io.github.zyngjaku.tmsfrontend.entity.User.Role.OWNER;

public class MainActivity extends AppCompatActivity {

    private AppBarConfiguration mAppBarConfiguration;
    private NavigationView navigationView;
    private TextView nameTextView, roleTextView, companyTextView;
    private ImageView imageView;
    private MenuItem item;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        navigationView = findViewById(R.id.nav_view);
        nameTextView = navigationView.getHeaderView(0).findViewById(R.id.nameTextView);
        roleTextView = navigationView.getHeaderView(0).findViewById(R.id.roleTextView);
        companyTextView = navigationView.getHeaderView(0).findViewById(R.id.companyTextView);
        imageView = navigationView.getHeaderView(0).findViewById(R.id.avatarImageView);

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.menu_map, R.id.menu_work, R.id.menu_orders, R.id.menu_employees, R.id.menu_vehicles)
                .setDrawerLayout(drawer)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);

        getUserDetails();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration) || super.onSupportNavigateUp();
    }

    private void initMenu(User user) {
        switch(user.getRole()){
            case OWNER:
                initOwnerMenu();
                break;
            case FORWARDER:
                initForwarderMenu();
                break;
            default:
                initDriverMenu();
        }
    }

    private void initOwnerMenu() {
        initForwarderMenu();
        navigationView.getMenu().findItem(R.id.menu_employees).setVisible(true);
        navigationView.getMenu().findItem(R.id.menu_vehicles).setVisible(true);
    }

    private void initForwarderMenu() {
        initDriverMenu();
        navigationView.getMenu().findItem(R.id.menu_orders).setVisible(true);
        navigationView.getMenu().findItem(R.id.menu_employees).setVisible(false);
        navigationView.getMenu().findItem(R.id.menu_vehicles).setVisible(false);
    }

    private void initDriverMenu() {
        navigationView.getMenu().findItem(R.id.menu_map).setVisible(true);
        navigationView.getMenu().findItem(R.id.menu_work).setVisible(true);
        navigationView.getMenu().findItem(R.id.menu_orders).setVisible(false);
        navigationView.getMenu().findItem(R.id.menu_employees).setVisible(false);
        navigationView.getMenu().findItem(R.id.menu_vehicles).setVisible(false);
    }

    public void getUserDetails() {
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                StringBuilder res = new StringBuilder();
                int returnCode = 500;

                try {
                    URL url = new URL(Utils.API_ADDRESS + "/user/details");
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
                        Gson g = new GsonBuilder()
                                .setDateFormat("HH:mm dd-MM-yyy")
                                .create();
                        final User user = g.fromJson(res.toString(), User.class);

                        MainActivity.this.runOnUiThread(new Runnable() {
                            public void run() {
                                nameTextView.setText(user.getFirstName() + " " + user.getLastName());
                                roleTextView.setText(user.getRole().toString());
                                companyTextView.setText(user.getCompany().getName());
                                Picasso.get().load(user.getAvatarUrl() + "&size=120").into(imageView);

                                initMenu(user);
                            }
                        });
                    }
                    else if (returnCode == 500) {
                        MainActivity.this.runOnUiThread(new Runnable() {
                            public void run() {
                                Toast.makeText(MainActivity.this, "There are some troubles with server!", Toast.LENGTH_LONG).show();
                            }
                        });
                    }
                    else {
                        MainActivity.this.runOnUiThread(new Runnable() {
                            public void run() {
                                Toast.makeText(MainActivity.this, "Username or password is incorrect!", Toast.LENGTH_LONG).show();
                            }
                        });
                    }
                }
            }
        });

        thread.start();
    }
}