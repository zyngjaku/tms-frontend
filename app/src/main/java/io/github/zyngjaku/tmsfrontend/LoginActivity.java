package io.github.zyngjaku.tmsfrontend;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import io.github.zyngjaku.tmsfrontend.utils.Utils;

public class LoginActivity extends AppCompatActivity {

    private ProgressBar progressBar;
    private EditText emailEditText, passwordEditText;
    private Button loginButton, createCompanyButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_activity);

        progressBar = findViewById(R.id.progressBar);
        emailEditText = findViewById(R.id.emailEditText);
        passwordEditText = findViewById(R.id.passwordEditText);
        loginButton = findViewById(R.id.loginButton);
        createCompanyButton = findViewById(R.id.createCompanyButton);

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startProgress();
                sendSignInRequest(emailEditText.getText().toString(), passwordEditText.getText().toString());
            }
        });

        createCompanyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent newIntent = new Intent(LoginActivity.this, SignupActivity.class);
                LoginActivity.this.startActivity(newIntent);
                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
            }
        });

    }

    private void startProgress() {
        loginButton.setText("");
        progressBar.setVisibility(View.VISIBLE);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE, WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
    }

    private void finishProgress() {
        loginButton.setText("LOG IN");
        progressBar.setVisibility(View.GONE);

        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
    }

    public void sendSignInRequest(final String email, final String password) {
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                int returnCode = 100;

                try {
                    URL url = new URL(Utils.API_ADDRESS + "/authenticate");
                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                    conn.setRequestMethod("POST");
                    conn.setRequestProperty("Content-Type", "application/json;charset=UTF-8");
                    conn.setRequestProperty("Accept","application/json");
                    conn.setDoOutput(true);
                    conn.setDoInput(true);

                    JSONObject jsonParam = new JSONObject();
                    jsonParam.put("mail", email);
                    jsonParam.put("password", password);

                    DataOutputStream os = new DataOutputStream(conn.getOutputStream());
                    os.writeBytes(jsonParam.toString());
                    os.flush();
                    os.close();

                    returnCode = conn.getResponseCode();

                    BufferedReader br = new BufferedReader(new InputStreamReader((conn.getInputStream())));
                    String output;
                    StringBuilder res = new StringBuilder();
                    while ((output = br.readLine()) != null) {
                        res.append(output);
                    }

                    JSONObject obj = new JSONObject(res.toString());
                    System.setProperty("token", obj.getString("token"));

                    conn.disconnect();
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    if (returnCode == 200) {
                        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                        startActivity(intent);
                        finish();
                    }
                    else if (returnCode == 100 || returnCode == 500) {
                        LoginActivity.this.runOnUiThread(new Runnable() {
                            public void run() {
                                finishProgress();
                                Toast.makeText(getApplicationContext(), "There are some troubles with server!", Toast.LENGTH_LONG).show();
                            }
                        });
                    }
                    else {
                        LoginActivity.this.runOnUiThread(new Runnable() {
                            public void run() {
                                finishProgress();
                                Toast.makeText(getApplicationContext(), "Username or password is incorrect!", Toast.LENGTH_LONG).show();
                            }
                        });
                    }
                }
            }
        });

        thread.start();
    }

}
