package com.example.myapplication;

import android.content.res.Resources;
import android.os.Bundle;
import com.google.android.material.snackbar.Snackbar;
import androidx.appcompat.app.AppCompatActivity;

import android.util.Base64;
import android.util.JsonReader;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import com.example.myapplication.databinding.ActivityAuthentificationBinding;

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

import javax.net.ssl.HttpsURLConnection;


public class Authentication extends AppCompatActivity {

    private boolean runres;

    private AppBarConfiguration appBarConfiguration;
    private ActivityAuthentificationBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_authentification);

        Resources res = getResources();
        Log.i("test", res.getString(R.string.test));
    }
    public void login(View view)
    {
        Thread thread = new Thread(new MyTask());
        thread.start();
        /*
        TextView name = findViewById(R.id.editTextTextUsername);
        String username = name.getText().toString();
        TextView pass = findViewById(R.id.editTextTextPassword);
        String password = pass.getText().toString();
        */
    }

    private String readStream(InputStream is) throws IOException {
        StringBuilder sb = new StringBuilder();
        BufferedReader r = new BufferedReader(new InputStreamReader(is),1000);
        for (String line = r.readLine(); line != null; line =r.readLine()){
            sb.append(line);
        }
        is.close();
        return sb.toString();
    }


    public class MyTask implements Runnable
    {
        public void run()
        {
            URL url = null;
            try {
                url = new URL("https://httpbin.org/basic-auth/bob/sympa");
                HttpsURLConnection urlConnection = (HttpsURLConnection) url.openConnection();
                try {
                    TextView name = findViewById(R.id.editTextTextUsername);
                    String username = name.getText().toString();
                    TextView pass = findViewById(R.id.editTextTextPassword);
                    String password = pass.getText().toString();
                    TextView result = findViewById(R.id.textResult);
                    Button button = findViewById(R.id.button);
                    //String basicAuth = "Basic " + Base64.encodeToString("bob:sympa".getBytes(),
                    //        Base64.NO_WRAP);
                    String toTest = username +":" + password;
                    String basicAuth = "Basic " + Base64.encodeToString( toTest.getBytes(),Base64.NO_WRAP);
                    urlConnection.setRequestProperty ("Authorization", basicAuth);
                    InputStream in = new BufferedInputStream(urlConnection.getInputStream());
                    String s = readStream(in);
                    Log.i("JFL", s);
                    JSONObject JFL = new JSONObject(s) ;
                    boolean res = JFL.getBoolean("authenticated");
                    Authentication.this.runres = res;
                    runOnUiThread(new Runnable()
                    {
                        @Override
                        public void run()
                        {
                            if (Authentication.this.runres)
                            {
                                result.setText(R.string.textResultTrue);
                            }
                            name.setVisibility(View.GONE);
                            pass.setVisibility(View.GONE);
                            button.setVisibility(View.GONE);
                            result.setVisibility(View.VISIBLE);
                        }
                    });

                } catch (JSONException e) {
                    e.printStackTrace();
                } finally {
                    urlConnection.disconnect();
                }
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }




}

