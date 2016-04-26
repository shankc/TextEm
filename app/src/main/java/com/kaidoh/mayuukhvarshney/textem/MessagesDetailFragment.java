package com.kaidoh.mayuukhvarshney.textem;

import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
/**
 * A fragment representing a single Messages detail screen.
 * This fragment is either contained in a {@link MessagesListActivity}
 * in two-pane mode (on tablets) or a {@link MessagesDetailActivity}
 * on handsets.
 */
public class MessagesDetailFragment extends Fragment {
    /**
     * The fragment argument representing the item ID that this fragment
     * represents.
     */
    public static final String ARG_ITEM_ID = "item_id";


    /**
     * The dummy content this fragment is presenting.
     */
    //private DummyContent.DummyItem mItem;
    private String mItem;
    private ImageView Send;
    private List<MessageData> ChatList;
    private ListView IncomingMessage;
    private ChatAdapter mChatAdapter;
    private EditText InputText;
   private String InputMessage;
    private TextView toolbartext;
    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public MessagesDetailFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments().containsKey(ARG_ITEM_ID)) {
            // Load the dummy content specified by the fragment
            // arguments. In a real-world scenario, use a Loader
            // to load content from a content provider.
            //mItem = DummyContent.ITEM_MAP.get(getArguments().getString(ARG_ITEM_ID));
            Bundle bundle = MessagesDetailFragment.this.getArguments();
            mItem=bundle.getString(ARG_ITEM_ID);
            Activity activity = this.getActivity();
           // CollapsingToolbarLayout appBarLayout = (CollapsingToolbarLayout) activity.findViewById(R.id.toolbar_layout);
            //if (appBarLayout != null) {
              //  appBarLayout.setTitle(mItem);
            //}

            // Recieved Messages
            ChatList = new ArrayList<MessageData>();
            Uri uri= Uri.parse("content://sms/inbox");
            Cursor c= getActivity().getContentResolver().query(uri, null, null, null, null);
            getActivity().startManagingCursor(c);





            // Read Messages and store it in a MessageListMethod

            if(c.moveToFirst()){
                for(int i=0;i<c.getCount();i++){
                    MessageData sms= new MessageData();
                    if(c.getString(c.getColumnIndexOrThrow("address")).equals(mItem)){
                        sms.setBody(c.getString(c.getColumnIndexOrThrow("body")));
                        sms.setNumber(c.getString(c.getColumnIndexOrThrow("address")));
                        sms.senttFromUser(false);

                        ChatList.add(sms);
                    }
                    c.moveToNext();
                }
            }
            Uri uri_sent=Uri.parse("content://sms/sent");
            Cursor csent=getActivity().getContentResolver().query(uri_sent,null,null,null,null);
            getActivity().startManagingCursor(csent);
            if(csent.moveToFirst()){
                for(int i=0;i<csent.getCount();i++){
                    MessageData sms= new MessageData();
                    Log.d("MessageDetailFragment"," the sent message is "+csent.getString(csent.getColumnIndexOrThrow("body"))+" "+csent.getString(csent.getColumnIndexOrThrow("address")));
                    if(csent.getString(csent.getColumnIndexOrThrow("address")).equals(mItem)){
                        sms.setBody(csent.getString(csent.getColumnIndexOrThrow("body")));
                        sms.setNumber(csent.getString(csent.getColumnIndexOrThrow("address")));
                        sms.setDate(csent.getInt(csent.getColumnIndexOrThrow("date")));
                        sms.senttFromUser(true);
                        ChatList.add(sms);
                    }
                    csent.moveToNext();
                }
            }


        }

        ((MessagesDetailActivity)getActivity()).toolbar.setTitle(mItem);
        Collections.sort(ChatList, new Comparator<MessageData>() {
            @Override
            public int compare(MessageData lhs, MessageData rhs) {
                if(lhs.getDate()<rhs.getDate()){
                    return lhs.getDate();
                }
                return rhs.getDate();
            }
        });

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.chat_activity, container, false);

                ((MessagesDetailActivity)getActivity()).getSupportActionBar().setTitle(mItem);

        Send=(ImageView)rootView.findViewById(R.id.chatSend);
        InputText=(EditText)rootView.findViewById(R.id.messageEdit);
        //InputMessage=InputText.getText().toString();
        Send.setImageResource(R.drawable.send);
        IncomingMessage=(ListView)rootView.findViewById(R.id.messagesContainer);
        mChatAdapter= new ChatAdapter(getActivity(),ChatList);
        IncomingMessage.setAdapter(mChatAdapter);
        mChatAdapter.notifyDataSetChanged();
        ((MessagesDetailActivity)getActivity()).toolbar.setTitle(mItem);
        Send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                InputMessage=InputText.getText().toString();
                InputMethodManager inputManager = (InputMethodManager)
                        getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                InputText.setText("");

                inputManager.hideSoftInputFromWindow(getActivity().getCurrentFocus().getWindowToken(),
                        InputMethodManager.HIDE_NOT_ALWAYS);
                try {
                    SmsManager smsManager = SmsManager.getDefault();
                    smsManager.sendTextMessage(mItem, null, InputMessage, null, null);
                    Toast.makeText(getActivity(), "Message sent.", Toast.LENGTH_SHORT).show();
                }

                catch (Exception e) {
                    Toast.makeText(getActivity(), "Message Sending Failed", Toast.LENGTH_SHORT).show();
                    e.printStackTrace();
                }
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        MessageData sms = new MessageData();
                        sms.setBody(InputMessage);
                        sms.senttFromUser(true);
                        ChatList.add(sms);
                        mChatAdapter.notifyDataSetChanged();
                    }
                });
            }
        });
        // Show the dummy content as text in a TextView.
       // if (mItem != null) {
         //   ((TextView) rootView.findViewById(R.id.messages_detail)).setText(mItem);
        //}


        return rootView;
    }
}
