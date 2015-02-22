package ru.promansew.smssender;

import android.content.*;
import android.os.Bundle;
import android.telephony.SmsMessage;
import android.widget.Toast;

public class SMSReceiver extends BroadcastReceiver {
	public void onReceive(Context context, Intent intent) {
		Bundle bundle = intent.getExtras();
		String str = "";
		if (bundle != null) {
			Object[] pdus = (Object[]) bundle.get("pdus");
			SmsMessage msg = SmsMessage.createFromPdu((byte[]) pdus[0]);
			str += SMSHelper.getInstance().getName(msg.getOriginatingAddress());
			str += ":\n";
			str += msg.getMessageBody().toString();
			Toast.makeText(context, str, Toast.LENGTH_LONG).show();
			SMS sms = new SMS();
			sms.address = msg.getOriginatingAddress();
	        sms.msg = msg.getMessageBody().toString();
	        sms.isRead = true;
	        sms.date = System.currentTimeMillis();
	        sms.type = 1; // TextBasedSmsColumns.MESSAGE_TYPE_INBOX
			SMSHelper.getInstance().onSMSListUpdated(sms);
		}
	}
}
