package ui.screen_tag_write;

import android.app.PendingIntent;
import android.content.Intent;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.nfc.tech.Ndef;
import android.nfc.tech.NdefFormatable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;
import com.example.radek.nfc_test.R;
import java.io.IOException;
import java.io.UnsupportedEncodingException;

public class TagWritingActivity extends AppCompatActivity {

    private NfcAdapter nfcAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tag_writing);
        nfcAdapter = NfcAdapter.getDefaultAdapter(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        final Intent intent = new Intent(this, getClass());
        intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        final PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, 0);
        nfcAdapter.enableForegroundDispatch(this, pendingIntent, null, null);
    }

    @Override
    protected void onPause() {
        super.onPause();
        nfcAdapter.disableForegroundDispatch(this);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        if (NfcAdapter.ACTION_TAG_DISCOVERED.equals(intent.getAction()) || NfcAdapter.ACTION_NDEF_DISCOVERED.equals(intent.getAction())) {
            Tag detectedTag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
            if (isSupportedTagType(detectedTag.getTechList())) {
                try {
                    WriteResponse tagWritingResponse = writeTag(createNdefMessage(true), detectedTag);
                    String message = (tagWritingResponse.isSuccessful() ? "Success: " : "Failed: ") + tagWritingResponse.getMessage();
                    Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
                } catch (UnsupportedEncodingException ex) {
                    Toast.makeText(this, "Defined encoding is not supported", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(this, "This tag type is not supported", Toast.LENGTH_SHORT).show();
            }
        }
    }

    public WriteResponse writeTag(NdefMessage ndefMessage, Tag tag) {
        int messageSize = ndefMessage.toByteArray().length;
        try {
            Ndef ndef = Ndef.get(tag);
            if (ndef != null) {
                ndef.connect();
                if (!ndef.isWritable()) {
                    return new WriteResponse(false, "Tag is read-only");
                }
                if (ndef.getMaxSize() < messageSize) {
                    return new WriteResponse(false, "Tag capacity is " + ndef.getMaxSize() + " bytes, message is " + messageSize + " bytes.");
                }
                ndef.writeNdefMessage(ndefMessage);
                return new WriteResponse(true, "Wrote message to pre-formatted tag.");
            } else { // we need to format the tag to NDEF standard
                NdefFormatable format = NdefFormatable.get(tag);
                if (format != null) {
                    try {
                        format.connect();
                        format.format(ndefMessage);
                        return new WriteResponse(true, "Formatted tag and wrote message");
                    } catch (IOException e) {
                        return new WriteResponse(false, "Failed to format tag.");
                    }
                } else {
                    return new WriteResponse(false, "Tag doesn't support NDEF.");
                }
            }
        } catch (Exception e) {
            return new WriteResponse(false, "Failed to write tag");
        }
    }

    public static boolean isSupportedTagType(String[] techs) {
        boolean ultralight = false;
        boolean nfcA = false;
        boolean ndef = false;
        for (String tech : techs) {
            switch (tech) {
                case "android.nfc.tech.MifareUltralight":
                    ultralight = true;
                    break;
                case "android.nfc.tech.NfcA":
                    nfcA = true;
                    break;
                case "android.nfc.tech.Ndef":
                case "android.nfc.tech.NdefFormatable":
                    ndef = true;
                    break;
            }
        }
        return ultralight && nfcA && ndef;
    }

    private NdefMessage createNdefMessage(boolean addAndroidApplicationRecord) throws UnsupportedEncodingException {
        String text = "alarm";
        String lang = "en";
        byte[] textBytes = text.getBytes();
        byte[] langBytes = lang.getBytes("UTF-8");

        byte[] payload = new byte[1 + langBytes.length + textBytes.length];
        payload[0] = (byte) langBytes.length;

        // copy langbytes and textbytes into payload
        System.arraycopy(langBytes, 0, payload, 1, langBytes.length);
        System.arraycopy(textBytes, 0, payload, 1 + langBytes.length, textBytes.length);
        NdefRecord rtdTextRecord = new NdefRecord(NdefRecord.TNF_WELL_KNOWN, NdefRecord.RTD_TEXT, new byte[0], payload);

        if (addAndroidApplicationRecord) { // add a record to launch an application on tag discovery
            return new NdefMessage(
                    new NdefRecord[] { rtdTextRecord, NdefRecord.createApplicationRecord("com.rmakowiecki.nfcalarmclock") }
            );
        } else {
            return new NdefMessage(new NdefRecord[] { rtdTextRecord });
        }
    }

    private final class WriteResponse {
        private final boolean successful;
        private final String message;

        WriteResponse(boolean successful, String message) {
            this.successful = successful;
            this.message = message;
        }

        boolean isSuccessful() {
            return successful;
        }

        String getMessage() {
            return message;
        }
    }
}
