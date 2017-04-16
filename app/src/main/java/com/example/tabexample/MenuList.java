package com.example.tabexample;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.nfc.tech.Ndef;
import android.nfc.tech.NdefFormatable;
import android.os.Bundle;
import android.os.StrictMode;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;
import java.util.TreeSet;

public class MenuList extends Activity {

    LinearLayout linearMain;
    CheckBox checkBox;
    ArrayList<String> menuList;
    Button placeOrder;
    Set<String> orderList = new TreeSet<String>();
    boolean mWriteMode = false;
    private NfcAdapter mNfcAdapter;
    private PendingIntent mNfcPendingIntent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu_list);
        linearMain = (LinearLayout) findViewById(R.id.linearMain);
        TextView view = new TextView(this);
        view.setText("Menu List");
        placeOrder = (Button) findViewById(R.id.button);
        linearMain.addView(view);
        Intent intent = getIntent();
        String restuarant = intent.getStringExtra("restuarant");
        HashMap<String,String> map = (HashMap<String, String>) intent.getSerializableExtra("menuObj");
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        placeOrder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mNfcAdapter = NfcAdapter.getDefaultAdapter(MenuList.this);
                mNfcPendingIntent = PendingIntent.getActivity(MenuList.this, 0,
                        new Intent(MenuList.this, MainActivity.class).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP), 0);

                enableTagWriteMode();

                new AlertDialog.Builder(MenuList.this).setTitle("Touch tag to write")
                        .setOnCancelListener(new DialogInterface.OnCancelListener() {
                            @Override
                            public void onCancel(DialogInterface dialog) {
                                disableTagWriteMode();
                            }

                        }).create().show();
            }
        });
        try {
            Menu menu1 = new Menu();
            menuList= menu1.hotelNameToMenuItems(restuarant, map);

        } catch (Exception e) {
            e.printStackTrace();
        }
        int count =1;
        for(String menu: menuList)
        {
            checkBox = new CheckBox(this);
            checkBox.setId(new Integer(count));
            checkBox.setText(menu);
            checkBox.setOnClickListener(getOnClickDoSomething(checkBox));
            linearMain.addView(checkBox);
            count++;
        }

    }
        View.OnClickListener getOnClickDoSomething(final Button button) {
            return new View.OnClickListener() {
                public void onClick(View v) {
                    orderList.add(button.getText().toString()+"$");
                    Toast.makeText(getApplicationContext(), "hi",
                            Toast.LENGTH_LONG).show();

                }
            };


    }


    /*
    * Writes an NdefMessage to a NFC tag
    */
    public boolean writeTag(NdefMessage message, Tag tag) {
        int size = message.toByteArray().length;
        try {
            Ndef ndef = Ndef.get(tag);
            if (ndef != null) {
                ndef.connect();
                if (!ndef.isWritable()) {
                    Toast.makeText(getApplicationContext(),
                            "Error: tag not writable",
                            Toast.LENGTH_SHORT).show();
                    return false;
                }
                if (ndef.getMaxSize() < size) {
                    Toast.makeText(getApplicationContext(),
                            "Error: tag too small",
                            Toast.LENGTH_SHORT).show();
                    return false;
                }
                ndef.writeNdefMessage(message);
                return true;
            } else {
                NdefFormatable format = NdefFormatable.get(tag);
                if (format != null) {
                    try {
                        format.connect();
                        format.format(message);
                        return true;
                    } catch (IOException e) {
                        return false;
                    }
                } else {
                    return false;
                }
            }
        } catch (Exception e) {
            return false;
        }
    }
    private void disableTagWriteMode() {
        mWriteMode = false;
        mNfcAdapter.disableForegroundDispatch(this);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        // Tag writing mode
        if (mWriteMode && NfcAdapter.ACTION_TAG_DISCOVERED.equals(intent.getAction())) {
            Tag detectedTag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);

            String s ="";
            for(String res: orderList)
            {
                s+=res;
            }

            String lang = "en";
            byte[] textBytes = s.getBytes();
            byte[] langBytes = lang.getBytes();
            int langLength = langBytes.length;
            int textLength = textBytes.length;

            byte[] payload = new byte[1 + langLength + textLength];
            payload[0] = (byte) langLength;


            Toast.makeText(getApplicationContext(), "This is the msg"+s,
                    Toast.LENGTH_LONG).show();
            NdefRecord record = NdefRecord.createMime(orderList.toString(),s.getBytes(Charset.forName("US-ASCII")));
            //NdefRecord record = new NdefRecord(NdefRecord.TNF_WELL_KNOWN, NdefRecord.RTD_TEXT, null,"Samosa".getBytes());
            NdefMessage message = new NdefMessage(new NdefRecord[] { record });

            if (writeTag(message, detectedTag)) {
                Toast.makeText(this, "Success: Wrote placeid to nfc tag", Toast.LENGTH_LONG)
                        .show();
            }
        }
    }
    private void enableTagWriteMode() {
        mWriteMode = true;
        IntentFilter tagDetected = new IntentFilter(NfcAdapter.ACTION_TAG_DISCOVERED);
        IntentFilter[] mWriteTagFilters = new IntentFilter[] { tagDetected };
        mNfcAdapter.enableForegroundDispatch(this, mNfcPendingIntent, mWriteTagFilters, null);
    }

}
