package com.kaidoh.mayuukhvarshney.textem;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.telephony.SmsManager;
import android.telephony.SmsMessage;
import android.util.Log;
/**
 * Created by mayuukhvarshney on 24/04/16.
 */
public class IncomingMessage extends BroadcastReceiver{
    final SmsManager sms = SmsManager.getDefault();
   private String PhoneNumber,message;
    private String date;
    private Long timestamp;
    private Context mContext;

    @Override
    public void onReceive(Context context, Intent intent) {

        // Retrieves a map of extended data from the intent.
        final Bundle bundle = intent.getExtras();
        this.mContext=context;
        try {

            if (bundle != null) {

                final Object[] pdusObj = (Object[]) bundle.get("pdus");

                for (int i = 0; i < pdusObj.length; i++) {

                    SmsMessage currentMessage = SmsMessage.createFromPdu((byte[]) pdusObj[i]);
                   PhoneNumber = currentMessage.getDisplayOriginatingAddress();
                    message = currentMessage.getDisplayMessageBody();
                   timestamp = System.currentTimeMillis()/1000;
                    date = timestamp.toString();
                    Log.i("SmsReceiver", "senderNum: " + PhoneNumber+ "; message: " + message);


                }
                Intent i = new Intent("Incoming");
                i.putExtra("PhoneNumber", PhoneNumber);
                i.putExtra("Message",message);
                i.putExtra("Date",date);
                LocalBroadcastManager.getInstance(context).sendBroadcast(i);

            }

        } catch (Exception e) {
            e.printStackTrace();

        }
        PendingIntent contentIntent = PendingIntent.getActivity(context, 0,
                new Intent(context, MessagesListActivity.class), 0);

        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(context)
                        .setSmallIcon(android.R.drawable.ic_dialog_email)
                        .setContentTitle(PhoneNumber)
                        .setContentText(message);
        mBuilder.setContentIntent(contentIntent);
        mBuilder.setAutoCancel(true);
        NotificationManager mNotificationManager =
                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        mNotificationManager.notify(1, mBuilder.build());

    }

}
