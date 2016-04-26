package com.kaidoh.mayuukhvarshney.textem;

/**
 * Created by mayuukhvarshney on 26/04/16.
 */

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

public class SearchAdapter extends BaseAdapter {

    private List<MessageData> SearchResults;
    private Context mContext;

    public SearchAdapter(Context context,List<MessageData> results){
        this.mContext=context;
        this.SearchResults=results;
    }
    @Override
    public int getCount() {
        return SearchResults.size();
    }
    @Override
    public long getItemId(int position) {
        //Unimplemented, because we aren't using Sqlite.
        return position;
    }
    @Override
    public MessageData getItem(int position) {
        if (SearchResults!= null) {
            return SearchResults.get(position);
        } else {
            return null;
        }
    }
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        MessageData message = getItem(position);

        ViewHolder holder=new ViewHolder();
        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.inbox_row, parent, false);
            holder.SearchNumber=(TextView)convertView.findViewById(R.id.NumberMessage);
            holder.SearchBody=(TextView)convertView.findViewById(R.id.MessageBody);
            convertView.setTag(holder);
        }
        else
        {
            holder=(ViewHolder)convertView.getTag();
        }
        return convertView;
    }

    private class ViewHolder{
            TextView SearchNumber,SearchBody;

    }

        }

