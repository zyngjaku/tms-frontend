package io.github.zyngjaku.tmsfrontend;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.coordinatorlayout.widget.CoordinatorLayout;

import com.google.android.material.snackbar.Snackbar;

public class SignupActivity extends AppCompatActivity {

    CoordinatorLayout coordinatorLayout;
    private ProgressBar progressBar;
    private Button createCompanyButton, loginButton;
    private EditText companyNameEditText, companyAddressEditText, companyCityEditText, companyZipCodeEditText, companyCountryEditText,
                     firstNameEditText, lastNameEditText, emailEditText, passwordEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.signup_activity);
        initVariables();

        createCompanyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startProgress();
                validateAllEditTextLabels();
                finishProgress();
            }
        });

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent newIntent = new Intent(SignupActivity.this, LoginActivity.class);
                SignupActivity.this.startActivity(newIntent);
                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);

                SignupActivity.this.finish();
            }
        });
    }

    private void initVariables() {
        coordinatorLayout = findViewById(R.id.layout);

        progressBar = findViewById(R.id.progressBar);
        createCompanyButton = findViewById(R.id.createCompanyButton);
        loginButton = findViewById(R.id.loginButton);

        companyNameEditText = findViewById(R.id.companyNameEditText);
        companyAddressEditText = findViewById(R.id.companyAddressEditText);
        companyCityEditText = findViewById(R.id.companyCityEditText);
        companyZipCodeEditText = findViewById(R.id.companyZipCodeEditText);
        companyCountryEditText = findViewById(R.id.companyCountryEditText);
        firstNameEditText = findViewById(R.id.firstNameEditText);
        lastNameEditText = findViewById(R.id.lastNameEditText);
        emailEditText = findViewById(R.id.emailEditText);
        passwordEditText = findViewById(R.id.passwordEditText);
    }

    private void startProgress() {
        createCompanyButton.setText("");
        progressBar.setVisibility(View.VISIBLE);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE, WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
    }

    private void finishProgress() {
        createCompanyButton.setText("CREATE");
        progressBar.setVisibility(View.GONE);

        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
    }

    public boolean validateAllEditTextLabels() {
        int[] editTextIds = new int[]{R.id.companyNameEditText, R.id.companyAddressEditText, R.id.companyCityEditText, R.id.companyZipCodeEditText,
                R.id.companyCountryEditText, R.id.firstNameEditText, R.id.lastNameEditText, R.id.emailEditText, R.id.passwordEditText};

        for (int editTextId : editTextIds) {
            EditText editText = findViewById(editTextId);
            String editTextValue = editText.getText().toString();
            if (TextUtils.isEmpty(editTextValue)) {
                editText.setError("This field cannot be empty");
            } else if (editTextValue.length() <= 3) {
                editText.setError("Value must be longer than 3 characters");
            }
        }

        return false;
    }
}
