package com.code.hypermario.peopleinfo.recyclerview;


import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.code.hypermario.peopleinfo.R;
import com.squareup.picasso.Picasso;

import java.util.List;

public class MyRecyclerAdapter extends RecyclerView.Adapter<FeedListRowHolder> {


  private List<FeedItem> feedItemList;

  private Context mContext;

  public MyRecyclerAdapter(Context context, List<FeedItem> feedItemList) {
    this.feedItemList = feedItemList;
    this.mContext = context;
  }

  @Override
  public FeedListRowHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
    View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.list_row, null);
    FeedListRowHolder mh = new FeedListRowHolder(v);

    return mh;
  }

  @Override
  public void onBindViewHolder(FeedListRowHolder feedListRowHolder, int i) {
    FeedItem feedItem = feedItemList.get(i);

    Picasso.with(mContext).load(feedItem.getThumbnail())
            .error(R.drawable.placeholder)
            .placeholder(R.drawable.placeholder)
            .into(feedListRowHolder.thumbnail);

    feedListRowHolder.title.setText(feedItem.getTitle());
    feedListRowHolder.title.setTextColor(Color.parseColor("#ffffff"));
    feedListRowHolder.title.setTextSize(15);
  }

  @Override
  public int getItemCount() {
    return (null != feedItemList ? feedItemList.size() : 0);
  }
}