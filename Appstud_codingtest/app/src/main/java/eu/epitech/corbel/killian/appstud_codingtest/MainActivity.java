package eu.epitech.corbel.killian.appstud_codingtest;

import android.content.Context;
import android.location.Location;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.support.v4.widget.SwipeRefreshLayout;
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

  /*
   *  Main activity : List of bar photos
   */
public class MainActivity extends AppCompatActivity {

      private RecyclerView mRecyclerView;
      private SwipeRefreshLayout mSwipeContainer;
      private Context mContext;
      private double mLongitude;
      private double mLatitude;
      final private String APIKey = "AIzaSyCcamaf8p3N2xGXEtuJSe_AAEFFkETnuac";

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Setting up the RecyclerView for the pictures
        mRecyclerView = (RecyclerView) this.findViewById(R.id.recycler_view_pics);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerView.setAdapter(new PicAdapter(this, null));

        mContext = this;

        // Trying to get current location. If there is no permission, we give a default one
        // TODO : Use SharedPreferences to get location from user input if no permission
        LocationManager lm = (LocationManager)getSystemService(Context.LOCATION_SERVICE);
        try {
            Location location = lm.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            mLongitude = location.getLongitude();
            mLatitude = location.getLatitude();
        } catch (SecurityException e) {
            mLongitude = 1.444195;
            mLatitude = 43.599156;
        }

        // AsyncTask used for internet requests
        new DownloadPicTask().execute();


        // Setting up the Swipe Container to refresh places based on new location
        mSwipeContainer = (SwipeRefreshLayout) this.findViewById(R.id.swipeContainer);
        mSwipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
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
        });

    }

    private class DownloadPicTask extends AsyncTask<Void, Void, PicAdapter> {

        @Override
        protected PicAdapter doInBackground(Void... params) {

            try {
                // Trying to connect to Google Places API
                URL url = new URL("https://maps.googleapis.com/maps/api/place/nearbysearch/json?key=" + APIKey + "&location="+ String.valueOf(mLatitude) + "," + String.valueOf(mLongitude) + "&radius=2000&type=bar");
                URLConnection connection = url.openConnection();

                // We read the result and store in into a String variable
                String line;
                StringBuilder builder = new StringBuilder();
                BufferedReader reader = new BufferedReader(new InputStreamReader(
                        connection.getInputStream()));
                while ((line = reader.readLine()) != null) {
                    builder.append(line);
                }

                // Getting JSON from String
                String jSONString = builder.toString();

                JSONObject jSONObject = new JSONObject(jSONString);
                JSONArray resultArray = jSONObject.getJSONArray("results");

                // Once we got our array, we create a new adapter with it. We want to do it in background
                PicAdapter adapter = new PicAdapter(mContext, resultArray);

                return adapter;

            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(PicAdapter adapter) {

            // Setting the new adapter
            mRecyclerView.setAdapter(adapter);
            super.onPostExecute(adapter);
            mSwipeContainer.setRefreshing(false);
        }
    }
}
