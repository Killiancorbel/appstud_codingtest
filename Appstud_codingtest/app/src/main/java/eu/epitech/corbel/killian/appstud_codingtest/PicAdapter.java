package eu.epitech.corbel.killian.appstud_codingtest;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONObject;


/**
 * Created by thekten on 29/03/17.
 */

    /*
     * Our own Adapter used to hydrate the RecyclerView in MainActivity
     */

public class PicAdapter extends RecyclerView.Adapter<PicAdapter.PicAdapterViewHolder> {

    private Context mContext;
    private JSONArray mResultArray;
    final private String APIKey = "AIzaSyCcamaf8p3N2xGXEtuJSe_AAEFFkETnuac";

    // Storing variable from MainActivity
    public PicAdapter(Context context, JSONArray resultArray) {
        mContext = context;
        mResultArray = resultArray;
    }

    // On create, we inflate the layout and return our own ViewHolder
    @Override
    public PicAdapterViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(mContext);
        View view = inflater.inflate(R.layout.pic_list_item, parent, false);
        return new PicAdapterViewHolder(view);
    }

    @Override
    public void onBindViewHolder(PicAdapterViewHolder holder, int position) {

        // We extract the label and the photo reference from JSON, build our URL, and use Picasso to display it in to our ImageView
        // I used Picasso for performance and simplicity (caching Bitmap mostly)
        try {
            JSONObject element = mResultArray.getJSONObject(position);
            if (element.has("photos")) {
                String name = element.getString("name");
                JSONArray photoArray = element.getJSONArray("photos");
                JSONObject photo = photoArray.getJSONObject(0);
                String reference = photo.getString("photo_reference");
                String imgUrl = "https://maps.googleapis.com/maps/api/place/photo?maxheight=400&photoreference=" + reference + "&key=" + APIKey;
                Picasso.with(mContext).load(imgUrl).into(holder.picImageView);
                holder.labelTextView.setText(name);
            }


        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    public int getItemCount() {
        if (mResultArray != null) {
            return mResultArray.length();
        } else {
            return 0;
        }
    }

    public class PicAdapterViewHolder extends RecyclerView.ViewHolder {

        ImageView picImageView;
        TextView labelTextView;

        public PicAdapterViewHolder(View itemView) {
            super(itemView);
            picImageView = (ImageView) itemView.findViewById(R.id.iv_main_activity_pic);
            labelTextView = (TextView) itemView.findViewById(R.id.tv_main_activity_label);
        }
    }
}
