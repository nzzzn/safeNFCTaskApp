package project.safenfctaskapp;

import project.safenfctaskapp.data.databaseClass;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Window;

public class loading extends Activity {
	
	private databaseClass db = new databaseClass(this);
	private String[] packages = {"META-INF", "org", "org.xmlpull", "org.xmlpull.v1", "org.xmlpull.v1.sax2", "org.w3c", "org.w3c.dom", "org.w3c.dom.ls", "org.xml", "org.xml.sax", "org.xml.sax.ext", "org.xml.sax.helpers", "org.apache", "org.apache.http", "org.apache.http.protocol", "org.apache.http.io", "org.apache.http.params", "org.apache.http.client", "org.apache.http.client.protocol", "org.apache.http.client.params", "org.apache.http.client.methods", "org.apache.http.client.utils", "org.apache.http.client.entity", "org.apache.http.conn", "org.apache.http.conn.params", "org.apache.http.conn.ssl", "org.apache.http.conn.routing", "org.apache.http.conn.util", "org.apache.http.conn.scheme", "org.apache.http.util", "org.apache.http.impl", "org.apache.http.impl.io", "org.apache.http.impl.client", "org.apache.http.impl.conn", "org.apache.http.impl.conn.tsccm", "org.apache.http.impl.auth", "org.apache.http.impl.entity", "org.apache.http.impl.cookie", "org.apache.http.message", "org.apache.http.auth", "org.apache.http.auth.params", "org.apache.http.entity", "org.apache.http.cookie", "org.apache.http.cookie.params", "org.apache.commons", "org.apache.commons.logging", "org.json", "junit", "junit.runner", "junit.framework", "assets", "assets.webkit", "assets.sounds", "assets.images", "javax", "javax.crypto", "javax.crypto.spec", "javax.crypto.interfaces", "javax.sql", "javax.xml", "javax.xml.parsers", "javax.xml.xpath", "javax.xml.transform", "javax.xml.transform.stream", "javax.xml.transform.dom", "javax.xml.transform.sax", "javax.xml.validation", "javax.xml.datatype", "javax.xml.namespace", "javax.microedition", "javax.microedition.khronos", "javax.microedition.khronos.opengles", "javax.microedition.khronos.egl", "javax.net", "javax.net.ssl", "javax.security", "javax.security.auth", "javax.security.auth.callback", "javax.security.auth.login", "javax.security.auth.x500", "javax.security.cert", "android", "android.app", "android.app.backup", "android.app.admin", "android.nfc", "android.nfc.tech", "android.service", "android.service.textservice", "android.service.wallpaper", "android.service.dreams", "android.widget", "android.drm", "android.hardware", "android.hardware.display", "android.hardware.usb", "android.hardware.input", "android.telephony", "android.telephony.cdma", "android.telephony.gsm", "android.graphics", "android.graphics.drawable", "android.graphics.drawable.shapes", "android.inputmethodservice", "android.gesture", "android.bluetooth", "android.test", "android.test.suitebuilder", "android.test.suitebuilder.annotation", "android.test.mock", "android.webkit", "android.preference", "android.database", "android.database.sqlite", "android.speech", "android.speech.tts", "android.sax", "android.text", "android.text.format", "android.text.util", "android.text.style", "android.text.method", "android.mtp", "android.util", "android.accessibilityservice", "android.view", "android.view.textservice", "android.view.inputmethod", "android.view.animation", "android.view.accessibility", "android.renderscript", "android.animation", "android.provider", "android.appwidget", "android.location", "android.net", "android.net.wifi", "android.net.wifi.p2p", "android.net.wifi.p2p.nsd", "android.net.sip", "android.net.http", "android.net.nsd", "android.net.rtp", "android.os", "android.os.storage", "android.media", "android.media.audiofx", "android.media.effect", "android.accounts", "android.opengl", "android.content", "android.content.pm", "android.annotation", "android.security", "java", "java.io", "java.awt", "java.awt.font", "java.sql", "java.nio", "java.nio.channels", "java.nio.channels.spi", "java.nio.charset", "java.nio.charset.spi", "java.beans", "java.text", "java.util", "java.util.logging", "java.util.prefs", "java.util.concurrent", "java.util.concurrent.atomic", "java.util.concurrent.locks", "java.util.zip", "java.util.jar", "java.util.regex", "java.math", "java.lang", "java.lang.reflect", "java.lang.ref", "java.lang.annotation", "java.net", "java.security", "java.security.spec", "java.security.acl", "java.security.interfaces", "java.security.cert", "dalvik", "dalvik.system", "dalvik.bytecode", "dalvik.annotation", "com", "com.android", "com.android.internal", "com.android.internal.util"};
	public static final String KEY_FIRST = "first"; //처음 세팅이 되는 것인지 아닌지 판단
	private classfindTask task;

	private boolean first;

	
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
	
	    // TODO Auto-generated method stub
	    requestWindowFeature(Window.FEATURE_NO_TITLE);
		
		setContentView(R.layout.loading);
		
		SharedPreferences prefs = getSharedPreferences("safeNFCs", MODE_PRIVATE); 
		
		first = prefs.getBoolean(KEY_FIRST, true); 
	}
	
	@SuppressLint("SdCardPath")
	public class classfindTask extends AsyncTask<Void,Void,String> {

		private Activity activity;
		public ProgressDialog dialog;
		
		public classfindTask(Activity con) {
			this.activity = con;
			dialog = new ProgressDialog(this.activity);
		}
		
		@Override
		protected String doInBackground(Void... arg0) {
			// TODO Auto-generated method stub

			if (first == true) {//이 엑티비티가 설치 후 처음 실행되는 것이라면
				db.open(false);
				for (int i = 0;i <packages.length;i++) {
					String name = packages[i];
					db.write("INSERT INTO classlist (_id, classname, usable, askable) VALUES(null,'"
									+name+"','1','1');");//db insert
				}
				db.close();
				first = false;
			}
			return null;
		}
		
		@Override
		protected void onPreExecute() {
			this.dialog.setMessage("잠시만 기다려주세요");
			this.dialog.setCancelable(false);
			this.dialog.show();
		}
		
		@Override
		protected void onPostExecute(String resultStr) {
			this.dialog.setCancelable(true);
			
			if (this.dialog.isShowing()==true)
				this.dialog.dismiss();
			Intent intent = new Intent(loading.this,MainActivity.class);
			intent.putExtra("noNFC", 1);
			startActivity(intent);
			loading.this.finish();
		}
	}
	
	@Override
    public void onStart() {
    	super.onStart();
    	Log.i("onStart()","app start");
    	task = new classfindTask(this);
    	task.execute();
    }
	
	
	@Override
	public void onStop() {
		super.onStop();
			SharedPreferences prefs = getSharedPreferences("safeNFCs", MODE_PRIVATE); 

			SharedPreferences.Editor editor = prefs.edit(); 

			editor.putBoolean(KEY_FIRST, first); 

			editor.commit(); 

	}

}
