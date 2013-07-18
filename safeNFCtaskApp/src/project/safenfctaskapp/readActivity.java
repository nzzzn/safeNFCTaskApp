/*
 * project.safenfctaskapp 어플리케이션에 사용될 엑티비티를 모아둔 페키지입니다.
 * readActivity nfc의 데이터를 읽는 엑티비티
*/
package project.safenfctaskapp;

import project.safenfctaskapp.nfc.NFCScript;
import project.safenfctaskapp.nfc.NfcReadWrite;
import project.safenfctaskapp.nfc.StringHandlerPair;
import project.safenfctaskapp.nfc.checkPackage;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

@SuppressLint("HandlerLeak")
public class readActivity extends Activity  implements OnClickListener {
	
	private boolean DEBUG = false;
	private NfcReadWrite nfcReadWrite;
	private checkPackage mCheckPackage;
	

	private TextView textView;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		
		setContentView(R.layout.read);
		
		nfcReadWrite = new NfcReadWrite();
		
		Button btn = (Button)findViewById(R.id.back2);
		btn.setOnClickListener(this);
		
		textView = (TextView)findViewById(R.id.content);
		
		mCheckPackage = new checkPackage(this);
		mCheckPackage.setClear(true);

		
		Intent intent = (Intent) getIntent().getExtras().get("nfc");
		NfcRead(intent);
	}
	
	@Override
	public void onResume() {
		super.onResume();
		mCheckPackage.setClear(true);

		
	}
	
	@Override
	public void onPause() {
		super.onPause();
		mCheckPackage.setClear(true);

	}
	
	public void NfcRead(Intent intent) {
		
		if(DEBUG) Log.i("NfcRead", "readIntent " + intent);
		
		if(nfcReadWrite.readNdefNfc(intent))
		{
			String nfcString = new String();
			
			NFCScript nfcS = new NFCScript(this, mHandler);
			
			if(DEBUG) Log.i("NFCScript : ", nfcS.getPriority() + "");
			
			for(int i = 0; i < nfcReadWrite.getLength(); i++)
			{
				nfcString = nfcReadWrite.getPayload(i);
				nfcS.setSchemeCode(nfcReadWrite.getPayload(i));
				
				nfcS.setDaemon(true);
				nfcS.start();
				
			}
			textView.setText(nfcString);
			
		}
		else
		{
			textView.setText(nfcReadWrite.getErrorMessage());
		}
		
	}

	@Override
	public void onClick(View v) {

		readActivity.this.finish();
		
	}
	
	private Handler mHandler = new Handler(){
		public void handleMessage(Message msg)
		{
			switch (msg.what)
			{
			case 0:
				StringHandlerPair shp = (StringHandlerPair)msg.obj;
				mCheckPackage.setHandler(shp.handler);
				mCheckPackage.askUI(shp.packageName);
				
				if(DEBUG) Log.i("readActivity handleMessage msg", shp.packageName);
				break;
			case 1:
				//1을 불러주면 종료
				readActivity.this.finish();
			}
		}
	};
}