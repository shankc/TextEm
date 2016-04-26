package com.kaidoh.mayuukhvarshney.textem;

/**
 * Created by mayuukhvarshney on 26/04/16.
 */

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;
public class SearchResultsActivity extends AppCompatActivity {

private List<MessageData> Searchdata;
    private InboxAdapter mAdapter;
    private boolean mTwoPane;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_messages_list);
Searchdata= new ArrayList<>();
       // handleIntent(getIntent());
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
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
handleIntent(getIntent());
    }
    private void setupRecyclerView(@NonNull RecyclerView recyclerView) {
        mAdapter= new InboxAdapter(Searchdata,mTwoPane);
        recyclerView.setAdapter(mAdapter);
        mAdapter.notifyDataSetChanged();


    }
    @Override
    protected void onNewIntent(Intent intent) {
        handleIntent(intent);
    }
    public class InboxAdapter extends RecyclerView.Adapter<InboxAdapter.MyViewHolder> {

        private List<MessageData> InboxMessages;
        private Boolean mTwoPane;

        public class MyViewHolder extends RecyclerView.ViewHolder {
            public TextView InNumber, InMessageBody;
            private View mView;

            public MyViewHolder(View view) {
                super(view);
                InNumber = (TextView) view.findViewById(R.id.NumberMessage);
                InMessageBody = (TextView) view.findViewById(R.id.MessageBody);
                mView = view;

            }
        }


        public InboxAdapter(List<MessageData> messages, Boolean TwoPane) {
            this.InboxMessages = messages;
            this.mTwoPane = TwoPane;
        }

        @Override
        public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View itemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.inbox_row, parent, false);

            return new MyViewHolder(itemView);
        }

        @Override
        public void onBindViewHolder(final MyViewHolder holder, int position) {
            final MessageData message = InboxMessages.get(position);
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
        private void handleIntent(Intent intent) {
            if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
                String query = intent.getStringExtra(SearchManager.QUERY);

                Uri uri= Uri.parse("content://sms/inbox");
                Cursor c= getContentResolver().query(uri, null, null, null, null);
                startManagingCursor(c);
                if(c.moveToFirst()){
                    for(int i=0;i<c.getCount();i++){
                        MessageData sms= new MessageData();
                        if(c.getString(c.getColumnIndexOrThrow("address")).equals(query)){
                            sms.setBody(c.getString(c.getColumnIndexOrThrow("body")));
                            sms.setNumber(c.getString(c.getColumnIndexOrThrow("address")));
                            sms.setDate(c.getInt(c.getColumnIndexOrThrow("date")));
                            Searchdata.add(sms);
                            break;

                        }
                        c.moveToNext();
                    }
                    if(Searchdata==null){
                        Toast.makeText(SearchResultsActivity.this,"No Results Found :(",Toast.LENGTH_SHORT).show();
                    }
                }
            }

        }
    }
