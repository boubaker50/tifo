package com.thephoenixit.walidchaieb;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.hardware.Camera;
import android.hardware.Camera.Parameters;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraManager;
import android.location.Address;
import android.location.Geocoder;
import android.media.AudioManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.StrictMode;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.provider.Settings;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.crashlytics.android.Crashlytics;
import com.google.gson.Gson;

import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;

import io.fabric.sdk.android.Fabric;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.Manifest.permission.ACCESS_COARSE_LOCATION;
import static android.Manifest.permission.ACCESS_FINE_LOCATION;
import static android.Manifest.permission.CAMERA;
import static android.Manifest.permission.GET_ACCOUNTS;
import static android.Manifest.permission.INTERNET;
import static android.Manifest.permission.READ_EXTERNAL_STORAGE;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;
import static android.Manifest.permission.WRITE_SETTINGS;

public class MainActivity extends AppCompatActivity {
    TextInputEditText mylocationeditText;
    ArrayList<String> neededFiles = new ArrayList<String>();
    LocationTrack locationTrack;
    ProgressBar progressBar;
    Handler handler;
    ImageView picture;
    String cameraId;
    int delay = 1000;
    Boolean firstTime = true;
    Date finalDate = null;
    public static Camera cam = null;// has to be static, otherwise onDestroy() destroys it
    private static final String TAG = "MainActivity";
    private int brightness;
    private ContentResolver cResolver;
    private Window window;
    private Button submit;
    int execute = 0, hide = 0;
    private EditText chairEditText;
    private String theURL;
    private TextInputLayout textInputLayout;
    private TextInputLayout textInputLayout2;
    private ConstraintLayout theBackGround;
    DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
    DateFormat dateFormatfull = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    int firstOccuranceOfSlash;
    int sequenceCounter = 0;
    File imgFile;
    CameraManager camManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Fabric.with(this, new Crashlytics());
        setContentView(R.layout.activity_main);
        getWindow().addFlags(android.view.WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (!Settings.System.canWrite(getApplicationContext())) {
                Intent intent = new Intent(android.provider.Settings.ACTION_MANAGE_WRITE_SETTINGS);
                intent.setData(Uri.parse("package:" + getApplicationContext().getPackageName()));
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            }
        }
        camManager = (CameraManager) getSystemService(Context.CAMERA_SERVICE);

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        submit = findViewById(R.id.button2);
        picture = findViewById(R.id.imageView);
        mylocationeditText = findViewById(R.id.mylocationeditText);
        progressBar = findViewById(R.id.progressBar2);
        chairEditText = findViewById(R.id.chairEditText);
        textInputLayout = findViewById(R.id.textInputLayout);
        textInputLayout2 = findViewById(R.id.textInputLayout2);
        theBackGround = findViewById(R.id.theBackGround);
        theBackGround.setVisibility(View.GONE);
        cResolver = getContentResolver();
        window = getWindow();
        try {
            Settings.System.putInt(cResolver,
                    Settings.System.SCREEN_BRIGHTNESS_MODE, Settings.System.SCREEN_BRIGHTNESS_MODE_MANUAL);
            brightness = android.provider.Settings.System.getInt(cResolver, Settings.System.SCREEN_BRIGHTNESS);
        } catch (Exception e) {
            e.printStackTrace();
        }
        brightnessToMAX();
        soundToMAX();
        int MyVersion = Build.VERSION.SDK_INT;
        if (MyVersion > Build.VERSION_CODES.LOLLIPOP_MR1) {
            if (!checkIfAlreadyhavePermission()) {
                requestForSpecificPermission();
            }
        }
        mylocationeditText.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                final int DRAWABLE_RIGHT = 2;
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    if (event.getRawX() >= (mylocationeditText.getRight() - mylocationeditText.getCompoundDrawables()[DRAWABLE_RIGHT].getBounds().width())) {
                        progressBar.setVisibility(View.VISIBLE);
                        locationTrack = new LocationTrack(MainActivity.this);
                        if (locationTrack.canGetLocation()) {

                            double longitude = locationTrack.getLongitude();
                            double latitude = locationTrack.getLatitude();
                            getAddresse(latitude, longitude);

                        } else {

                            locationTrack.showSettingsAlert();
                        }
                        return true;
                    }
                }
                return false;
            }
        });
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!chairEditText.getText().toString().isEmpty()) {
                    ApiService api = RetroClient.getApiService();
                    Call<String> call = api.getsequence("http://82.231.56.233/api/enchainement/" + chairEditText.getText().toString());
                    call.enqueue(new Callback<String>() {
                        @Override
                        public void onResponse(Call<String> call, Response<String> response) {
                            try {
                                try {
                                    hideKeyboardFrom(getApplicationContext(), getCurrentFocus());
                                } catch (Exception e) {

                                }
                                Gson gson = new Gson();
                                sequence[] receivedData = gson.fromJson(response.body(), sequence[].class);
                                final ArrayList<sequence> theNeededData = new ArrayList<sequence>(Arrays.asList(receivedData));
                                theNeededData.get(0).setHeureExecution("00:16:00");
                                theNeededData.get(0).      setHeureFin("00:16:30");
                                theNeededData.get(1).setHeureExecution("00:17:00");
                                theNeededData.get(1).      setHeureFin("00:17:30");
                                for (int i = 0; i < theNeededData.size(); i++)
                                    Log.e(TAG, "onResponse: The object: " + theNeededData.get(i).toString());
                                buzzBeforeTwoMinutes(theNeededData.get(0));
                                traitTheJSONReceived(theNeededData);
                                theURL = theNeededData.get(0).getCheminSon().replace("ftp://", "");
                                firstOccuranceOfSlash = findInStr(theURL, '/');
                                theURL = theURL.substring(0, firstOccuranceOfSlash);
                                theSequenceBegins();
                                for (int i = 0; i < theNeededData.size(); i++) {
                                    try {
                                        neededFiles.add(theNeededData.get(i).getCheminSon().substring(theNeededData.get(i).getCheminSon().indexOf(theURL) + theURL.length()));
                                        neededFiles.add(theNeededData.get(i).getCheminImage().substring(theNeededData.get(i).getCheminSon().indexOf(theURL) + theURL.length()));
                                    } catch (IndexOutOfBoundsException e) {
                                    }
                                }
                                neededFiles = removeRedundancy(neededFiles);
                                new Thread(new Runnable() {
                                    @Override
                                    public void run() {
                                        downloadFilesFromFTP(theURL.substring(0, firstOccuranceOfSlash), "user", "user", neededFiles, theNeededData);
                                    }
                                }).start();

                            } catch (IndexOutOfBoundsException e) {
                                Toast.makeText(getApplicationContext(), "Une erreur est survenu, verifier le numero de votre siege.", Toast.LENGTH_LONG).show();
                                chairEditText.setError("verifier le numero du siege");
                                Log.e(TAG, "onResponse: " + e.getMessage());
                            }
                        }

                        @Override
                        public void onFailure(Call<String> call, Throwable t) {
                            Log.e(TAG, "onFailure: " + t.getMessage());
                        }
                    });
                } else
                    chairEditText.setError("Le numero du siege est obligatoire.");

            }
        });
    }

    public ArrayList<String> removeRedundancy(ArrayList<String> myList) {
        Set<String> set = new HashSet<>(myList);
        myList.clear();
        myList.addAll(set);
        return myList;
    }

    private FTPClient downloadFilesFromFTP(String url, String username, String password, ArrayList<String> neededFiles, ArrayList<sequence> theNeededData) {
        File directory = new File(Environment.getExternalStorageDirectory() + File.separator + "Walid");
        final File imgFile = new File(Environment.getExternalStorageDirectory() + File.separator + "Walid" + File.separator + theNeededData.get(0).getCheminImage().substring(theNeededData.get(0).getCheminImage().lastIndexOf('/') + 1));
        Log.e(TAG, "downloadFilesFromFTP: " + imgFile.getAbsolutePath());
        Log.e(TAG, "downloadFilesFromFTP File existance: " + imgFile.exists());
        theSequenceBegins();
        runOnUiThread(new Runnable() {

            @Override
            public void run() {

                if (imgFile.exists()) {
                    Bitmap myBitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
                    picture.setImageBitmap(myBitmap);
                    picture.setVisibility(View.VISIBLE);
                }
            }
        });

        if (!directory.exists())
            directory.mkdirs();
        FTPClient client = new FTPClient();
        if (!client.isConnected()) {
            try {
                client.connect(url, 21);
                client.login(username, password);
            } catch (IOException e) {
            }
            for (int i = 0; i < neededFiles.size(); i++) {
                try {
                    if (client.listFiles(neededFiles.get(i)).length > 0) {
                        client.setFileType(FTP.BINARY_FILE_TYPE);
                        client.enterLocalPassiveMode();
                        OutputStream outputStream = null;
                        boolean success = false;
                        int lasIndexOF = neededFiles.get(i).lastIndexOf('/');
                        File localFile = new File(Environment.getExternalStorageDirectory() + File.separator + "Walid" + File.separator + neededFiles.get(i).substring(lasIndexOF + 1));

                        if (!localFile.exists()) {
                            localFile.createNewFile();
                            try {
                                outputStream = new BufferedOutputStream(new FileOutputStream(localFile));
                                success = client.retrieveFile(neededFiles.get(i), outputStream);
                                if (success) {
                                    Log.e(TAG, "downloadFilesFromFTP: File created");
                                    if (imgFile.exists()) {
                                        final Bitmap myBitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
                                        runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                picture.setImageBitmap(myBitmap);
                                            }
                                        });
                                        picture.setVisibility(View.VISIBLE);
                                    }
                                } else
                                    Log.e(TAG, "downloadFilesFromFTP: Problem occurred");
                            } finally {
                                if (outputStream != null) {
                                    outputStream.close();
                                }
                            }
                        }
                    } else
                        Log.e(TAG, "downloadFilesFromFTP: File doesn\'t exists");
                } catch (IOException e) {
                }
            }
        }
        return client;
    }

    private void traitTheJSONReceived(final ArrayList<sequence> receivedData) {
        //for (int i = 0; i < receivedData.size(); i++) {
        handler = new Handler();
        Date date = new Date();
        //theSequenceBegins();
        final String begin = dateFormat.format(date) + " " + receivedData.get(sequenceCounter).getHeureExecution();
        final String end = dateFormat.format(date) + " " + receivedData.get(sequenceCounter).getHeureFin();
        try {
            finalDate = dateFormatfull.parse(end);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        final Timer t = new Timer();
        try {
            final int finalI = sequenceCounter;
            t.schedule(new TimerTask() {
                public void run() {
                    Runnable runnable = new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getApplicationContext(), "Executed here", Toast.LENGTH_LONG).show();
                            if (firstTime) {
                                firstTime = false;
                                imgFile = new File(Environment.getExternalStorageDirectory() + File.separator + "Walid" + File.separator + receivedData.get(finalI).getCheminImage().substring(receivedData.get(finalI).getCheminImage().lastIndexOf('/') + 1));
                                theSequenceBegins();
                                Intent mIntent = new Intent(getApplicationContext(), songService.class);
                                mIntent.putExtra("song", receivedData.get(finalI).getCheminSon());
                                startService(mIntent);
                                if (imgFile.exists()) {
                                    Bitmap myBitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
                                    picture.setImageBitmap(myBitmap);
                                }
                            }
                            Date now = new Date();
                            if (finalDate.getTime() - now.getTime() < 0) {
                                t.cancel();
                                t.purge();
                                flashLightOff();
                                picture.setVisibility(View.GONE);
                                theSequenceEnds();
                                Log.e(TAG, "run: Counter stopped at: " + finalDate);
                                stopService(new Intent(getApplicationContext(), songService.class));
                                sequenceCounter++;
                                firstTime = true;
                                if (sequenceCounter < receivedData.size())
                                    traitTheJSONReceived(receivedData);
                                return;
                            } else {
                                if (execute < receivedData.get(finalI).getDureeAffichage()) {
                                    if (!imgFile.exists())
                                        flashLightOn();
                                    execute++;
                                    picture.setVisibility(View.VISIBLE);
                                    Log.e(TAG, "run: Show");
                                } else if (hide < receivedData.get(finalI).getDureeEteint()) {
                                    if (!imgFile.exists())
                                        flashLightOff();
                                    hide++;
                                    picture.setVisibility(View.GONE);
                                    Log.e(TAG, "run: Hide");
                                } else {
                                    execute = 0;
                                    hide = 0;
                                }
                            }
                            handler.postDelayed(this, 1000);
                        }
                    };
                    handler.postDelayed(runnable, 1000);
                    Log.e(TAG, "run: Executed at " + begin);
                }
            }, new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(begin));
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }
    //}

    public void buzzBeforeTwoMinutes(sequence sequence) {
        Date date = new Date();
        final String begin = dateFormat.format(date) + " " + sequence.getHeureExecution();
        Date Date = null;
        Date Date2 = null;
        try {
            Date2 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(begin);
            Date = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(begin);
            Date.setMinutes(Date.getMinutes() - 2);
        } catch (ParseException e) {
        }
        Date now = Calendar.getInstance().getTime();
        if (now.getTime() - Date2.getTime() > 0) {
            Toast.makeText(getApplicationContext(), "Pas d’évènement à exécuter", Toast.LENGTH_LONG).show();

        } else if (now.getTime() - Date.getTime() > 0)
            Toast.makeText(getApplicationContext(), "Un evenement va commencer bientot", Toast.LENGTH_LONG).show();
        else {
            Timer _timer = new Timer();
            _timer.schedule(new TimerTask() {
                @Override
                public void run() {
                    MainActivity.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(MainActivity.this, "Preparez vous, l\'enchenement va commencer après deux minutes.", Toast.LENGTH_LONG).show();
                        }
                    });
                    vibrate(5000);
                    return;
                }

            }, Date);
        }
    }

    public Address getSameAddresse(double latitude, double longitude) {
        Log.d("Latide", String
                .valueOf(latitude));
        Log.d("longitude", String
                .valueOf(longitude));
        Geocoder geocoder;
        List<Address> addresses;
        geocoder = new Geocoder(getApplicationContext().getApplicationContext(), Locale.getDefault());

        try {
            addresses = geocoder.getFromLocation(latitude, longitude, 1); // Here 1 represent max location result to returned, by documents it recommended 1 to 5
            return addresses.get(0);


        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    public void getAddresse(double latitude, double longitude) {

        Address locationAddress = getSameAddresse(latitude, longitude);

        if (locationAddress != null) {
            String address = locationAddress.getAddressLine(0);
            String currentLocation;

            if (!TextUtils.isEmpty(address)) {
                currentLocation = address;
                progressBar.setVisibility(View.GONE);
                ArrayList<String> stadiums = new ArrayList<>();
                stadiums.add("Parc Des Princes");
                stadiums.add("Camp NOU");
                for (int i = 0; i < stadiums.size(); i++) {
                    String theLocation = currentLocation;
                    // String theLocation = "CAMP NOU, Barcelone, Spain";
                    String theStadium = stadiums.get(i);
                    theLocation = theLocation.replace(" ", "").toLowerCase();
                    theStadium = theStadium.replace(" ", "").toLowerCase();
                    if (!theLocation.contains(theStadium))
                        mylocationeditText.setText("" + currentLocation);
                    else
                        mylocationeditText.setText("" + stadiums.get(i));
                }
            }
        }
    }

    private boolean checkIfAlreadyhavePermission() {
        int result = ContextCompat.checkSelfPermission(this, GET_ACCOUNTS);
        if (result == PackageManager.PERMISSION_GRANTED) {
            return true;
        } else {
            return false;
        }
    }

    private void requestForSpecificPermission() {
        ActivityCompat.requestPermissions(this, new String[]{ACCESS_COARSE_LOCATION,
                READ_EXTERNAL_STORAGE,
                INTERNET,
                WRITE_SETTINGS,
                ACCESS_FINE_LOCATION,
                WRITE_EXTERNAL_STORAGE,
                CAMERA
        }, 101);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case 101:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    //granted
                } else {
                    //not granted
                }
                break;
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    public void flashLightOff() {
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                try {
                    String cameraId;
                    camManager = (CameraManager) this.getSystemService(Context.CAMERA_SERVICE);
                    if (camManager != null) {
                        cameraId = camManager.getCameraIdList()[0];
                        camManager.setTorchMode(cameraId, false);
                    }
                } catch (CameraAccessException e) {
                    e.printStackTrace();
                    Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();

                }
            } else {
                if (getPackageManager().hasSystemFeature(
                        PackageManager.FEATURE_CAMERA_FLASH)) {
                    cam = Camera.open();
                    Parameters p = cam.getParameters();
                    p.setFlashMode(Camera.Parameters.FLASH_MODE_OFF);
                    cam.setParameters(p);
                    cam.stopPreview();

//                    cam = Camera.open();
//                    Parameters p = cam.getParameters();
//                    p.setFlashMode(Parameters.FLASH_MODE_TORCH);
//                    cam.setParameters(p);
//                    cam.startPreview();
                }


            }
        } catch (Exception e) {
            Log.e(TAG, "flashLightOn: " + e.getMessage());
        }
    }

    public void flashLightOn() {
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                try {
                    camManager = (CameraManager) this.getSystemService(Context.CAMERA_SERVICE);
                    String cameraId = null; // Usually front camera is at 0 position.
                    if (camManager != null) {

                        cameraId = camManager.getCameraIdList()[0];
                        camManager.setTorchMode(cameraId, true);
                    }
                } catch (CameraAccessException e) {
                    Log.e("Camera", e.toString());

                }
            } else {
                if (getPackageManager().hasSystemFeature(
                        PackageManager.FEATURE_CAMERA_FLASH)) {
                    cam = Camera.open();
                    Parameters p = cam.getParameters();
                    p.setFlashMode(Camera.Parameters.FLASH_MODE_TORCH);
                    cam.setParameters(p);
                    cam.startPreview();
                }
            }

        } catch (Exception e) {
            Log.e(TAG, "flashLightOn: " + e.getMessage());
        }


    }


    private void vibrate(int duration) {
        Vibrator v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        if (v.hasVibrator()) {
            Log.e("Can Vibrate", "YES");
        } else {
            Log.e("Can Vibrate", "NO");
        }
        if (Build.VERSION.SDK_INT >= 26) {
            v.vibrate(VibrationEffect.createOneShot(duration, VibrationEffect.DEFAULT_AMPLITUDE));
        } else {
            v.vibrate(duration);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        stopService(new Intent(this, songService.class));
    }

    public void soundToMAX() {
        AudioManager am =
                (AudioManager) getSystemService(Context.AUDIO_SERVICE);

        am.setStreamVolume(
                AudioManager.STREAM_MUSIC,
                am.getStreamMaxVolume(AudioManager.STREAM_MUSIC),
                0);
        Toast.makeText(getApplicationContext(), "Sound set to maximum", Toast.LENGTH_LONG).show();
    }

    public void brightnessToMAX() {
        android.provider.Settings.System.putInt(getApplicationContext().getContentResolver(),
                android.provider.Settings.System.SCREEN_BRIGHTNESS, 255);
        WindowManager.LayoutParams layoutpars = window.getAttributes();
        layoutpars.screenBrightness = brightness / (float) 255;
        //Apply attribute changes to this window
        window.setAttributes(layoutpars);
        Toast.makeText(getApplicationContext(), "Brightness set to maximum", Toast.LENGTH_LONG).show();
    }

    public static int findInStr(String s1, char c) {
        for (int i = 0; i < s1.length(); i++) {
            if (s1.charAt(i) == c)
                return i;
        }
        return -1;
    }

    public void theSequenceBegins() {
        submit.setVisibility(View.GONE);
        textInputLayout.setVisibility(View.GONE);
        textInputLayout2.setVisibility(View.GONE);
        theBackGround.setVisibility(View.VISIBLE);
    }

    public void theSequenceEnds() {
        submit.setVisibility(View.VISIBLE);
        textInputLayout.setVisibility(View.VISIBLE);
        textInputLayout2.setVisibility(View.VISIBLE);
        theBackGround.setVisibility(View.GONE);

    }

    public static void hideKeyboardFrom(Context context, View view) {
        InputMethodManager imm = (InputMethodManager) context.getSystemService(Activity.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }
}
