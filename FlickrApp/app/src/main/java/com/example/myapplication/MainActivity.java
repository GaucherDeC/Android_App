package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class MainActivity extends AppCompatActivity {
    String url = "https://www.flickr.com/services/feeds/photos_public.gne?tags=trees&format=json";
    TextView tv;
    ImageView iv;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        iv = (ImageView) findViewById(R.id.imageView);

        Button btn_image = (Button) findViewById(R.id.button_image);
        btn_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AsyncFlickrJSONData asyncFlickrJSONData = new AsyncFlickrJSONData();
                asyncFlickrJSONData.execute(url);

            }
        });

        Button btn_ListActivity = (Button) findViewById(R.id.button_ListActivity);
        btn_ListActivity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i =new Intent(MainActivity.this,ListActivity.class);
                startActivity(i);

            }
        });
    }

    public class AsyncFlickrJSONData extends AsyncTask<String,Void, JSONObject> {

        private String readStream(InputStream is) throws IOException {
            StringBuilder sb = new StringBuilder();
            BufferedReader r = new BufferedReader(new InputStreamReader(is),1000);
            for (String line = r.readLine(); line != null; line =r.readLine()){
                sb.append(line);
            }
            is.close();
            return sb.toString();
        }

        @Override
        protected JSONObject doInBackground(String... strings) {
            URL url = null;
            try {
                url = new URL(strings[0]);
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();

                try {
                    InputStream in = new BufferedInputStream(urlConnection.getInputStream());
                    String s = readStream(in);
                    //Log.i("JFL", s);
                    try {
                        JSONObject jsonObject = new JSONObject((s.subSequence(("jsonFlickrFeed(").length(),s.length()-1)).toString());
                        return jsonObject;
                    } catch (JSONException err){
                        Log.d("Error",err.toString());
                    }
                }
                finally {
                    urlConnection.disconnect();
                }
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

            return null;
        }
        protected void onPostExecute (JSONObject result){
            //Log.i("TAG",result.toString());
            try {
                String url_image = result.getJSONArray("items").getJSONObject(1).getJSONObject("media").getString("m");
                //Log.i("IMG", result.getJSONArray("items").getJSONObject(1).getString("link"));
                AsyncBitmapDownloader asyncBitmapDownloader = new AsyncBitmapDownloader();
                asyncBitmapDownloader.execute(url_image);
                Log.i("TEST",url_image);
            } catch (JSONException err){
                Log.d("Error",err.toString());
            }


        }
    }

    public class AsyncBitmapDownloader extends AsyncTask<String,Void, Bitmap> {

        @Override
        protected Bitmap doInBackground(String... strings) {
            URL url = null;
            try {
                url = new URL(strings[0]);
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();

                try {
                    InputStream in = new BufferedInputStream(urlConnection.getInputStream());
                    Bitmap bm = BitmapFactory.decodeStream(in);
                    Log.i("Test do in",""+(bm==null));
                    return bm;
                }
                finally {
                    urlConnection.disconnect();
                }
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

            return null;
        }

        protected void onPostExecute (Bitmap result){
            runOnUiThread(new Runnable() {
                              @Override
                              public void run() {
                                  Log.i("Test on post",""+(result==null));
                                  iv.setImageBitmap(result);
                              }
                                });
        }
    }
}