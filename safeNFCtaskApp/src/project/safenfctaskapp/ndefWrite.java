/* project.safenfctaskapp 어플리케이션에 사용될 엑티비티를 모아둔 페키지입니다.
*  ndefWrite 작성된 ndef 메세지를 nfc 장치에 입력하는 엑티비티
*/
package project.safenfctaskapp;

import project.safenfctaskapp.mcoupon.Certificate;
import project.safenfctaskapp.nfc.NfcReadWrite;
import android.app.Activity;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.IntentFilter.MalformedMimeTypeException;
import android.nfc.NfcAdapter;
import android.nfc.tech.MifareClassic;
import android.os.Bundle;
import android.util.Log;
import android.view.Window;
import android.widget.TextView;

public class ndefWrite extends Activity {	
	private NfcReadWrite nfcWrite;	
	private NfcAdapter mAdapter;	
	private TextView textView;	
	private Intent content;
	private PendingIntent mPendingIntent;
	private IntentFilter[] mFilters;
	private String[][] mTechLists;
	
	private Certificate cert = new Certificate();
	
	@Override	
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);	
		
		requestWindowFeature(Window.FEATURE_NO_TITLE);	
		
		setContentView(R.layout.writingtag);	
		
		mAdapter = NfcAdapter.getDefaultAdapter(this);	 
		textView = (TextView)findViewById(R.id.loading);
		nfcWrite = new NfcReadWrite();	
		
		mPendingIntent = PendingIntent.getActivity(this, 0, new Intent(this, getClass())
										.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP), 0);	
		IntentFilter ndef = new IntentFilter(NfcAdapter.ACTION_NDEF_DISCOVERED);
		
		try {			
			ndef.addDataType("*/*");		
		} catch (MalformedMimeTypeException e) {
			throw new RuntimeException("fail", e);	
		}		
		mFilters = new IntentFilter[] { ndef, };
		mTechLists = new String[][] { 
				new String[] { MifareClassic.class.getName() } 
				};	
		try {			
			ndef.addDataType("*/*");
			} catch (MalformedMimeTypeException e) {
				throw new RuntimeException("fail", e);	
			}		
		content = getIntent();//넘어오는 intent를 저장	
	}	
	
	@Override	
	public void onResume() {
		super.onResume();		//엑티비티 내에서 nfc 기기를 읽을 수 있도록 설정
		mAdapter.enableForegroundDispatch(this, mPendingIntent, mFilters, mTechLists);
	}
	
	@Override
	public void onNewIntent(Intent intent) {//nfc 기기가 읽히기 전에는 절대 불리지 않음
		Log.i("Foreground dispatch", "Discovered tag with intent: " + intent);	
		String s = content.getStringExtra("message");//넘어온 intent의 내용을 꺼내옴	
		Log.i("nw",s);
		
		if(nfcWrite.WriteNdefNfc(intent, s, false, "", true, cert.getKey("Issuer1").keyPair, cert.getKey("CA").keyPair.getPrivate()))//message를 nfc에 입력	
			textView.setText("Write success"); 		
		else 			
			textView.setText(nfcWrite.getErrorMessage()); 	
		ndefWrite.this.finish();	
	}
}