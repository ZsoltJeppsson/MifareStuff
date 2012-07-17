package com.zsoleszpapper.mifarestuff;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.Intent;
import android.nfc.NfcAdapter;
import android.nfc.tech.MifareClassic;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

public class MainActivity extends Activity implements MifareCreditEvents {
    private static NfcAdapter mAdapter;
    private static PendingIntent mPendingIntent;
    private static String[][] mTechLists;

    private static TextView mTextView;

    private static Button mFormatButton;
    private static Button mReadButton;
    private static Button mDecrementButton;



    private static MifareCreditHandler mMifareCreditHandler;
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        mMifareCreditHandler = new MifareCreditHandler(this);
        
        mTextView = (TextView) findViewById(R.id.textView1);
        mTextView.setText("");
        
        mFormatButton = (Button) findViewById(R.id.button1);
        mFormatButton.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                mMifareCreditHandler.setCommand(MifareCreditHandler.COMMAND_FORMAT);
                mTextView.setText("Ready to format, touch tag.");
            }
        });

        mReadButton = (Button) findViewById(R.id.button2);
        mReadButton.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                mMifareCreditHandler.setCommand(MifareCreditHandler.COMMAND_READ);
                mTextView.setText("Ready to read, touch tag.");
            }
        });

        mDecrementButton = (Button) findViewById(R.id.button3);
        mDecrementButton.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                mMifareCreditHandler.setCommand(MifareCreditHandler.COMMAND_DECREMENT);
                mTextView.setText("Ready to decrement, touch tag.");
            }
        });

        mAdapter = NfcAdapter.getDefaultAdapter(this);
        mPendingIntent = PendingIntent.getActivity(this, 0, new Intent(this, getClass()).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP), 0);
        mTechLists = new String[][] { new String[] { MifareClassic.class.getName() } };

        Intent intent = getIntent();
        resolveIntent(intent);
    }

    @Override
    public void onResume() {
        super.onResume();
        mAdapter.enableForegroundDispatch(this, mPendingIntent, null, mTechLists);
    }

    @Override
    public void onPause() {
        super.onPause();
        mAdapter.disableForegroundDispatch(this);
    }

    @Override
    public void onNewIntent(Intent intent) {
        Log.i("Foreground dispatch", "Discovered tag with intent: " + intent);
        resolveIntent(intent);
    }

    private void resolveIntent(Intent intent) {
    	mMifareCreditHandler.IntentResolver(intent);
    }
    
	public void ReadValueBlock(int block, int value) {
		mTextView.setText("Read OK. " + value);
		
	}

	public void DecrementBlock(int block) {
		mTextView.setText("Decrement OK. ");
	}

	public void FormatBlock(int block) {
		mTextView.setText("Format OK. ");
	}
}