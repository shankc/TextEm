package com.kaidoh.mayuukhvarshney.textem;

/**
 * Created by mayuukhvarshney on 25/04/16.
 */

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v7.app.AppCompatActivity;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;
public class ComposeMessageActivity extends AppCompatActivity {

  List<MessageData> ComposedMessages;
    EditText NumberBox,TypedMessage;
    ListView ComposedList;
    ImageButton AddContacts;
    ImageView SendButton;
    ChatAdapter ComposeAdapter;

    String PhoneNumber,theMessage;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.compose_message);
        NumberBox=(EditText)findViewById(R.id.numberbox);
        AddContacts=(ImageButton)findViewById(R.id.pick_contact);
        ComposedList= (ListView)findViewById(R.id.messagesContainer);
        SendButton=(ImageView)findViewById(R.id.chatSend);
        TypedMessage=(EditText)findViewById(R.id.messageEdit);
        SendButton.setImageResource(R.drawable.ic_flight_takeoff_black_24dp);
        AddContacts.setImageResource(R.drawable.ic_group_black_24dp);

        ComposedMessages=new ArrayList<>();
        ComposeAdapter=new ChatAdapter(this,ComposedMessages);
        ComposedList.setAdapter(ComposeAdapter);
        AddContacts.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType(ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE);
                startActivityForResult(intent, 1);
            }
        });

        SendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                theMessage=TypedMessage.getText().toString();
                PhoneNumber=NumberBox.getText().toString();
                Log.d("ComposeMessageActivity", " the sent message is " + theMessage);
                try {
                    SmsManager smsManager = SmsManager.getDefault();
                    smsManager.sendTextMessage(PhoneNumber, null, theMessage, null, null);
                    Toast.makeText(ComposeMessageActivity.this, "Message sent", Toast.LENGTH_SHORT).show();
                    ComposeMessageActivity.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            MessageData sms = new MessageData();
                            sms.senttFromUser(true);
                            //ComposedList.setAdapter(ComposeAdapter);
                            sms.setBody(theMessage);
                            ComposedMessages.add(sms);
                           // Log.d("ComposeMessageActivity", "the message in array is " + ComposedMessages.get(0).getBody());
                            ComposeAdapter.notifyDataSetChanged();
                        }
                    });
                }

                catch (Exception e) {
                    Toast.makeText(ComposeMessageActivity.this, "Message Sending Failed", Toast.LENGTH_SHORT).show();
                    e.printStackTrace();
                }

            }
        });


    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // TODO Auto-generated method stub
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == 1){
            if(resultCode == RESULT_OK){
                Uri contactData = data.getData();
                Cursor cursor =  managedQuery(contactData, null, null, null, null);
                cursor.moveToFirst();

                String number =       cursor.getString(cursor.getColumnIndexOrThrow(ContactsContract.CommonDataKinds.Phone.NUMBER));

                //contactName.setText(name);
                NumberBox.setText(number);
                //contactEmail.setText(email);
            }
        }
    }

}
