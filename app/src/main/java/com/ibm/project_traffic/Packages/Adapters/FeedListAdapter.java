package com.ibm.project_traffic.Packages.Adapters;

import android.content.Context;
import android.graphics.Typeface;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.github.curioustechizen.ago.RelativeTimeTextView;
import com.ibm.project_traffic.Packages.Utils.PostDetails;
import com.ibm.project_traffic.Packages.Utils.UserFeedback;
import com.ibm.project_traffic.R;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

public class FeedListAdapter extends RecyclerView.Adapter<FeedListAdapter.FeedViewHolder> {

    //List of Post Details
    List<PostDetails> dataset;
    Context mContext;

    //Constructors
    public FeedListAdapter(Context context, List<PostDetails> postData){
        this.dataset = postData;
        this.mContext = context;
    }


    public FeedListAdapter(){

    }

    protected static class FeedViewHolder extends RecyclerView.ViewHolder{
        RelativeTimeTextView timeStamp;
        TextView username;
        TextView locationDetails;
        TextView postDetails;

        public FeedViewHolder(View view){
            super(view);

            //Binding Views to fields in FeedViewHolder
            timeStamp = (RelativeTimeTextView)view.findViewById(R.id.post_timestamp);
            username = (TextView)view.findViewById(R.id.username);
            locationDetails = (TextView)view.findViewById(R.id.location_details);
            postDetails = (TextView)view.findViewById(R.id.post_details);

            //Set Typeface
            Typeface postDetailsFont = Typeface.createFromAsset(view.getContext().getAssets(), "fonts/notosans-regular.ttf");
            Typeface usernameFont = Typeface.createFromAsset(view.getContext().getAssets(), "fonts/notosans-bold.ttf");
            Typeface locationDetailsFont = Typeface.createFromAsset(view.getContext().getAssets(), "fonts/notosans-regular.ttf");
            postDetails.setTypeface(postDetailsFont);
            username.setTypeface(usernameFont);
            locationDetails.setTypeface(locationDetailsFont);
        }
    }


    @Override
    public FeedViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View rootView = LayoutInflater.from(parent.getContext()).inflate(R.layout.feed_list_adapter, parent, false);
        return new FeedViewHolder(rootView);
    }

    @Override
    public void onBindViewHolder(FeedViewHolder holder, int position) {
        holder.postDetails.setText(dataset.get(position).getComment());
        holder.locationDetails.setText(dataset.get(position).getReportingAddress());
        holder.timeStamp.setReferenceTime(Timestamp.valueOf(dataset.get(position).getTimeStamp()).getTime());
        holder.username.setText(dataset.get(position).getUsername());
    }

    @Override
    public int getItemCount() {
        return (null != dataset ? dataset.size() : 0);
    }

    public static void sort(List<PostDetails> data){
        Collections.sort(data, new Comparator<PostDetails>() {
            @Override
            public int compare(PostDetails lhs, PostDetails rhs) {
                return lhs.getTimeStamp().compareTo(rhs.getTimeStamp());
            }
        });
    }
}
