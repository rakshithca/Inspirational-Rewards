package com.inspiration.inspirationrewards.activity;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.inspiration.inspirationrewards.JSON.PostDetailsAsyncTask;
import com.inspiration.inspirationrewards.JSON.PutDetailsAsyncTask;
import com.inspiration.inspirationrewards.JSON.StatusNotifier;
import com.inspiration.inspirationrewards.R;
import com.inspiration.inspirationrewards.services.AppLocationService;
import com.inspiration.inspirationrewards.utils.APIs;
import com.inspiration.inspirationrewards.utils.LocationAddress;
import com.inspiration.inspirationrewards.utils.NetworkCheck;
import com.inspiration.inspirationrewards.utils.StoredData;
import com.inspiration.inspirationrewards.utils.Validation;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EditProfileActivity extends Activity implements View.OnClickListener,StatusNotifier {

    EditText usernameET, passwordET, firstNameET, lastNameET, departmentET, positionET, yourStoryET;
    CheckBox adminCB;
    ImageView iv_profile_image;
    Uri mCapturedImageURI;
    Bitmap bitmap;
    String My_Address = "",studentID = "",json_data = "";
    static int Capture_Camera = 100;
    static int Gallery_Images = 196709;
    public static final int LOCATION_PERMISSION_REQUEST = 3;
    public static final int CAMERA_PERMISSION_REQUEST = 1;
    public static final int READ_EXTERNAL_STORAGE_REQUEST_ID = 2;
    public static final int MULTI_PERMISSION_REQUEST = 4;
    byte[] image_data = null;
    AppLocationService appLocationService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        initViews();
        assignValuesToViews();

        ((ImageView) findViewById(R.id.backIconIMG)).setOnClickListener(this);
        ((ImageView) findViewById(R.id.saveIMG)).setOnClickListener(this);
        iv_profile_image.setOnClickListener(this);

        My_Address = "";
        if (Build.VERSION.SDK_INT >= 23) {
            int hasCameraPermission = checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION);
            if (hasCameraPermission != PackageManager.PERMISSION_GRANTED) {
//                                permissionCheck();
                requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        LOCATION_PERMISSION_REQUEST);
            } else {
                getAddress();
            }
        }else{
            getAddress();
        }
    }


    private void getAddress() {
        appLocationService = new AppLocationService(
                EditProfileActivity.this);

        Location location = appLocationService
                .getLocation(LocationManager.NETWORK_PROVIDER);

        if(location == null){
            location = appLocationService.getLocation(LocationManager.GPS_PROVIDER);
        }

        //To hard-code the lat & long if you have issues with getting it.
        //remove the below if-condition and use the following couple of lines.
        //double latitude = 37.422005;
        //double longitude = -122.084095

        if (location != null) {
            double latitude = location.getLatitude();
            double longitude = location.getLongitude();
            LocationAddress locationAddress = new LocationAddress();
            locationAddress.getAddressFromLocation(latitude, longitude,
                    getApplicationContext(), new GeocoderHandler());
        } else {
            showSettingsAlert();

        }
    }

    public void showSettingsAlert() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(
                EditProfileActivity.this);
        alertDialog.setTitle("SETTINGS");
        alertDialog.setMessage("Enable Location Provider! Go to settings menu?");
        alertDialog.setPositiveButton("Settings",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent(
                                Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                        EditProfileActivity.this.startActivity(intent);
                    }
                });
        alertDialog.setNegativeButton("Cancel",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                        showCustomToast("Please Enable Location Service", Toast.LENGTH_LONG);
                        finish();
                    }
                });
        alertDialog.show();
    }

    private void initViews() {
        usernameET = (EditText) findViewById(R.id.usernameET);
        passwordET = (EditText) findViewById(R.id.passwordET);
        firstNameET = (EditText) findViewById(R.id.firstNameET);
        lastNameET = (EditText) findViewById(R.id.lastNameET);
        departmentET = (EditText) findViewById(R.id.departmentET);
        positionET = (EditText) findViewById(R.id.positionET);
        yourStoryET = (EditText) findViewById(R.id.yourStoryET);
        adminCB = (CheckBox) findViewById(R.id.adminCB);
        iv_profile_image = (ImageView) findViewById(R.id.iv_profile_image);
    }

    private void assignValuesToViews() {
        try {
            if (StoredData.getString(EditProfileActivity.this, "loginResult") != null) {

                JSONObject jsonObject = new JSONObject(StoredData.getString(EditProfileActivity.this, "loginResult"));
                firstNameET.setText(jsonObject.getString("firstName") );
                lastNameET.setText(jsonObject.getString("lastName"));
                usernameET.setText(jsonObject.getString("username"));
                passwordET.setText(jsonObject.getString("password"));
                adminCB.setChecked(jsonObject.getBoolean("admin"));
                departmentET.setText(jsonObject.getString("department"));
                positionET.setText(jsonObject.getString("position"));
                yourStoryET.setText(jsonObject.getString("story"));
                studentID = jsonObject.getString("studentId");

                byte[] decodedString = Base64.decode(jsonObject.getString("imageBytes"), Base64.DEFAULT);
                Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
                iv_profile_image.setImageBitmap(decodedByte);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
    @Override
    public void onClick(View view) {

        switch (view.getId())
        {
            case R.id.backIconIMG:
                finish();
                break;
            case R.id.iv_profile_image:
                final Dialog dialog = new Dialog(EditProfileActivity.this);
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                dialog.setContentView(R.layout.select_picture);
                Button camera_btn = (Button) dialog.findViewById(R.id.btn_takephoto);
                Button gallery_btn = (Button) dialog.findViewById(R.id.btn_choosephoto);
                Button btn_cancel = (Button) dialog.findViewById(R.id.btn_cancel);

                btn_cancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        dialog.dismiss();
                    }
                });
                camera_btn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (Build.VERSION.SDK_INT >= 23) {
                            int hasCameraPermission = checkSelfPermission(Manifest.permission.CAMERA);
                            if (hasCameraPermission != PackageManager.PERMISSION_GRANTED) {
                                permissionCheck();
                            } else {
                                String fileName = "temp.jpg";
                                ContentValues values = new ContentValues();
                                values.put(MediaStore.Images.Media.TITLE, fileName);
                                mCapturedImageURI = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
                                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                                intent.putExtra(MediaStore.EXTRA_OUTPUT, mCapturedImageURI);
                                startActivityForResult(intent, Capture_Camera);
                            }
                        } else {
                            String fileName = "temp.jpg";
                            ContentValues values = new ContentValues();
                            values.put(MediaStore.Images.Media.TITLE, fileName);
                            mCapturedImageURI = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
                            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                            intent.putExtra(MediaStore.EXTRA_OUTPUT, mCapturedImageURI);
                            startActivityForResult(intent, Capture_Camera);
                        }
                        dialog.dismiss();
                    }
                });
                /* Taking Gallery image to update profile picture*/
                gallery_btn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // Send Analytics

                        if (Build.VERSION.SDK_INT >= 23) {

                            int hasStoragePermission = checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE);
                            if (hasStoragePermission != PackageManager.PERMISSION_GRANTED) {
                                requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                                        READ_EXTERNAL_STORAGE_REQUEST_ID);
                            } else {
                                Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
                                photoPickerIntent.setType("image/*");
                                startActivityForResult(photoPickerIntent, Gallery_Images);
                                //Crop.pickImage(PersonalInfoActivity.this);
                            }
                        } else {
                            Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
                            photoPickerIntent.setType("image/*");
                            startActivityForResult(photoPickerIntent, Gallery_Images);
                            //Crop.pickImage(PersonalInfoActivity.this);
                        }
                        dialog.dismiss();
                    }
                });
                dialog.show();
                break;
            case R.id.saveIMG:
                if (NetworkCheck.isInternetAvailable(EditProfileActivity.this)) {
                    if (isValidData() == null) {
                        final Dialog d = new Dialog(EditProfileActivity.this);
                        d.requestWindowFeature(Window.FEATURE_NO_TITLE);
                        d.setContentView(R.layout.save_changes);
                        Button scancel = d.findViewById(R.id.btn_scancel);
                        Button okay = d.findViewById(R.id.btn_ok);
                        okay.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                d.dismiss();
                                InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                                imm.hideSoftInputFromWindow(((ImageView) findViewById(R.id.saveIMG)).getWindowToken(), 0);
                                saveUserData();
                            }
                        });
                        scancel.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                d.dismiss();
                                showCustomToast("Profile Not updated", Toast.LENGTH_LONG);
                            }
                        });
                        d.show();

                    } else {
                        showCustomToast(""+isValidData(), Toast.LENGTH_SHORT);
                    }
                } else {
                    showCustomToast("Please check network connection",Toast.LENGTH_SHORT);
                }
                break;
        }
    }

    private void showCustomToast(String msg, int length) {
        LayoutInflater inflater = getLayoutInflater();
        View custom_toast = inflater.inflate(R.layout.toast_layout,
                (ViewGroup) findViewById(R.id.toast_root));
        TextView tv = custom_toast.findViewById(R.id.toast_text);
        tv.setText(msg);
        Toast toast = new Toast(getApplicationContext());
        toast.setView(custom_toast);
        toast.setDuration(length);
        toast.show();
    }
    private void saveUserData() {
        try {
            String encodedImage = "";
            JSONObject previous_jsonObject = new JSONObject(StoredData.getString(EditProfileActivity.this, "loginResult"));
            if (image_data != null) {
                encodedImage = Base64.encodeToString(image_data, Base64.DEFAULT);
            }else{
                encodedImage = previous_jsonObject.getString("imageBytes");
            }
            JSONObject jsonObject = new JSONObject();
            try {
//A20424771
                jsonObject.put("studentId", studentID);
                jsonObject.put("username", usernameET.getText().toString());
                jsonObject.put("password", passwordET.getText().toString());
                jsonObject.put("firstName", firstNameET.getText().toString());
                jsonObject.put("lastName", lastNameET.getText().toString());
                jsonObject.put("position", positionET.getText().toString());
                jsonObject.put("pointsToAward", previous_jsonObject.getInt("pointsToAward"));
                jsonObject.put("department", departmentET.getText().toString());
                jsonObject.put("story", yourStoryET.getText().toString());
                jsonObject.put("admin", adminCB.isChecked());
                jsonObject.put("location", ""+My_Address);
                jsonObject.put("rewardRecords", previous_jsonObject.getJSONArray("rewards"));
                jsonObject.put("imageBytes", encodedImage);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            json_data = jsonObject.toString();
            PutDetailsAsyncTask asynctask = new PutDetailsAsyncTask(EditProfileActivity.this);
            asynctask.execute(APIs.BASE_URL + APIs.REGISTRATION, jsonObject.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private String isValidData() {
        if (Validation.validateTextField(usernameET) && Validation.validateTextField(passwordET) && Validation.validateTextField(firstNameET) &&
                Validation.validateTextField(lastNameET) && Validation.validateTextField(departmentET) && Validation.validateTextField(positionET) &&
                Validation.validateTextField(yourStoryET)) {
            return null;
        } else {
            return "Enter all the fields";
        }
    }

    /*Checking for permissions*/
    private void permissionCheck() {
        List<String> permissionsNeeded = new ArrayList<String>();

        final List<String> permissionsList = new ArrayList<String>();
        if (!addPermission(permissionsList, Manifest.permission.CAMERA))
            permissionsNeeded.add("CAMERA");
        if (!addPermission(permissionsList, Manifest.permission.WRITE_EXTERNAL_STORAGE))
            permissionsNeeded.add("WRITE_EXTERNAL_STORAGE");
        if (!addPermission(permissionsList, Manifest.permission.READ_EXTERNAL_STORAGE))
            permissionsNeeded.add("READ_EXTERNAL_STORAGE");
        if (permissionsList.size() > 0) {
            if (permissionsNeeded.size() > 0) {
                // Need Rationale
                String message = "You need to grant access to "
                        + permissionsNeeded.get(0);
                for (int i = 1; i < permissionsNeeded.size(); i++)
                    message = message + ", " + permissionsNeeded.get(i);
                showMessageOKCancel(message,
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog,
                                                int which) {
                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                    requestPermissions(permissionsList
                                                    .toArray(new String[permissionsList
                                                            .size()]),
                                            MULTI_PERMISSION_REQUEST);
                                }
                            }
                        });
                return;
            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestPermissions(
                        permissionsList.toArray(new String[permissionsList.size()]),
                        MULTI_PERMISSION_REQUEST);
            }
            return;
        }
    }
    /*Adding new permissions*/
    private boolean addPermission(List<String> permissionsList,
                                  String permission) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(permission) != PackageManager.PERMISSION_GRANTED) {
                permissionsList.add(permission);
                // Check for Rationale Option
                if (!shouldShowRequestPermissionRationale(permission))
                    return false;
            }
        }
        return true;
    }

    private void showMessageOKCancel(String message,
                                     android.content.DialogInterface.OnClickListener onClickListener) {
        new android.support.v7.app.AlertDialog.Builder(EditProfileActivity.this).setMessage(message)
                .setPositiveButton("OK", onClickListener).setCancelable(false)
                .setNegativeButton("Cancel", null).create().show();

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        switch (requestCode) {
            case MULTI_PERMISSION_REQUEST: {
                Map<String, Integer> perms = new HashMap<String, Integer>();
                // Initial
                perms.put(Manifest.permission.CAMERA,
                        PackageManager.PERMISSION_GRANTED);
                perms.put(Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        PackageManager.PERMISSION_GRANTED);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                    perms.put(Manifest.permission.READ_EXTERNAL_STORAGE,
                            PackageManager.PERMISSION_GRANTED);
                }
                // Fill with results
                for (int i = 0; i < permissions.length; i++)
                    perms.put(permissions[i], grantResults[i]);
                // Check for ACCESS_FINE_LOCATION
                if (perms.get(Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED
                        && perms.get(Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED
                        && perms.get(Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED
                        ) {
                    // All Permissions Granted
                    openCamera();
                } else {
                    // Permission Denied
                    permissionCheck();
                    showCustomToast("Some Permission is denied", Toast.LENGTH_LONG);
                }
            }
            break;
            case CAMERA_PERMISSION_REQUEST: {
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // Permission Granted
                    openCamera();
                }
            }
            break;
            case READ_EXTERNAL_STORAGE_REQUEST_ID: {
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // Permission Granted
                    Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
                    photoPickerIntent.setType("image/*");
                    startActivityForResult(photoPickerIntent, Gallery_Images);
                }
            }
            break;
            case LOCATION_PERMISSION_REQUEST:{
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    getAddress();
                }else{
                    showCustomToast("Please Enable Location Service", Toast.LENGTH_SHORT);
                    finish();
                }
            }
            break;
            default:
                super.onRequestPermissionsResult(requestCode, permissions,
                        grantResults);
        }
    }
    /*Launch Camera */
    private void openCamera() {
        String fileName = "temp.jpg";
        ContentValues values = new ContentValues();
        values.put(MediaStore.Images.Media.TITLE, fileName);
        mCapturedImageURI = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, mCapturedImageURI);
        startActivityForResult(intent, Capture_Camera);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == Gallery_Images&& resultCode == RESULT_OK) {
            try {
                processGallery(data);
            } catch (Exception e) {
                showCustomToast("onActivityResult: " + e.getMessage(), Toast.LENGTH_SHORT);
                e.printStackTrace();
            }
        } else if (requestCode == Capture_Camera  && resultCode == RESULT_OK) {
            try {
                processCamera();
            } catch (Exception e) {
                showCustomToast("onActivityResult: " + e.getMessage(), Toast.LENGTH_SHORT);
                e.printStackTrace();
            }
        }
    }

    private void processCamera() {
        ((ImageView) findViewById(R.id.iv_profile_image)).setImageURI(mCapturedImageURI);
        try {
            ContentResolver cr = getBaseContext().getContentResolver();
            InputStream inputStream = cr.openInputStream(mCapturedImageURI);
            Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 40, baos);
            image_data = baos.toByteArray();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    private void processGallery(Intent data) {
        Uri galleryImageUri = data.getData();
        if (galleryImageUri == null)
            return;

        InputStream imageStream = null;
        try {
            imageStream = getContentResolver().openInputStream(galleryImageUri);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        bitmap = BitmapFactory.decodeStream(imageStream);
        ((ImageView) findViewById(R.id.iv_profile_image)).setImageBitmap(bitmap);
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 40, stream);
        byte[] byteArray = stream.toByteArray();
        image_data = byteArray;
    }

    @Override
    public void OnSuccess(String response) {
        LayoutInflater inflater = getLayoutInflater();
        showCustomToast(response, Toast.LENGTH_SHORT);
        if (response.equals("Profile Updated Successfully"))
        {
            StoredData.saveString(EditProfileActivity.this, "loginResult", json_data);
            Intent intent = new Intent(EditProfileActivity.this, ProfileActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            intent.putExtra("defaultLogin", true);
            startActivity(intent);
            finish();
        }
    }

    @Override
    public void OnError() {
        showCustomToast("Error while Creating User: " , Toast.LENGTH_SHORT);
    }

    private class GeocoderHandler extends Handler {
        @Override
        public void handleMessage(Message message) {
            String locationAddress;
            switch (message.what) {
                case 1:
                    Bundle bundle = message.getData();
                    locationAddress = bundle.getString("address");
                    break;
                default:
                    locationAddress = null;
            }
            if (locationAddress != null && !locationAddress.equals(""))
            {
                My_Address = locationAddress;
            }
            Log.e("Address",""+locationAddress);
        }
    }
}
