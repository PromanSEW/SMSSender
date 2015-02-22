package ru.promansew.smssender;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.*;

public class ActNew extends Activity {

	AutoCompleteTextView tnumber;
	
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.newsms);
		tnumber = ((AutoCompleteTextView) findViewById(R.id.newsms_number));
		tnumber.setAdapter(SMSHelper.getInstance().getContactsAdapter());
	}
	
	public void send(View v) {
		String number = tnumber.getText().toString();
		String message = ((EditText) findViewById(R.id.newsms_text)).getText().toString();
		if(number.equals("") || message.equals("")) Toast.makeText(this, R.string.newsms_toast_error, Toast.LENGTH_SHORT).show();
		else {
			SMSHelper.sendSMS(getApplicationContext(), number, message);
			setResult(RESULT_OK);
			finish();
		}
	}
}
