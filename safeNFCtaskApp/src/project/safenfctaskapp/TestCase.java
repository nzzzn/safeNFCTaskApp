package project.safenfctaskapp;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import project.safenfctaskapp.mcoupon.MCouponSchemeSrc;
import project.safenfctaskapp.nfc.NFCScript;
import project.safenfctaskapp.nfc.StringHandlerPair;
import project.safenfctaskapp.nfc.checkPackage;

import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

public class TestCase extends Activity implements OnClickListener  {

	boolean DEBUG = false;
	
	private checkPackage mCheckPackage;
	
	private String viewstr = "";
	String[] localUpdate = null;
	
	TextView textview = null;
	
	NFCScript nfcS;
	int index = -2; //MCoupon 먼저 실행하기 위해서.
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_test_case);

		Button btnback = (Button)findViewById(R.id.TestCase_back);
		Button btnstart = (Button)findViewById(R.id.testcase_start);
		
		btnback.setOnClickListener(this);
		btnstart.setOnClickListener(this);
		
		textview = (TextView)findViewById(R.id.testcase_text);
		
		mCheckPackage = new checkPackage(this);
		mCheckPackage.setClear(true);

	}

	@SuppressLint("HandlerLeak")
	private Handler mHandler = new Handler(){
		@SuppressWarnings("static-access")
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
				if(nfcS != null)
				{
					if(-1 == index)					//-1이면 issuer 실행
					{
						viewstr += "issuer1 : " + nfcS.getResult() + "\n";
					}
					else if(0 == index)					//-2면 cashier 실행
					{
						viewstr += "casher1 : " + nfcS.getResult() + "\n";
					}
					else
					{
						viewstr += localUpdate[index] + " : " + nfcS.getResult() + "\n";
					}
					textview.setText(viewstr);
				}
				
				String nfcString = new String();
				MCouponSchemeSrc mcouponSrc = new MCouponSchemeSrc();
				index++;
				
				if(localUpdate.length == index)			//다 했으면 종료
					break;
				else if(-1 == index)					//-1이면 issuer 실행
					nfcString = mcouponSrc.issuer1;
				else if(0 == index)					//-2면 cashier 실행
					nfcString = mcouponSrc.cashier1;
				else
					nfcString = readFile(localUpdate[index]);	
				
				nfcS = new NFCScript(TestCase.this, mHandler);

				nfcS.setSchemeCode(nfcString);
				
				nfcS.setDaemon(true);
				nfcS.start();
				break;
			}
		}
	};
	
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.TestCase_back:
			TestCase.this.finish();
			break;
		case R.id.testcase_start:
			testStart();
			break;
		}

	}
	
	public void testStart()
	{
		
		WifiManager wifiMgr = (WifiManager)getSystemService(android.content.Context.WIFI_SERVICE);
		if(wifiMgr.isWifiEnabled() == false)
		{
			textview.setText("wifi가 연결되지 않았습니다.\n연결후 시도하세요.");
			return;
		}
		
		
		//로컬파일의 update.txt을 읽어 드린다.
		localUpdate = readLineFile("update.scm");
		
		//서버버전파일을 다운로드한다.		
		downFile("http://nzzzn.dothome.co.kr/jscheme/update.scm", "update.scm");
		
		//로컬파일과 서버파일 확인해서 업데이트 있으면 다운로드 한다.
		String[] serverUpdate = readLineFile("update.scm");
		if(localUpdate == null || serverUpdate[0].compareTo(localUpdate[0]) != 0)
		{
			for(int i = 1; i < serverUpdate.length; i++)
			{
				downFile("http://nzzzn.dothome.co.kr/jscheme/" + serverUpdate[i], serverUpdate[i]);
			}
		}
		
		localUpdate = readLineFile("update.scm");
		
		viewstr += localUpdate[0] + "\n";
		textview.setText(viewstr);

		mHandler.sendEmptyMessage(1);
		
	}
	
	public String[] readLineFile(String filename)
	{
		File file = new File(Environment.getExternalStorageDirectory() + "/Android/data/safeNfcTaskApp/update/" + filename);
		BufferedReader reader = null;
		String[] result = null;
		
	    try {
	    	
	        reader = new BufferedReader(new FileReader(file));
	        List<String> list = new ArrayList<String>();

	        while(reader.ready()) 
	        {
	            String line = reader.readLine();
	            
	            if(line != null && !"".equals(line.trim())) {
	                list.add(line);
	            }
	        }
	        
	        if(list.size() > 0) {
	            result = new String[list.size()];
	            for(int i = 0;i<list.size();i++) {
	                result[i] = list.get(i);
	            }
	        }
	        
	    } catch(IOException ioe) {
	    	
	    } finally {
	        if(reader != null)
	        {
				try {
					reader.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
	        }
	    }
	    
	    return result;
	}
	
	public String readFile(String filename)
	{
		
		String str[] = readLineFile(filename);
		String result = "";
		
		for(int i = 0; i < str.length; i++)
		{
			result += str[i] + "\n";
		}
				
		return result;
		
	}

	public void downFile(String uri, String filename)
	{
    	DownloadHttpThread down = new DownloadHttpThread(uri);
    	down.start();
    	
    	try {
			down.join();
		} catch (InterruptedException e) {
			
		}
    	down.saveFile(filename);
	}
	

	public class DownloadHttpThread extends Thread
	{
		private String url;
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		
		public DownloadHttpThread(String Url)
		{
			url = Url;
		}
		
		@Override
		public void run()
		{
	        try {
	        	URL is_from_url = new URL(url);
	        	InputStream is = is_from_url.openStream();
        		
        		byte buffer[] = new byte[1024];
        		int count;
        		while((count = is.read(buffer, 0, 1024)) != -1)
        		{
        			bos.write(buffer, 0, count);
        		}
        		
       		}
       		catch (IOException e) {
    			if(DEBUG) Log.i ("NFCScript", "[Exception] : " + e.toString() );
       		}
		}
		
		public void saveFile(String fileName)
		{	
			
			String path = Environment.getExternalStorageDirectory() + "/Android/data/safeNfcTaskApp/update";
			File file = new File(path);
			if( !file.exists() )
			{
				file.mkdirs();
			}
			
			file = new File(path + "/" + fileName);
			
			try {
				FileOutputStream fos = new FileOutputStream(file);
				fos.write(getDownByteArray());
				fos.close();
				
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		public byte[] getDownByteArray()
		{
			return bos.toByteArray();
		}
		
	}
}
