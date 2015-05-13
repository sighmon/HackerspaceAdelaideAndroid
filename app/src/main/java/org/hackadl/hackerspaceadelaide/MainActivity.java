package org.hackadl.hackerspaceadelaide;

import android.content.Intent;
import android.nfc.NdefMessage;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.nfc.tech.IsoDep;
import android.nfc.tech.Ndef;
import android.nfc.tech.NfcA;
import android.os.Parcelable;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import java.io.IOException;


public class MainActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        handleIntent(getIntent());
    }

    @Override
    public void onNewIntent(Intent intent) {
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        handleIntent(intent);
    }

    private void handleIntent(Intent intent) {

        String action = intent.getAction();
        if (NfcAdapter.ACTION_TAG_DISCOVERED.equals(action)) {

            // In case we would still use the Tech Discovered Intent
            Tag tag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
            String[] techList = tag.getTechList();

            for (String tech : techList) {
                Log.i("NFC", "TAG_DISCOVERED: " + tech);
            }

            StringBuilder stringBuilder = new StringBuilder();
            for (byte idByte : tag.getId()) {
                stringBuilder.append(String.format("%02X ", idByte));
            }
            Log.i("NFC", "TAG_ID: " + stringBuilder.toString());
            TextView textView = (TextView) this.findViewById(R.id.home_welcome);
            textView.setText(stringBuilder.toString());

            stringBuilder = new StringBuilder();
            IsoDep iso = IsoDep.get(tag);
            byte[] READ_BINARY = {
                    (byte) 0x00, // CLA Class
                    (byte) 0xB0, // INS Instruction
                    (byte) 0x80, // P1  (indicate use of SFI)
                    (byte) 0x01, // P2  (SFI = 0x01)
                    (byte) 0x04  // LE  maximal number of bytes expected in result
            };
            if (iso != null) {
                try {
                    iso.connect();
                    byte[] isoBytes = iso.transceive(READ_BINARY);
                    for (byte isoByte : isoBytes) {
                        stringBuilder.append(String.format("%02X ", isoByte));
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
                Log.i("NFC", "isoBytes: " + stringBuilder.toString());

            }

            Ndef ndefTag = Ndef.get(tag);
            if (ndefTag != null) {
                Log.i("NFC", "Ndef: " + ndefTag.getType());
            }
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
