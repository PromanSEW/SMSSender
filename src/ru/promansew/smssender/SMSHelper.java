package ru.promansew.smssender;

import java.util.ArrayList;

import android.app.PendingIntent;
import android.content.*;
import android.database.Cursor;
import android.net.Uri;
import android.provider.ContactsContract.CommonDataKinds.Phone;
import android.provider.ContactsContract.*;
import android.telephony.SmsManager;
import android.widget.*;
import android.widget.SimpleCursorAdapter.CursorToStringConverter;

public class SMSHelper {
	
	private static final String[] PROJECTION = new String[] { Data._ID, Contacts.DISPLAY_NAME_PRIMARY, Phone.NUMBER };
	private static final String SELECTION = Phone.NUMBER + " LIKE ? AND " + Phone.TYPE + "='" + Phone.TYPE_MOBILE + 
			"' AND " + Data.MIMETYPE + "='" + Phone.CONTENT_ITEM_TYPE + "'";
	
	private SimpleCursorAdapter adapter;
	private ArrayList<SMS> smslist;
	private Context context;
	private ActMain act;
	private ActDialog dlg;

	private static SMSHelper instance;

	public static SMSHelper getInstance() { return instance; }

	public static void initInstance(ActMain context) {
		if (instance == null) instance = new SMSHelper(context);
	}
	
	public SimpleCursorAdapter getContactsAdapter() { return adapter; }
	
	public ArrayList<SMS> getSMSList() { return smslist; }
	
	public void setActMainHandler(ActMain act) { this.act = act; }
	
	public void setActDialogHandler(ActDialog dlg) { this.dlg = dlg; }

	private SMSHelper(final ActMain context) {
		this.context = context.getApplicationContext();
		adapter = new SimpleCursorAdapter(context, android.R.layout.simple_list_item_2, null, 
				new String[] { Contacts.DISPLAY_NAME_PRIMARY, Phone.NUMBER }, 
				new int[] { android.R.id.text1, android.R.id.text2 }, 0);
		adapter.setFilterQueryProvider(new FilterQueryProvider() {
		    public Cursor runQuery(CharSequence constraint) {
		        return context.getContentResolver().query(
		            Data.CONTENT_URI, PROJECTION, SELECTION,
		            new String[] { constraint + "%" },
		            Contacts.DISPLAY_NAME_PRIMARY + " ASC"
		        );
		    }
		});
		adapter.setCursorToStringConverter(new CursorToStringConverter() {
			public CharSequence convertToString(Cursor cursor) {
				return cursor.getString(cursor.getColumnIndex(Phone.NUMBER));
			}
		});
	}
	
	public String getName(String number) {
		String selection = Phone.TYPE + "='" + Phone.TYPE_MOBILE + "' AND " + Data.MIMETYPE + "='" + Phone.CONTENT_ITEM_TYPE + "'";
		Cursor c = context.getContentResolver().query(Data.CONTENT_URI, PROJECTION, selection, null, null);
		if(c.moveToFirst()) {
			int index = c.getColumnIndex(Phone.NUMBER);
			for(int i=0; i < c.getCount(); i++) {
				String num = c.getString(index);
				if(num.replaceAll("-", "").replaceAll(" ", "").equals(number)) {
					String name = c.getString(c.getColumnIndex(Contacts.DISPLAY_NAME_PRIMARY));
					c.close();
					return name;
				}
				c.moveToNext();
			}
		}
		c.close();
		return number;
	}
	
	public static ArrayList<SMS> getSMSForName(Context act, String number) {
		ArrayList<SMS> list = new ArrayList<SMS>();
		Uri message = Uri.parse("content://sms/");
	    ContentResolver cr = act.getContentResolver();
	    Cursor c = cr.query(message, null, "address = '" + number + "'", null, "date ASC");
	    if (c.moveToFirst()) {
	        for (int i=0; i < c.getCount(); i++) {
	            SMS sms = new SMS();
	            sms.address = c.getString(c.getColumnIndex("address"));
	            sms.msg = c.getString(c.getColumnIndex("body"));
	            sms.isRead = c.getInt(c.getColumnIndex("read")) == 1;
	            sms.date = c.getLong(c.getColumnIndex("date"));
	            sms.type = c.getInt(c.getColumnIndex("type"));
	            list.add(sms);
	            c.moveToNext();
	        }
	    }
	    c.close();
		return list;
	}
	
	public static SMS sendSMS(Context context, String number, String message) {
		SmsManager.getDefault().sendTextMessage(number, null, message, 
				PendingIntent.getActivity(context, 0, new Intent(context, ActMain.class), 0), null);
		SMS sms = new SMS();
		sms.address = number;
        sms.msg = message;
        sms.isRead = true;
        sms.date = System.currentTimeMillis();
        sms.type = 2; // TextBasedSmsColumns.MESSAGE_TYPE_SENT
		return sms;
	}
	
	public void updateSMSList() {
		smslist = new ArrayList<SMS>();
	    Uri message = Uri.parse("content://sms/");
	    Cursor c = context.getContentResolver().query(message, null, null, null, null);
	    if (c.moveToFirst()) {
	    	int address = c.getColumnIndex("address");
	    	int body = c.getColumnIndex("body");
	    	int read = c.getColumnIndex("read");
	    	int date = c.getColumnIndex("date");
	    	int type = c.getColumnIndex("type");
	        for (int i=0; i < c.getCount(); i++) {
	            SMS sms = new SMS();
	            sms.address = c.getString(address);
	            sms.msg = c.getString(body);
	            sms.isRead = c.getInt(read) == 1;
	            sms.date = c.getLong(date);
	            sms.type = c.getInt(type);
	            smslist.add(sms);
	            c.moveToNext();
	        }
	    }
	    c.close();
	}
	
	public void onSMSListUpdated(SMS sms) {
		if(act != null) act.updateListView();
		if(dlg != null && dlg.number.equals(sms.address)) dlg.addSMS(sms);
	}
}
