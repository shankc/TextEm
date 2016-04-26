package com.kaidoh.mayuukhvarshney.textem;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.kaidoh.mayuukhvarshney.textem.dummy.DummyContent;

import java.util.ArrayList;
import java.util.List;

/**
 * An activity representing a list of Content. This activity
 * has different presentations for handset and tablet-size devices. On
 * handsets, the activity presents a list of items, which when touched,
 * lead to a {@link MessagesDetailActivity} representing
 * item details. On tablets, the activity presents the list of items and
 * item details side-by-side using two vertical panes.
 */
public class MessagesListActivity extends AppCompatActivity {

    /**
     * Whether or not the activity is in two-pane mode, i.e. running on a tablet
     * device.
     */
    private boolean mTwoPane;
    List<MessageData> MessageList;
    InboxAdapter mAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_messages_list);
        MessageList= new ArrayList<MessageData>();

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitle(getTitle());
        // parseing all messages on device and storing it on MessageList element
        Uri uri= Uri.parse("content://sms/inbox");
        Cursor c= getContentResolver().query(uri, null, null ,null,null);
        startManagingCursor(c);

        // Read Messages and store it in a MessageListMethod

        if(c.moveToFirst() && c!=null){
            for(int i=0;i<c.getCount();i++){
                MessageData sms= new MessageData();
                sms.setBody(c.getString(c.getColumnIndexOrThrow("body")));
                sms.setNumber(c.getString(c.getColumnIndexOrThrow("address")));
                sms.setDate(c.getInt(c.getColumnIndexOrThrow("date")));
//                sms.setID(c.getString(c.getColumnIndexOrThrow("id")));
                MessageList.add(sms);

                c.moveToNext();
            }
        }
        else {
            Log.d("MessageListActivity", " cursor is null");
        }
//c.close();

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        if(fab!=null)
        fab.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Intent intent = new Intent("android.intent.action.VIEW");

                /** creates an sms uri */
                //Uri data = Uri.parse("sms:");

                /** Setting sms uri to the intent */
               // intent.setData(data);

                /** Initiates the SMS compose screen, because the activity contain ACTION_VIEW and sms uri */
                //startActivity(intent);
               Intent intent = new Intent(MessagesListActivity.this,ComposeMessageActivity.class);
                startActivity(intent);


            }
        });
        View recyclerView = findViewById(R.id.messages_list);
        assert recyclerView != null;
        setupRecyclerView((RecyclerView) recyclerView);

        if (findViewById(R.id.messages_detail_container) != null) {
            // The detail container view will be present only in the
            // large-screen layouts (res/values-w900dp).
            // If this view is present, then the
            // activity should be in two-pane mode.
            mTwoPane = true;
        }
    }

    private void setupRecyclerView(@NonNull RecyclerView recyclerView) {
       mAdapter= new InboxAdapter(MessageList,mTwoPane);
        recyclerView.setAdapter(mAdapter);
        mAdapter.notifyDataSetChanged();


    }
    public class InboxAdapter extends RecyclerView.Adapter<InboxAdapter.MyViewHolder> {

        private List<MessageData> InboxMessages;
        private Boolean mTwoPane;

        public class MyViewHolder extends RecyclerView.ViewHolder {
            public TextView InNumber,InMessageBody;
            private View mView;

            public MyViewHolder(View view) {
                super(view);
                InNumber=(TextView)view.findViewById(R.id.NumberMessage);
                InMessageBody=(TextView)view.findViewById(R.id.MessageBody);
                mView=view;

            }
        }


        public InboxAdapter(List<MessageData> messages,Boolean TwoPane) {
            this.InboxMessages = messages;
            this.mTwoPane=TwoPane;
        }

        @Override
        public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View itemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.inbox_row, parent, false);

            return new MyViewHolder(itemView);
        }

        @Override
        public void onBindViewHolder(final MyViewHolder holder, int position) {
            final MessageData message= InboxMessages.get(position);
            holder.InNumber.setText(message.getNumber());
            holder.InMessageBody.setText(message.getBody());
            holder.mView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mTwoPane) {
                        Bundle arguments = new Bundle();
                        arguments.putString(MessagesDetailFragment.ARG_ITEM_ID, message.getNumber());
                        MessagesDetailFragment fragment = new MessagesDetailFragment();
                        fragment.setArguments(arguments);
                        getSupportFragmentManager().beginTransaction()
                                .replace(R.id.messages_detail_container, fragment)
                                .commit();
                    } else {
                        Context context = v.getContext();
                        Intent intent = new Intent(context, MessagesDetailActivity.class);
                        intent.putExtra(MessagesDetailFragment.ARG_ITEM_ID, message.getNumber());

                        context.startActivity(intent);
                    }
                }
            });
        }

        @Override
        public int getItemCount() {
            return InboxMessages.size();
        }
    }

    public class SimpleItemRecyclerViewAdapter
            extends RecyclerView.Adapter<SimpleItemRecyclerViewAdapter.ViewHolder> {

        private final List<DummyContent.DummyItem> mValues;

        public SimpleItemRecyclerViewAdapter(List<DummyContent.DummyItem> items) {
            mValues = items;
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.messages_list_content, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(final ViewHolder holder, int position) {
            holder.mItem = mValues.get(position);
            holder.mIdView.setText(mValues.get(position).id);
            holder.mContentView.setText(mValues.get(position).content);

            holder.mView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mTwoPane) {
                        Bundle arguments = new Bundle();
                        arguments.putString(MessagesDetailFragment.ARG_ITEM_ID, holder.mItem.id);
                        MessagesDetailFragment fragment = new MessagesDetailFragment();
                        fragment.setArguments(arguments);
                        getSupportFragmentManager().beginTransaction()
                                .replace(R.id.messages_detail_container, fragment)
                                .commit();
                    } else {
                        Context context = v.getContext();
                        Intent intent = new Intent(context, MessagesDetailActivity.class);
                        intent.putExtra(MessagesDetailFragment.ARG_ITEM_ID, holder.mItem.id);

                        context.startActivity(intent);
                    }
                }
            });
        }

        @Override
        public int getItemCount() {
            return mValues.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            public final View mView;
            public final TextView mIdView;
            public final TextView mContentView;
            public DummyContent.DummyItem mItem;

            public ViewHolder(View view) {
                super(view);
                mView = view;
                mIdView = (TextView) view.findViewById(R.id.id);
                mContentView = (TextView) view.findViewById(R.id.content);
            }

            @Override
            public String toString() {
                return super.toString() + " '" + mContentView.getText() + "'";
            }
        }
    }
}
