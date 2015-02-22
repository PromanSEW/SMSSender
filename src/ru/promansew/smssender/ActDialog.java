package ru.promansew.smssender;

import android.app.Activity;
import android.os.Bundle;
import android.text.*;
import android.view.View;
import android.widget.*;

public class ActDialog extends Activity {
	
	private final SMSHelper smsh = SMSHelper.getInstance();
	private boolean doUpdate = false;
	private String name;
	public String number;
	private TextView ttext;
	private ListView lv;
	private DialogAdapter adapter;
	
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.dialog);
		name = getIntent().getStringExtra("name");
		number = getIntent().getStringExtra("number");
		setTitle(name);
		lv = (ListView) findViewById(R.id.dialog_lv);
		ttext = (TextView) findViewById(R.id.dialog_smstext);
		ttext.addTextChangedListener(new TextWatcher() {
	        public void afterTextChanged(Editable s) {
	            findViewById(R.id.dialog_send).setEnabled(s.length() > 0);
	        }
	        public void beforeTextChanged(CharSequence s, int start, int count, int after){}
	        public void onTextChanged(CharSequence s, int start, int before, int count){}
		});
	}
	
	protected void onStart() { super.onStart(); updateListView(); }
	
	protected void onResume() { super.onResume(); smsh.setActDialogHandler(this); }
	
	protected void onPause() { smsh.setActDialogHandler(null); super.onPause(); }
	
	protected void onDestroy() { smsh.setActDialogHandler(null); super.onDestroy(); }
	
	public void onBackPressed() { if(doUpdate) setResult(RESULT_OK); super.onBackPressed(); }
	
	private void updateListView() {
		adapter = new DialogAdapter(this);
		adapter.setSMSList(SMSHelper.getSMSForName(this, number));
		lv.setAdapter(adapter);
		lv.setSelection(adapter.getCount() - 1);
	}
	
	public void addSMS(SMS sms) { adapter.add(sms); }
	
	public void send(View v) {
		adapter.add(SMSHelper.sendSMS(getApplicationContext(), number, ttext.getText().toString()));
		ttext.setText("");
	}
}
