package ru.promansew.smssender;

import java.util.*;

import android.content.Context;
import android.provider.Telephony.TextBasedSmsColumns;
import android.view.*;
import android.widget.*;

public class DialogAdapter extends BaseAdapter {
	
	private Context context;
	private ArrayList<SMS> smslist = new ArrayList<SMS>();

	public DialogAdapter(Context context) { this.context = context; }

	public View getView(int position, View convertView, ViewGroup parent) {
		SMS sms = smslist.get(position);
		int layout_id = 0;
		switch(sms.type) {
		case 1: layout_id = R.layout.dialog_item_you; break; // TextBasedSmsColumns.MESSAGE_TYPE_INBOX;
		default: layout_id = R.layout.dialog_item_me;
		}
		convertView = LayoutInflater.from(context).inflate(layout_id, null);
		((TextView) convertView.findViewById(R.id.item_text)).setText(sms.msg);
		long gone = System.currentTimeMillis() - sms.date;
		GregorianCalendar date = new GregorianCalendar();
		date.setTimeInMillis(sms.date);
		((TextView) convertView.findViewById(R.id.item_time)).setText(
				context.getString(gone < 86400000 ? R.string.time_today : R.string.time_earlier, date, date));
		return convertView;
	}
	
	public void add(SMS sms) {
		smslist.add(sms);
		notifyDataSetChanged();
	}
	
	public int getCount() { return smslist.size(); }

	public SMS getItem(int position) { return smslist.get(position); }

	public long getItemId(int position) { return 0; }
	
	public void setSMSList(ArrayList<SMS> list) {
		smslist = list;
		notifyDataSetChanged();
	}

}
