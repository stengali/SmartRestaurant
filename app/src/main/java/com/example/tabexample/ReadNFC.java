package com.example.tabexample;

import android.app.ListActivity;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.nfc.NdefMessage;
import android.nfc.NfcAdapter;
import android.os.Bundle;
import android.os.Parcelable;
import android.os.Vibrator;
import android.widget.ArrayAdapter;

import java.util.ArrayList;


public class ReadNFC extends ListActivity
{

    ArrayList<String> listItems = new ArrayList<String>();
    ArrayAdapter<String> adapter;
    final Context context = this;
    protected NfcAdapter nfcAdapter;
    protected PendingIntent nfcPendingIntent;
    String tagName =null;
    String tagID = null;
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_read_nfc);

        adapter=new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1,listItems);
        setListAdapter(adapter);
        nfcAdapter = NfcAdapter.getDefaultAdapter(this);
        nfcPendingIntent = PendingIntent.getActivity(this, 0, new Intent(this, this.getClass()).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP), 0);
    }
    public void addItems(String list) {
        listItems.clear();

      //  StringTokenizer st = new StringTokenizer(list,"$");
       // while(st.hasMoreElements()) {
         //   StringTokenizer st1 = new StringTokenizer(st.nextElement().toString(), "|");
           // String itemName = st1.nextToken();
           // String qty = st1.nextToken();
           // listItems.add(itemName +":"+qty);

        listItems.add(new String(list));
        //}
        adapter.notifyDataSetChanged();
    }


    @Override
    public void onNewIntent(Intent intent) {
        //Log.d(TAG, "onNewIntent");

        Parcelable[] rawMsgs = intent
                .getParcelableArrayExtra(NfcAdapter.EXTRA_NDEF_MESSAGES);
        NdefMessage msg = (NdefMessage) rawMsgs[0];
        byte[] array = null;
        array = msg.getRecords()[0].getPayload();

        addItems(new String(array));
        byte[] arrayOfByte = intent.getByteArrayExtra("android.nfc.extra.ID");
        String tagIdDetected = ByteArrayToHexString(arrayOfByte);
        tagID = tagIdDetected;

    }

    @Override
    protected void onResume() {
        //Log.d(TAG, "onResume");

        super.onResume();
        enableForegroundMode();
    }

    @Override
    protected void onPause() {
        //Log.d(TAG, "onPause");

        super.onPause();

        disableForegroundMode();
    }

    public void enableForegroundMode() {
        ////Log.d(TAG, "enableForegroundMode");

        IntentFilter tagDetected = new IntentFilter(NfcAdapter.ACTION_TAG_DISCOVERED); // filter for all
        IntentFilter[] writeTagFilters = new IntentFilter[] {tagDetected};
        nfcAdapter.enableForegroundDispatch(this, nfcPendingIntent, writeTagFilters, null);
    }

    public void disableForegroundMode() {
        //		//Log.d(TAG, "disableForegroundMode");

        nfcAdapter.disableForegroundDispatch(this);
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