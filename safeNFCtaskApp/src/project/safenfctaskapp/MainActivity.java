/*
 * project.safenfctaskapp 어플리케이션에 사용될 엑티비티를 모아둔 페키지입니다.
 * MainActivity 어플리케이션이 실행될 때 사용될 메인 엑티비티
 * github update
*/
package project.safenfctaskapp;

import java.io.FileInputStream;
import java.io.IOException;

import android.nfc.NfcAdapter;
import android.nfc.tech.MifareClassic;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.app.Activity;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.IntentFilter.MalformedMimeTypeException;

import project.safenfctaskapp.data.workdata;
import project.safenfctaskapp.mcoupon.Certificate;
import project.safenfctaskapp.mcoupon.MCouponSchemeSrc;
import project.safenfctaskapp.nfc.NfcReadWrite;

public class MainActivity extends Activity implements OnClickListener, OnItemClickListener{
	private Certificate cert = new Certificate();
	private NfcReadWrite nfcReadWrite;

	private static NfcAdapter mAdapter;
	private static PendingIntent mPendingIntent;
	private static IntentFilter[] mFilters;
	private static String[][] mTechLists;
	
	public enum NfcMode {Read, Write, BeamIssuer1, BeamCashier1, BeamIssuer2, BeamCashier2, BeamUser};
	
	TextView textView = null;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		
		setContentView(R.layout.mainactivity);
		
		Button write = (Button)findViewById(R.id.write);
		Button read = (Button)findViewById(R.id.setting);
		
		TextView tv = (TextView)findViewById(R.id.test);
		
		textView = (TextView)findViewById(R.id.textview_main);;
		Button btnRead = (Button)findViewById(R.id.btn_read);
		Button btnWrite = (Button)findViewById(R.id.btn_write);
		Button btnIssuer1 = (Button)findViewById(R.id.btn_issuer1);
		Button btnCashier1 = (Button)findViewById(R.id.btn_cashier1);
		Button btnIssuer2 = (Button)findViewById(R.id.btn_issuer2);
		Button btnCashier2 = (Button)findViewById(R.id.btn_cashier2);
		Button btnBonus = (Button)findViewById(R.id.btn_bonus);
		Button btnTestCase = (Button)findViewById(R.id.btn_testcase);
		
		
		write.setOnClickListener(this);
		read.setOnClickListener(this);
		tv.setOnClickListener(this);
		
		
		btnRead.setOnClickListener(this);
		btnWrite.setOnClickListener(this);
		btnIssuer1.setOnClickListener(this);
		btnCashier1.setOnClickListener(this);
		btnIssuer2.setOnClickListener(this);
		btnCashier2.setOnClickListener(this);
		btnBonus.setOnClickListener(this);
		btnTestCase.setOnClickListener(this);
		
		
		mAdapter = NfcAdapter.getDefaultAdapter(this);
		
		mPendingIntent = PendingIntent.getActivity(this, 0, new Intent(this, getClass()).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP), 0);
		IntentFilter ndef = new IntentFilter(NfcAdapter.ACTION_NDEF_DISCOVERED);
		
		try {
			ndef.addDataType("*/*");
		} catch (MalformedMimeTypeException e) {
			throw new RuntimeException("fail", e);
		}
		mFilters = new IntentFilter[] { ndef, };

		mTechLists = new String[][] { new String[] { MifareClassic.class.getName() } };
		
		Log.i("onCreate()","app create");
	
		nfcReadWrite = new NfcReadWrite();
		
		Intent intent = getIntent();
		if (intent.getExtras().getInt("noNFC") != 1) {
			onNewIntent(intent);
			MainActivity.this.finish();
		}
	
	}
	
	@Override
	public void onResume(){
		super.onResume();
		mAdapter.enableForegroundDispatch(this, mPendingIntent, mFilters, mTechLists);
	}
	
	@Override
	public void onPause() {
		super.onPause();
		mAdapter.disableForegroundDispatch(this);
	}
	
	@Override
	public void onNewIntent(Intent intent) {//nfc를 읽음
		Log.i("Foreground dispatch", "Discovered tag with intent: " + intent);
		
		Intent read = new Intent(MainActivity.this, readActivity.class);
		read.putExtra("nfc", intent);
		startActivity(read);
	}
	
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.setting:
			Intent set = new Intent(MainActivity.this, setting.class);
			startActivity(set);
			break;
			
		case R.id.btn_read:
			mAdapter.enableForegroundDispatch(this, mPendingIntent, mFilters, mTechLists);
			break;
			
		case R.id.write:
			Intent write = new Intent(MainActivity.this, writeActivity.class);
			startActivity(write);
			break;
			
		case R.id.btn_issuer1:
			
			//암호화 한것
			if(nfcReadWrite.BeamNdefNfc(MCouponSchemeSrc.issuer1, false, "", true, cert.getKey("Issuer1").keyPair, cert.getKey("CA").keyPair.getPrivate()))
			{
				mAdapter.setNdefPushMessage(nfcReadWrite.getBeamMessage(), MainActivity.this);
				textView.setText("beam issuer1 ready!");
			}
			else
			{
				textView.setText(nfcReadWrite.getErrorMessage()); 
			}
			break;
			
		case R.id.btn_cashier1:
			
			//암호화 한것
			if(nfcReadWrite.BeamNdefNfc(MCouponSchemeSrc.cashier1 , false, "", true, cert.getKey("Issuer1").keyPair, cert.getKey("CA").keyPair.getPrivate()))
			{
				mAdapter.setNdefPushMessage(nfcReadWrite.getBeamMessage(), MainActivity.this);
				textView.setText("beam cashier1 ready!");
			}
			else
			{
				textView.setText(nfcReadWrite.getErrorMessage()); 
			}
			break;
			
		case R.id.btn_issuer2:
			textView.setText("Issuer 2!");
			break;
			
		case R.id.btn_cashier2:
			textView.setText("Cashier 2!");
			break;
			
		case R.id.btn_bonus:
			byte[] bonus = new byte[100];
			int index = 0;
			try {
				FileInputStream fis = openFileInput("bonus.text");
				int i;
				
				while((i = fis.read()) != -1)
				{
					bonus[index] = (byte) i;
					index++;
				}
				fis.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			textView.setText(new String(bonus, 0, index));
			break;
		
		case R.id.btn_testcase:
			Intent testCase = new Intent(MainActivity.this, TestCase.class);
			startActivity(testCase);
		
		}
	}


	@Override
	public void onItemClick(AdapterView<?> parent, View v, int position, long length) {
		Intent intent = new Intent(MainActivity.this, writeActivity.class);
		workdata wd = (workdata) parent.getItemAtPosition(position);
		intent.putExtra("workname",wd.getName());
		startActivity(intent);
	}

}