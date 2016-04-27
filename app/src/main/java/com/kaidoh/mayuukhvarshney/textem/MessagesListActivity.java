package com.kaidoh.mayuukhvarshney.textem;

import android.app.SearchManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

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
    private BroadcastReceiver mMessageReceiver;
    private String InMessage,InNumber;
    private int InDate;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_messages_list);
        MessageList= new ArrayList<MessageData>();

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        // parseing all messages on device and storing it on MessageList element
        Uri uri= Uri.parse("content://sms/inbox");
        Cursor c= getContentResolver().query(uri, null, null ,null,null);
        startManagingCursor(c);

        // Read Messages and store it in a MessageListMethod

        if(c.moveToFirst()){
            for(int i=0;i<c.getCount();i++){
                MessageData sms= new MessageData();
                boolean found=false;
                if(MessageList!=null)
                {
                    for(int j=0;j<MessageList.size();j++){
                        if(c.getString(c.getColumnIndexOrThrow("address")).equals(MessageList.get(j).getNumber())){

                            MessageList.get(j).setBody(c.getString(c.getColumnIndexOrThrow("body")));
                            MessageList.get(j).setNumber(c.getString(c.getColumnIndexOrThrow("address")));
                            MessageList.get(j).setDate(c.getInt(c.getColumnIndexOrThrow("date")));
                            found=true;
                            c.moveToNext();
                            break;
                        }
                    }
                    if(!found)
                    {
                        sms.setBody(c.getString(c.getColumnIndexOrThrow("body")));
                        sms.setNumber(c.getString(c.getColumnIndexOrThrow("address")));
                        sms.setDate(c.getInt(c.getColumnIndexOrThrow("date")));
//                sms.setID(c.getString(c.getColumnIndexOrThrow("id")));
                        MessageList.add(sms);

                        c.moveToNext();
                    }

                }
                else {
                    sms.setBody(c.getString(c.getColumnIndexOrThrow("body")));
                    sms.setNumber(c.getString(c.getColumnIndexOrThrow("address")));
                    sms.setDate(c.getInt(c.getColumnIndexOrThrow("date")));
//                sms.setID(c.getString(c.getColumnIndexOrThrow("id")));
                    MessageList.add(sms);

                    c.moveToNext();
                }
            }

        }
        else {
            Toast.makeText(MessagesListActivity.this,"No Messages in Inbox",Toast.LENGTH_SHORT).show();
            //Log.d("MessageListActivity", " cursor is null");
        }

//c.close();
mMessageReceiver=new BroadcastReceiver() {
    @Override
    public void onReceive(Context context, Intent intent) {
        Bundle extras= intent.getExtras();
        InNumber=extras.getString("PhoneNumber");
        InMessage=extras.getString("Message");
        String temp=extras.getString("Date");
        if(temp!=null){
            InDate=Integer.parseInt(temp);
        }
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                boolean flag=false;
                MessageData sms = new MessageData();
                for(int i=0;i<MessageList.size();i++){
                    if(InNumber.equals(MessageList.get(i).getNumber())){
                        MessageList.get(i).setBody(InMessage);
                        MessageList.get(i).setDate(InDate);
                        MessageList.get(i).setNumber(InNumber);
                        mAdapter.notifyDataSetChanged();
                        flag=true;
                        break;
                    }

                }
                if(!flag){
                    sms.setNumber(InNumber);
                    sms.setBody(InMessage);
                    sms.setDate(InDate);
                    MessageList.add(0,sms);
                    mAdapter.notifyDataSetChanged();

                }
            }
        });

    }
};
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        if (fab != null)
        fab.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {

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



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.option_menu, menu);
       /* SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        SearchView mySearchView = (SearchView) menu.findItem(R.id.search).getActionView();
        SearchableInfo searchableInfo = searchManager.getSearchableInfo(new ComponentName(MessagesListActivity.this, SearchResultsActivity.class));
        mySearchView.setSearchableInfo(searchableInfo);

       */
        SearchManager searchManager =
                (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        SearchView searchView =
                (SearchView) menu.findItem(R.id.search).getActionView();
        if(searchView!=null){
        searchView.setSearchableInfo(
                searchManager.getSearchableInfo(getComponentName()));
            searchView.setIconifiedByDefault(false);
        }
        else
        {
            Log.d("MessageListActivity","Searchview is null ");
        }
        return true;
    }
    @Override
    public void onStart(){
        super.onStart();
        LocalBroadcastManager.getInstance(this).registerReceiver(mMessageReceiver,
                new IntentFilter("Incoming"));
    }
}
