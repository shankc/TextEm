package com.kaidoh.mayuukhvarshney.textem;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.telephony.SmsManager;
import android.telephony.SmsMessage;
import android.util.Log;
import android.app.PendingIntent;
import android.app.NotificationManager;
/**
 * Created by mayuukhvarshney on 24/04/16.
 */
public class IncomingMessage extends BroadcastReceiver{
    final SmsManager sms = SmsManager.getDefault();
   private String PhoneNumber,message;
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
                    Log.i("SmsReceiver", "senderNum: " + PhoneNumber+ "; message: " + message);


                }
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
