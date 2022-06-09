package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Vector;

public class ListActivity extends AppCompatActivity {
    String url = "https://www.flickr.com/services/feeds/photos_public.gne?tags=trees&format=json";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);
        MyAdaptater myAdaptater = new MyAdaptater();
        ListView lv = (ListView) findViewById(R.id.list);
        lv.setAdapter(myAdaptater);

        AsyncFlickrJSONDataForList asyncFlickrJSONDataForList = new AsyncFlickrJSONDataForList(myAdaptater);
        asyncFlickrJSONDataForList.execute(url);
    }

    public class MyAdaptater extends BaseAdapter{
        Vector<String> stringVector = new Vector<String>();

        void dd(String url){
            stringVector.add(url);
            return;
        }
        @Override
        public int getCount() {
            return stringVector.size();
        }

        @Override
        public Object getItem(int i) {
            return null;
        }

        @Override
        public long getItemId(int i) {
            return 0;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            //Log.i("JFL", "TODO");
            LayoutInflater inflater = LayoutInflater.from(ListActivity.this);
            /*
            View theInflatedView = inflater.inflate(R.layout.textviewlayout, null);
            TextView tv =(TextView) theInflatedView.findViewById(R.id.textView);
            tv.setText(stringVector.get(i));
            */
            View theInflatedView = inflater.inflate(R.layout.bitmaplayout, null);


            RequestQueue queue = MySingleton.getInstance(viewGroup.getContext()).
                    getRequestQueue();


            ImageRequest imageRequest=new ImageRequest (stringVector.get(i), new Response.Listener<Bitmap>() {
                @Override
                public void onResponse(Bitmap response) {
                    ImageView imageView = (ImageView) theInflatedView.findViewById(R.id.imageView2);
                    imageView.setImageBitmap(response);

                }
            },0,0, ImageView.ScaleType.CENTER_CROP,Bitmap.Config.ARGB_8888, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.i("erro",error.toString());
                    error.printStackTrace();
                }});

            queue.add(imageRequest);

            return theInflatedView;
        }
    }

    public class AsyncFlickrJSONDataForList extends AsyncTask<String, Void, JSONObject> {
        MyAdaptater adaptater;

        private String readStream(InputStream is) throws IOException {
            StringBuilder sb = new StringBuilder();
            BufferedReader r = new BufferedReader(new InputStreamReader(is),1000);
            for (String line = r.readLine(); line != null; line =r.readLine()){
                sb.append(line);
            }
            is.close();
            return sb.toString();
        }

        public AsyncFlickrJSONDataForList(MyAdaptater adapter){
            this.adaptater = adapter;
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
                JSONArray jsonArray = result.getJSONArray("items");
                String url;
                for (int i = 0;i<jsonArray.length();++i){
                    url = jsonArray.getJSONObject(i).getJSONObject("media").getString("m");
                    adaptater.dd(url);
                    adaptater.notifyDataSetChanged();
                    Log.i("AsyncFlickrJSONDataForL","Adding to adaptater URL" + url);
                };

            } catch (JSONException err){
                Log.d("Error",err.toString());
            }


        }
    }


}