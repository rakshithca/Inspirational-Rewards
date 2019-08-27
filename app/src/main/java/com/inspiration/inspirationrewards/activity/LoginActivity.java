package com.inspiration.inspirationrewards.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.inspiration.inspirationrewards.JSON.PostDetailsAsyncTask;
import com.inspiration.inspirationrewards.JSON.StatusNotifier;
import com.inspiration.inspirationrewards.R;
import com.inspiration.inspirationrewards.utils.APIs;
import com.inspiration.inspirationrewards.utils.StoredData;
import com.inspiration.inspirationrewards.utils.Validation;

import org.json.JSONException;
import org.json.JSONObject;

public class LoginActivity extends Activity implements StatusNotifier {

    EditText usernameET, passwordET;
    CheckBox rememberMyCredentialsCB;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        usernameET = (EditText) findViewById(R.id.usernameET);
        passwordET = (EditText) findViewById(R.id.passwordET);
        rememberMyCredentialsCB = (CheckBox) findViewById(R.id.rememberMyCredentialsCB);

        ((TextView) findViewById(R.id.registrationTV)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, RegistrationActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
            }
        });

        ((Button) findViewById(R.id.loginBTN)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Validation.validateTextField(usernameET) && Validation.validateTextField(passwordET)) {
                    InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(((Button) findViewById(R.id.loginBTN)).getWindowToken(), 0);
                    loginData();
                }else{
                    LayoutInflater inflater = getLayoutInflater();
                    View custom_toast = inflater.inflate(R.layout.toast_layout,
                            (ViewGroup) findViewById(R.id.toast_root));
                    TextView tv = custom_toast.findViewById(R.id.toast_text);
                    tv.setText("Enter all the fields");
                    Toast toast = new Toast(getApplicationContext());
                    toast.setView(custom_toast);
                    toast.setDuration(Toast.LENGTH_SHORT);
                    toast.show();
                    //toast.makeText(LoginActivity.this, "Enter all the fields", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void loginData() {

        JSONObject jsonObject = new JSONObject();
        try {
//A20424771
            jsonObject.put("studentId", getResources().getString(R.string.stident_id));
            jsonObject.put("username", usernameET.getText().toString());
            jsonObject.put("password", passwordET.getText().toString());

        } catch (JSONException e) {
            e.printStackTrace();
        }
        PostDetailsAsyncTask asynctask = new PostDetailsAsyncTask(
                LoginActivity.this);
        asynctask.execute(APIs.BASE_URL + APIs.LOGIN, jsonObject.toString());
    }
    @Override
    public void OnSuccess(String response) {
        StoredData.saveString(LoginActivity.this, "loginResult", response);
        if (rememberMyCredentialsCB.isChecked()) {
            StoredData.saveBoolean(LoginActivity.this, "loginStaus", true);
        }
        Intent intent = new Intent(LoginActivity.this, ProfileActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.putExtra("defaultLogin", false);
        startActivity(intent);
        finish();
    }

    @Override
    public void OnError() {
        LayoutInflater inflater = getLayoutInflater();
        View custom_toast = inflater.inflate(R.layout.toast_layout,
                (ViewGroup) findViewById(R.id.toast_root));
        TextView tv = custom_toast.findViewById(R.id.toast_text);
        tv.setText("Login Failed");
        Toast toast = new Toast(getApplicationContext());
        toast.setView(custom_toast);
        toast.setDuration(Toast.LENGTH_LONG);
        toast.show();
       //Toast.makeText(this, "Login failed", Toast.LENGTH_SHORT).show();
    }
}
