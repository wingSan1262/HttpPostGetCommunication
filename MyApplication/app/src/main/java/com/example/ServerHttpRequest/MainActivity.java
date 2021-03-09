package com.example.ServerHttpRequest;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.widget.ImageView;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.lang.ref.WeakReference;

public class MainActivity extends AppCompatActivity {
    private Handler mHandler = null;
    public static final int JSON_RETURN_FROM_SERVER = 99;
    public static final String URL_SQL_QUERIES = "https://badcodegr.000webhostapp.com/SqlService/sqlQueries.php";
    public static final String WEB_VIDEO_URL = "https://badcodegr.000webhostapp.com/SqlService/downloadToDevice.php";
    String JSONString = null;
    TextView mTv = null;
    ImageView imageView = null;
    WeakReference <Context> weakReference = null;

    String[] jsonDateCol;
    public static final int DOWNLOAD_VIDEO_FROM_SERVER = 98;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        weakReference = new WeakReference<Context>(this);

        mTv = findViewById(R.id.text_view);
        imageView = findViewById(R.id.image_view);

        mHandler = new Handler(this.getMainLooper()) {
            @Override
            public void handleMessage(Message msg) {
                if (msg != null) {
                    switch (msg.what) {
                        case JSON_RETURN_FROM_SERVER :
                            JSONString = (String) msg.obj;
//                            mTv.setText(JSONString);
                            try {
                                loadIntoListView(JSONString, mTv);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            break;
                        case DOWNLOAD_VIDEO_FROM_SERVER :
                            String savedPath = (String) msg.obj;

                            File mFile = new File(savedPath);
                            if (mFile.exists()){
                                mTv.setText(savedPath);
                                int heightDeviceScale = ImageScallingUtils.getDisplayHeight(weakReference.get());
                                int widthDeviceScale = ImageScallingUtils.getDisplayWidth(weakReference.get());
                                int newWidth, newHeight;
                                float factor;
                                int margin = 200;

                                Bitmap scaledBitmap = null;
                                Bitmap myBitmap = BitmapFactory.decodeFile(mFile.getAbsolutePath());

                                // rezize image to fit and proporsional to device screen
                                if (myBitmap.getHeight() > myBitmap.getWidth()){
                                    factor = ( (float) heightDeviceScale / (float) ImageScallingUtils.dpToPx(myBitmap.getHeight(), weakReference.get()));
                                } else {
                                    factor = (float) widthDeviceScale / (float) ImageScallingUtils.dpToPx(myBitmap.getWidth(), weakReference.get());
                                }
                                newWidth = (int) (myBitmap.getWidth() * factor);
                                newHeight = (int) (myBitmap.getHeight() * factor);
                                scaledBitmap = Bitmap.createScaledBitmap(myBitmap, newWidth, newHeight, true);

                                imageView.setImageBitmap(scaledBitmap);

                            } else {
                                mTv.setText("video acquisition failed");
                            }
                    }
                }
            }
        };

//        GetObjectsFromServer.getJSON(WEB_VIDEO_URL, mHandler);
        GetObjectsFromServer.getVideo(WEB_VIDEO_URL, mHandler, this);
    }

    private void loadIntoListView(String json, TextView textView) throws JSONException {
        //creating a json array from the json string
        JSONArray jsonArray = new JSONArray(json);
        StringBuilder sb = new StringBuilder();

        //creating a string array for listview
        String[] jsonDateCol = new String[jsonArray.length()];


        //looping through all the elements in json array
        for (int i = 0; i < jsonArray.length(); i++) {

            //getting json object from the json array
            JSONObject obj = jsonArray.getJSONObject(i);

            //getting the name from the json object and putting it inside string array
            jsonDateCol[i] = obj.getString("Date");
            sb.append(obj.getString("Date"));
        }
//        File file = this.getExternalFilesDir(null);
//        assert file != null;
        textView.setText(sb.toString());
    }
}
