package com.example.tabexample;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.StrictMode;
import android.os.Vibrator;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.HashMap;


public class MainActivity extends Activity
{

	ArrayList<String> listItems = new ArrayList<String>();
	ArrayAdapter<String> adapter;
    Button action;
	final Context context = this;
	Button search;
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		final EditText restuarant = (EditText)findViewById(R.id.editText2);
		final EditText location = (EditText)findViewById(R.id.editText);
		ListView listView = (ListView)findViewById(R.id.list);
		search= (Button)findViewById(R.id.search);
		action=(Button) findViewById(R.id.bAction);
		adapter=new ArrayAdapter<String>(this,
				android.R.layout.simple_list_item_1,listItems);
		listView.setAdapter(adapter);
		final Menu menu = new Menu();
		final HashMap<String, String> mymap=new HashMap<String, String>();
		search.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				try {
					InputMethodManager inputManager = (InputMethodManager)
							getSystemService(Context.INPUT_METHOD_SERVICE);

					inputManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(),
							InputMethodManager.HIDE_NOT_ALWAYS);
					StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
					StrictMode.setThreadPolicy(policy);

//					HttpClient client = new DefaultHttpClient();
//					String rest = restuarant.getText().toString().trim();
//					String loc = location.getText().toString().trim();
//					rest = rest.replaceAll(" ", "%20");
//					loc = loc.replaceAll(" ", "%20");
//					String url = "http://tapnpay.elasticbeanstalk.com/PostServlet?restuarant=" + rest + "&location=" + loc;
//					HttpGet request = new HttpGet(url);
//					//..more logic
//					HttpResponse response;
//					response = client.execute(request);
//
//					HttpEntity entity = response.getEntity();

//					String responseString = EntityUtils.toString(entity, "UTF-8");

					HashMap<String, String> map = menu.searchHotels(restuarant.getText().toString().trim(),location.getText().toString().trim());
					mymap.putAll(map);
					addItems(map);

				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});

		action.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent i = new Intent(getApplicationContext(), ReadNFC.class);
				startActivity(i);
			}
		});
		listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

				String restuarant = adapter.getItem(position);

				Intent downloadIntent = new Intent(getApplicationContext(), MenuList.class).putExtra("restuarant", restuarant);

				downloadIntent.putExtra("menuObj",mymap);
				startActivity(downloadIntent);

			}
		});
	}
	public void addItems(HashMap<String,String> maps) {
		listItems.clear();

		for(String key:maps.keySet()){
			listItems.add(key);
		}
		adapter.notifyDataSetChanged();
	}

	private void vibrate() {
		//	//Log.d(TAG, "vibrate");

		Vibrator vibe = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE) ;
		vibe.vibrate(500);
	}
	public static String ByteArrayToHexString(byte[] paramArrayOfByte)
	{
		String[] arrayOfString = { "0", "1", "2", "3", "4", "5", "6", "7", "8", "9", "A", "B", "C", "D", "E", "F" };
		String str1 = "";
		for (int i = 0;; i++)
		{
			if (i >= paramArrayOfByte.length) {
				return str1;
			}
			int j = 0xFF & paramArrayOfByte[i];
			int k = 0xF & j >> 4;
			String str2 = str1 + arrayOfString[k];
			int m = j & 0xF;
			str1 = str2 + arrayOfString[m];
		}
	}
}