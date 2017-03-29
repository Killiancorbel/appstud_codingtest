package eu.epitech.corbel.killian.appstud_codingtest;

import android.content.Context;
import android.location.Location;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

public class MainActivity extends AppCompatActivity {

    private RecyclerView mRecyclerView;
    private Context mContext;
    private double mLongitude;
    private double mLatitude;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mRecyclerView = (RecyclerView) this.findViewById(R.id.recycler_view_pics);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerView.setAdapter(new PicAdapter(this, null));

        mContext = this;

        LocationManager lm = (LocationManager)getSystemService(Context.LOCATION_SERVICE);
        try {
            Location location = lm.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            mLongitude = location.getLongitude();
            mLatitude = location.getLatitude();
        } catch (SecurityException e) {
            mLongitude = 1.444195;
            mLatitude = 43.599156;
        }


        new DownloadPicTask().execute();

    }

    private class DownloadPicTask extends AsyncTask<Void, Void, PicAdapter> {

        @Override
        protected PicAdapter doInBackground(Void... params) {

            try {
                URL url = new URL("https://maps.googleapis.com/maps/api/place/nearbysearch/json?key=AIzaSyCcamaf8p3N2xGXEtuJSe_AAEFFkETnuac&location="+ String.valueOf(mLatitude) + "," + String.valueOf(mLongitude) + "&radius=2000&type=bar");
                URLConnection connection = url.openConnection();


                String line;
                StringBuilder builder = new StringBuilder();
                BufferedReader reader = new BufferedReader(new InputStreamReader(
                        connection.getInputStream()));
                while ((line = reader.readLine()) != null) {
                    builder.append(line);
                }

                String jSONString = builder.toString();

                Log.d("JSON String", jSONString);

                JSONObject jSONObject = new JSONObject(jSONString);
                JSONArray resultArray = jSONObject.getJSONArray("results");

                PicAdapter adapter = new PicAdapter(mContext, resultArray);

                return adapter;

            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(PicAdapter adapter) {

            mRecyclerView.setAdapter(adapter);
            super.onPostExecute(adapter);
        }
    }
}
