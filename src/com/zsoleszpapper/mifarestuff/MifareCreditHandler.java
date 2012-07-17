package com.zsoleszpapper.mifarestuff;

import java.io.IOException;

import android.content.Intent;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.nfc.tech.MifareClassic;
import android.util.Log;

public class MifareCreditHandler {
	private MifareCreditEvents callback;
	
	private static final String TAG = "MifareCreditHandler";
	
	public static int COMMAND_FORMAT = 1;
	public static int COMMAND_READ = 2;
	public static int COMMAND_DECREMENT = 3;
	private static int COMMAND_NOTHING = 255;
	private static int command = COMMAND_NOTHING;
	
	public MifareCreditHandler(MifareCreditEvents cb) {
		callback = cb;
	}
	
    private void forge_value_block(byte[] data, byte value, byte address) {
        data[0] = data[1] = data[2] = data[3] = (byte)0;
        data[0] = value;
        for (int i=0; i<4; i++) {
            data[i+4] = (byte)~(data[i]);
            data[i+8] = data[i];
        }
        data[12] = address;
        data[13] = (byte)~data[12];
        data[14] = data[12];
        data[15] = (byte)~data[12];
    }
	
    public void setCommand(int newcommand) {
    	command = newcommand;
    }

    private void mifare_format(MifareClassic mfc, int block) throws IOException {
        byte[] data = new byte[16];
        forge_value_block(data, (byte)64, (byte)0);
        mfc.writeBlock(block, data);
        callback.FormatBlock(block);
    }

    private void mifare_read(MifareClassic mfc, int block) throws IOException {
        byte[] data = new byte[16];
        data = mfc.readBlock(block);
        callback.ReadValueBlock(block, data[0]);
    }

    private void mifare_decrement(MifareClassic mfc, int block) throws IOException {
        mfc.decrement(block, 1);
        mfc.transfer(block);
        callback.DecrementBlock(block);
    }
    

    public void IntentResolver(Intent intent) {
        Log.i(TAG, "resolveIntent");
        String action = intent.getAction();
        if (NfcAdapter.ACTION_TECH_DISCOVERED.equals(action)) {
            Tag tagFromIntent = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
            MifareClassic mfc = MifareClassic.get(tagFromIntent);

            try {
                mfc.connect();
                int sector = 1;
                int block = mfc.sectorToBlock(sector);

                boolean auth = mfc.authenticateSectorWithKeyA(sector, MifareClassic.KEY_DEFAULT);
                if (!auth) { auth=mfc.authenticateSectorWithKeyA(sector, MifareClassic.KEY_MIFARE_APPLICATION_DIRECTORY); }
                if (!auth) { auth=mfc.authenticateSectorWithKeyA(sector, MifareClassic.KEY_NFC_FORUM); }
                if (auth) {
                    if (command == COMMAND_FORMAT) {
                        mifare_format(mfc, block);
                    } else if (command == COMMAND_READ) {
                        mifare_read(mfc, block);
                    } else if (command == COMMAND_DECREMENT) {
                        mifare_decrement(mfc, block);
                    }
                    command = COMMAND_NOTHING;
                } else {
                    // Auth error callback
                }
            } catch (IOException e) {
            	String errorMessage = e.getLocalizedMessage();
            	Log.e(TAG, errorMessage!=null ? errorMessage : "Unknown exception");
            }
        }
    }

	
}
