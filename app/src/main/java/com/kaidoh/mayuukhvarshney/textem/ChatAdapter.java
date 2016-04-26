package com.kaidoh.mayuukhvarshney.textem;

import android.content.Context;
import android.widget.LinearLayout.LayoutParams;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import java.util.List;


public class ChatAdapter extends BaseAdapter{
    private Context mContext;
    private List<MessageData> mMessages;



    public ChatAdapter(Context context, List<MessageData> messages) {
        super();
        this.mContext = context;
        this.mMessages = messages;
    }
    @Override
    public int getCount() {
        return mMessages.size();
    }
    @Override
    public MessageData getItem(int position) {
        if (mMessages!= null) {
            return mMessages.get(position);
        } else {
            return null;
        }
    }
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        MessageData message = getItem(position);

        ViewHolder holder;
        if(convertView == null)
        {
            holder = new ViewHolder();
            convertView = LayoutInflater.from(mContext).inflate(R.layout.bubble_chat, parent, false);
            holder.message = (TextView) convertView.findViewById(R.id.message_text);
            convertView.setTag(holder);
        }
        else
            holder = (ViewHolder) convertView.getTag();

        holder.message.setText(message.getBody());

        LayoutParams lp = (LayoutParams) holder.message.getLayoutParams();
        //check if it is a status message then remove background, and change text color.
        {
            //Check whether message is mine to show green background and align to right
            if(message.getFrom())
            {
                holder.message.setBackgroundResource(R.drawable.grey_bubble);
                lp.gravity = Gravity.START;
            }
            //If not mine then it is from sender to show orange background and align to left
            else
            {
                holder.message.setBackgroundResource(R.drawable.purple_bubble);
                lp.gravity = Gravity.END;
            }
            holder.message.setLayoutParams(lp);
            holder.message.getResources().getColor(R.color.textColor);
        }
        return convertView;
    }
    private static class ViewHolder
    {
        TextView message;
    }

    @Override
    public long getItemId(int position) {
        //Unimplemented, because we aren't using Sqlite.
        return position;
    }

}
