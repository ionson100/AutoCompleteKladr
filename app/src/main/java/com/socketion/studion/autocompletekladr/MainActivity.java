package com.socketion.studion.autocompletekladr;

import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.ProgressBar;

import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import javax.net.ssl.HttpsURLConnection;

public class MainActivity extends AppCompatActivity {








    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final AddressCompleteTextView textView= (AddressCompleteTextView) findViewById(R.id.aauto_complete);
        textView.setAdapter(new AddressAdapter(this));
        textView.setLoadingIndicator((ProgressBar) findViewById(R.id.progress_bar));
        textView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                AddressData book = (AddressData) adapterView.getItemAtPosition(position);
                textView.setText(book.value);
            }
        });
    }
}
class SenderAutoComplete{

    String error=null;

    private List<AddressData> stringList;
    private String query;


    public void send( List<AddressData> stringList, String query){


        this.stringList = stringList;
        this.query = query;


        try {
            new SenderWorker().execute().get();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private class SenderWorker extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... params) {



            HttpsURLConnection conn = null;

            try {
                URL url = new URL("https://suggestions.dadata.ru/suggestions/api/4_1/rs/suggest/address");
                conn = (HttpsURLConnection) url.openConnection();

                conn.setReadTimeout(15000 /*milliseconds*/);
                conn.setConnectTimeout(20000 /* milliseconds */);
                conn.setRequestMethod("POST");
                conn.setDoInput(true);
                conn.setDoOutput(true);
                conn.setDoOutput(true);
                conn.setRequestProperty("Content-Type", "application/json");
                conn.setRequestProperty("Accept", "application/json");
                conn.setRequestProperty("Authorization", "Token daaa944d66e9f24f1f3e4d0ea926dfdc2da01e97");
                OutputStreamWriter wr = new OutputStreamWriter(conn.getOutputStream());
                wr.write("{ \"query\": \""+query+"\", \"count\": 10 }");
                wr.flush();
                wr.close();
                conn.connect();
                final int status = conn.getResponseCode();
                int ch;
                StringBuilder sb = new StringBuilder();
                try {
                    BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream(), "UTF-8"));
                    while ((ch = reader.read()) != -1) {
                        sb.append((char) ch);
                    }
                } catch (IOException ignored) {

                }
                if (status == 200) {

                    stringList.clear();
                    Gson gson=new Gson();

                   Iosia iosia= gson.fromJson(sb.toString(),Iosia.class);
                    for (AddressData suggestion : iosia.suggestions) {
                        stringList.add(suggestion);
                    }
                }

            } catch (Exception e) {
               error=e.getMessage();
            }finally {
                if (conn != null) {
                    conn.disconnect();
                }
            }


            return null;
        }
    }
    class Iosia{
        List<AddressData> suggestions=new ArrayList<>();
    }

}
