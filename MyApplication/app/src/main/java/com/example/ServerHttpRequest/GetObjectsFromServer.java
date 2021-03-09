package com.example.ServerHttpRequest;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import java.io.BufferedReader;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Objects;

public class GetObjectsFromServer {

    //this method is actually fetching the json string
    public static void getJSON(final String urlWebService, final Handler mHandler) {
        /*
         * As fetching the json string is a network operation
         * And we cannot perform a network operation in main thread
         * so we need an AsyncTask
         * The constrains defined here are
         * Void -> We are not passing anything
         * Void -> Nothing at progress update as well
         * String -> After completion it should return a string and it will be the json string
         * */


        final String[] returnJson = {null};

        class GetJSON extends AsyncTask<Void, Void, String> {

            //this method will be called before execution
            //you can display a progress bar or something
            //so that user can understand that he should wait
            //as network operation may take some time
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
            }

            //this method will be called after execution
            //so here we are displaying a toast with the json string
            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                returnJson[0] = s;
                Message message = mHandler.obtainMessage(MainActivity.JSON_RETURN_FROM_SERVER, s);
                mHandler.sendMessage(message);
//                Toast.makeText(getApplicationContext(), s, Toast.LENGTH_SHORT).show();
            }

            //in this method we are fetching the json string
            @Override
            protected String doInBackground(Void... voids) {



                try {
                    //creating a URL
                    URL url = new URL(urlWebService);

                    //Opening the URL using HttpURLConnection
                    HttpURLConnection con = (HttpURLConnection) url.openConnection();

                    //StringBuilder object to read the string from the service
                    StringBuilder sb = new StringBuilder();

                    //We will use a buffered reader to read the string from service
                    BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(con.getInputStream()));

                    //A simple string to read values from each line
                    String json;

                    //reading until we don't find null
                    while ((json = bufferedReader.readLine()) != null) {

                        //appending it to string builder
                        sb.append(json + "\n");
                    }

                    //finally returning the read string
                    return sb.toString().trim();
                } catch (Exception e) {
                    return null;
                }

            }
        }

        //creating asynctask object and executing it
        GetJSON getJSON = new GetJSON();
        getJSON.execute();
    }

    public static void getVideo (final String urlWebService, final Handler mHandler, Context context){
        final String mVideoFile = Objects.requireNonNull(context.getExternalFilesDir(null)).toString() + "/tempImage.jpg";
        final int[] response = new int[1];
        class GetVideoTask extends AsyncTask<Void, Void, Void> {

            //in this method we are fetching the json string
            @Override
            protected Void doInBackground(Void... voids) {
                try {

                    //creating a URL
                    URL url = new URL(urlWebService);


                    //Opening the URL using HttpURLConnection
                    HttpURLConnection connection = (HttpURLConnection) url.openConnection();

//                    InputStream inputStream = new BufferedInputStream(connection.getInputStream(), 8192);\
                    InputStream inputStream = connection.getInputStream();

                    OutputStream outputStream = new FileOutputStream(mVideoFile);

                    byte[] data = new byte[1024];
                    int count = inputStream.read(data);
                    while (count != -1){
                        outputStream.write(data, 0, count);
                        count = inputStream.read(data);
                    }
                    outputStream.flush();
                    outputStream.close();
                    inputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                    Log.e("error", Objects.requireNonNull(e.getMessage()));
                } finally {
                    Message message = mHandler.obtainMessage(MainActivity.DOWNLOAD_VIDEO_FROM_SERVER, mVideoFile);
                    mHandler.sendMessage(message);
                }
                return null;
            }

        }
        GetVideoTask getVideoTask = new GetVideoTask();
        getVideoTask.execute();
    }
}

