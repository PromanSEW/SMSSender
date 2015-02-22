package ru.promansew.smssender;

import java.util.*;

import android.app.Activity;
import android.content.Intent;
import android.os.*;
import android.view.*;
import android.widget.*;
import android.widget.AdapterView.OnItemClickListener;

public class ActMain extends Activity {

	private SMSHelper smsh;
	private ListView lv;
	private ArrayList<String> numbers;
	
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		lv = (ListView) findViewById(R.id.main_lv);
		SMSHelper.initInstance(this);
		smsh = SMSHelper.getInstance();
	}
	
	protected void onStart() { super.onStart(); updateListView(); }
	
	protected void onResume() { super.onResume(); smsh.setActMainHandler(this); }
	
	protected void onPause() { smsh.setActMainHandler(null); super.onPause(); }
	
	protected void onDestroy() { smsh.setActMainHandler(null); super.onDestroy(); }
	
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	public boolean onOptionsItemSelected(MenuItem item) {
		int id = item.getItemId();
		if (id == R.id.action_new) {
			startActivityForResult(new Intent(this, ActNew.class), 0);
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
	
	protected void onActivityResult(int resultCode, int requestCode, Intent data) {
		if(resultCode == RESULT_OK) updateListView();
	}
	
	public void updateListView() {
		new AsyncTask<Void, Void, Void>() {
			ArrayList<Map<String, String>> list = new ArrayList<Map<String, String>>();
			protected void onPreExecute() {
				lv.setVisibility(View.GONE);
				findViewById(R.id.main_loading).setVisibility(View.VISIBLE);
			}
			protected Void doInBackground(Void... params) {
				SMSHelper.getInstance().updateSMSList();
				numbers = new ArrayList<String>();
				ArrayList<SMS> smslist = smsh.getSMSList();
				ArrayList<Long> dates = new ArrayList<Long>();
				Map<String, String> m;
				for(SMS sms: smslist) {
					boolean add = true;
					for(int i=0; i < list.size(); i++) {
						if(numbers.get(i).equals(sms.address)) {
							if(sms.date > dates.get(i).longValue()) {
								m = new HashMap<String, String>();
								m.put("name", smsh.getName(sms.address));
								m.put("msg", sms.msg.length() > 40 ? sms.msg.substring(0, 40) + "..." : sms.msg);
								list.set(i, m); dates.set(i, sms.date);
							}
							add = false; break;
						}
					}
					if(add) {
						m = new HashMap<String, String>();
						m.put("name", smsh.getName(sms.address));
						m.put("msg", sms.msg.length() > 40 ? sms.msg.substring(0, 40) + "..." : sms.msg);
						list.add(m); numbers.add(sms.address); dates.add(sms.date);
					}
				}
				return null;
			}
			protected void onPostExecute(Void result) {
				findViewById(R.id.main_loading).setVisibility(View.GONE);
				if(list.isEmpty()) {
					findViewById(R.id.main_nosms).setVisibility(View.VISIBLE);
					lv.setVisibility(View.GONE);
				} else {
					findViewById(R.id.main_nosms).setVisibility(View.GONE);
					lv.setVisibility(View.VISIBLE);
					lv.setAdapter(new SimpleAdapter(ActMain.this, list, android.R.layout.simple_list_item_2, 
						new String[] { "name", "msg" }, new int[] { android.R.id.text1, android.R.id.text2 }));
					lv.setOnItemClickListener(new OnItemClickListener() {
						public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
							Intent intent = new Intent(ActMain.this, ActDialog.class);
							intent.putExtra("name", ((TextView) view.findViewById(android.R.id.text1)).getText().toString());
							intent.putExtra("number", numbers.get(position));
							startActivityForResult(intent, 0);
						}
					});
				}
			}
		}.execute();
	}
}
