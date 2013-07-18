/*
 * project.safenfctaskapp 어플리케이션에 사용될 엑티비티를 모아둔 페키지입니다.
 * writeActivity nfc에 입력할 ndef메세지틀 설정하는 엑티비티 및 UI
*/
package project.safenfctaskapp;

import java.util.ArrayList;
import java.util.List;

import project.safenfctaskapp.adapter.ndefList;
import project.safenfctaskapp.data.databaseClass;
import project.safenfctaskapp.data.ndefdata;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.pm.ResolveInfo;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.AudioManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.TextView;

@SuppressLint("HandlerLeak")
public class writeActivity extends Activity implements OnClickListener,
										OnItemClickListener, OnItemLongClickListener{

	private ListView ndefList;
	private TextView customTag;
	private int index;
	private int vol;
	Dialog dialog;
	databaseClass db;
	
	public String [] modename = {"ringmode", "wifimode","wificonnection","appstart","ringvolume","player"};
	
	private ndefList nWork;
	private static ArrayList<ndefdata> nList;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
	    
	    requestWindowFeature(Window.FEATURE_NO_TITLE);
	    
	    setContentView(R.layout.write);
	    
	    db = new databaseClass(this);
	    
	    ndefList=(ListView)findViewById(R.id.writelist);
	    customTag = (TextView)findViewById(R.id.customtag);
	    dialog = new Dialog(this);
	    
	    Button done = (Button)findViewById(R.id.done);
	    Button back = (Button)findViewById(R.id.back);
	    
	    done.setOnClickListener(this);
	    back.setOnClickListener(this);
	    customTag.setOnClickListener(this);
//	    get = getIntent();

	}
	
	@Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        nWork.notifyDataSetChanged();
    } 
	
	@Override
	public void onStart() {//리스트를 어뎁터에 정의 후 리스트 뷰에 할당
    	super.onStart();
    	Log.i("onStart()","app start");
    	
    	nList = makelist();

    	nWork = new ndefList(this, nList);
        
        ndefList.setAdapter(nWork);
        ndefList.setOnItemClickListener(this);
        ndefList.setOnItemLongClickListener(this);
	}
	
    public void onItemClick(final AdapterView<?> parent, View v, final int position, long leng) {//UI 부분입니다
    	//리스트의 아이템이 눌릴 때 불리는 메소드
    	
    	final Message msg = new Message();
    	final ndefdata nd = (ndefdata)parent.getItemAtPosition(position);
    	
    	switch(position) {
    	default:
    		msg.obj = nd;
    		msg.what = 0;
    		
    		mHandler.sendMessage(msg);
    		
    		break;
    	case 2:
            final Dialog dialog = new Dialog(this);
            dialog.setContentView(R.layout.dialog_editbox_two);
            dialog.setTitle("WIFI Setting");

            final EditText id = (EditText) dialog.findViewById(R.id.edit1);
            final EditText pwd =(EditText) dialog.findViewById(R.id.edit2);

            Button save = (Button) dialog.findViewById(R.id.det_ok);
            Button cancle = (Button) dialog.findViewById(R.id.det_cancel);
            //에딧 박스 두개와 버튼 두개의 다이얼로그 생성
            
            save.setOnClickListener(new OnClickListener() {
                public void onClick(View v) {//save 버튼을 입력 시
                    dialog.dismiss();//다이얼로그 제거
                    nd.setContent(id.getText().toString() + "\\" 
    						+ pwd.getText().toString());
                    nd.setConNum(2);
                    msg.obj = nd;
            		msg.what = 0;
            		
            		mHandler.sendMessage(msg);
                    Log.i("wificontent",nd.getContent());
                   //자료 저장
                }
            });

            cancle.setOnClickListener(new OnClickListener() {
                public void onClick(View v) {
                    dialog.dismiss();
                }
            });

            dialog.show();//다이얼로그를 뿌림
    		break;
    	case 3:
    		PackageManager manager = getPackageManager();
    		final Intent mainIntent = new Intent(Intent.ACTION_MAIN, null);
    		mainIntent.addCategory(Intent.CATEGORY_LAUNCHER);
    		
    		List<ResolveInfo> resolveInfos = manager.queryIntentActivities(mainIntent, 0);
    		final String[] pkgName = new String[resolveInfos.size()];
    		String[] appName = new String[resolveInfos.size()];
    		
    		for (int i = 0;i < resolveInfos.size(); i++) {
    		 
    		   ResolveInfo ri = resolveInfos.get(i);
    		 
    		   pkgName[i] = ri.activityInfo.applicationInfo.packageName; //패키지 이름
    		   appName[i] = ri.loadLabel(manager).toString();

    		}
    		AlertDialog.Builder alt_bld = new AlertDialog.Builder(this);
    		alt_bld.setTitle("Select one Digit");
    		alt_bld.setSingleChoiceItems(appName , -1,
    				new DialogInterface.OnClickListener() {
    			public void onClick(DialogInterface dialog, int item) {
    				index = item;
    			}
    		});//리스트를 뿌려주는 다이얼로그 생성
    		alt_bld.setPositiveButton("확인", new DialogInterface.OnClickListener() {
    			@Override
    			public void onClick(DialogInterface dialog, int which) {
    				nd.setContent(pkgName[index]);
    				msg.obj = nd;
    	    		msg.what = 0;
    	    		
    	    		mHandler.sendMessage(msg);
    			}
    		});
    		alt_bld.setNegativeButton("취소",
    				new DialogInterface.OnClickListener() {
    			@Override
    			public void onClick(DialogInterface dialog, int which) {
    			}
    		});
    		 
    		AlertDialog alert = alt_bld.create();
    		alert.show();
    		break;
    	case 4:
    		final AlertDialog.Builder alerts = new AlertDialog.Builder(this); 
    		
    	    alerts.setTitle("Volume control"); 

    	    LinearLayout linear=new LinearLayout(this); 

    	    linear.setOrientation(1); 

    	    final SeekBar seek = new SeekBar(this); 
    	    seek.setMax(((AudioManager)getSystemService(AUDIO_SERVICE)).
    	    							getStreamMaxVolume(AudioManager.STREAM_RING));
    	    seek.incrementProgressBy(0);
    	    
    	    seek.setOnSeekBarChangeListener( new SeekBar.OnSeekBarChangeListener() {
				
				@Override
				public void onStopTrackingTouch(SeekBar seekBar) {
					// TODO Auto-generated method stub
					
				}
				
				@Override
				public void onStartTrackingTouch(SeekBar seekBar) {
					// TODO Auto-generated method stub
					
				}
				
				@Override
				public void onProgressChanged(SeekBar seekBar, int progress,
						boolean fromUser) {
					// TODO Auto-generated method stub
					vol = progress;//값이 변경되었을 때 불리는 메소드. 값을 저장한다
				}
			});
    	    
    	    linear.addView(seek); 

    	    alerts.setView(linear); 

    	    alerts.setPositiveButton("확인",new DialogInterface.OnClickListener() 
    	    { 
    	        public void onClick(DialogInterface dialog,int id)  
    	        { 
    	        	
    	        	nd.setContent(String.valueOf(vol));
    	        	msg.obj = nd;
    	    		msg.what = 0;
    	    		
    	    		mHandler.sendMessage(msg);
    	        }
    	    }); 

    	    alerts.setNegativeButton("취소",new DialogInterface.OnClickListener()  
    	    { 
    	        public void onClick(DialogInterface dialog,int id)  
    	        { 
    	        } 
    	    }); 
    	    alerts.show();
    		break;
    	}
	}
	
	public ArrayList<ndefdata> makelist() {
		ArrayList<ndefdata> array = new ArrayList<ndefdata>();
		ndefdata nd;
		int i;
		for (i=0;i<modename.length;i++) {
			nd = new ndefdata();
			nd.setName(modename[i]);
			nd.setMode(i);
			nd.setContent("null");
			array.add(nd);
		}
/*		db.open(true);
		Cursor c;
		c = db.read("SELECT * FROM customtag");
		
		while(c.isAfterLast() == false) {
			nd = new ndefdata();
			nd.setName(c.getString(1));
			nd.setContent(c.getString(2));
			array.add(nd);
		}
		db.close();*/
		return array;
	}

	

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.back://뒤로 버튼
			writeActivity.this.finish();
			break;
		case R.id.customtag:
			Intent intent = new Intent(writeActivity.this,makeTag.class);
			startActivity(intent);
			writeActivity.this.finish();
			break;
		}
		
	}

	@Override
	public boolean onItemLongClick(AdapterView<?> parent, View v, int position,
			long length) {
		final AlertDialog.Builder alerts = new AlertDialog.Builder(this); 
		if (position == 2)
			((ndefdata)parent.getItemAtPosition(position)).setConNum(2);
		
	    alerts.setTitle("scheme code"); 
	    String message = makeMessage((ndefdata)parent.getItemAtPosition(position));

	    LinearLayout linear=new LinearLayout(this); 

	    linear.setOrientation(1); 
	    
	    TextView tv = new TextView(this);
	    tv.setText(message);

	    linear.addView(tv); 

	    alerts.setView(linear); 

	    alerts.setPositiveButton("확인",new DialogInterface.OnClickListener() 
	    { 
	        public void onClick(DialogInterface dialog,int id)  
	        { 
	        }
	    }); 
	    alerts.show();
		return false;
	}
	
	public String makeMessage(ndefdata nd) {//mode에 따른 message 정의 
		String message = "";
		switch (nd.getMode()) {
		case custom:
			message = nd.getContent();
			break;
		case wifimode:
			message = "(define wifiMgr (.getSystemService context (android.content.Context.WIFI_SERVICE$)))\n" +
		            "(define wifiEnabled (.isWifiEnabled wifiMgr))\n" +
                    "(if (equal? wifiEnabled #t) (.setWifiEnabled wifiMgr #f)\n" +
	                "(.setWifiEnabled wifiMgr #t))";
			break;
		case ringmode:
			message = "(define vibMgr (.getSystemService context (android.content.Context.AUDIO_SERVICE$)))" + 
				      "(define ringerMode (.getRingerMode vibMgr))\n" +
				      "(if (equal? ringerMode (android.media.AudioManager.RINGER_MODE_SILENT$))\n" +
				      "(.setRingerMode vibMgr (android.media.AudioManager.RINGER_MODE_NORMAL$))\n" +
				 	  "(if (equal? ringerMode (android.media.AudioManager.RINGER_MODE_NORMAL$))\n" +
				 	  "(.setRingerMode vibMgr (android.media.AudioManager.RINGER_MODE_VIBRATE$))\n" + 
				      "(.setRingerMode vibMgr (android.media.AudioManager.RINGER_MODE_SILENT$))))";
			break;
		case wificonnection:
			String[] contents = nd.setContentToStringArray();
			message = "(define wifiMgr (.getSystemService context (android.content.Context.WIFI_SERVICE$)))\n" +
					"(define wc (android.net.wifi.WifiConfiguration.))\n" +
					"(.SSID$ wc \"\\\"" + contents[0] + "\\\"\")\n" +			//스킴의 스트링으로 잘 컨버전 될까?
					"(.preSharedKey$ wc \"\\\"" + contents[1] + "\\\"\")\n" +
					"(.hiddenSSID$ wc #t)\n" +
					"(.status$ wc (android.net.wifi.WifiConfiguration$Status.ENABLED$))\n" +
					"(.set (.allowedKeyManagement$ wc) 1)\n" +
					"(define res (.addNetwork wifiMgr wc))\n" +
					"(.enableNetwork wifiMgr res #t)"; 						//캐스팅 확인
			break;
		case appstart:
			String con = nd.getContent();
			message = "(define pm (.getPackageManager context))\n" +
					"(define i (.getLaunchIntentForPackage pm \"" + con + "\"))\n" +
					"(.setFlags i (android.content.Intent.FLAG_ACTIVITY_NEW_TASK$))\n" +
					"(.startActivity context i)";
			break;
		case ringvolume:
			String volume = nd.getContent();
			message = "(define am (.getSystemService context " + 
					"(android.content.Context.AUDIO_SERVICE$)))\n" +
					"(define vol " + volume + ")\n" +
					"(.setStreamVolume am (android.media.AudioManager.STREAM_RING$) " +
					"vol (android.media.AudioManager.FLAG_PLAY_SOUND$))";
			break;
		case player:
			/*명필 수정 -	레퍼런스 폰에서 작동 안함.. type video가 없다. 삼성폰에서는 정상적 작동함.
			message = "(define intent (android.content.Intent. \"android.intent.action.VIEW\"))\n" +
					"(define uri (android.net.Uri.parse (android.net.Uri.encode \"http://daily3gp.com/vids/747.3gp\")))\n"+
					"(.setDataAndType intent uri \"video/*\")\n" +
					"(.startActivity context intent)\n";
			*/
			
			//아이유 동영상임.
			message = "(define intent (android.content.Intent. \"android.intent.action.VIEW\" (android.net.Uri.parse \"http://www.youtube.com/embed/EyR9lx92x9A?rel=0\")))\n" +
					"(.startActivity context intent)\n";
			break;
		}
		return message;
	}//앰 내에서 스킴 코드를 볼 수 있도록 
	//프리퍼런스 액티비티->android.jar을 읽어서 on/off 설정 가능하도록 제작
	//패키지에 따라 사용자에게 질의. -> 질의 후 설정 완료

	
	private Handler mHandler = new Handler(){
		public void handleMessage(Message msg)
		{
			switch (msg.what)
			{
			case 0:
				
				Intent writendef = new Intent(writeActivity.this, ndefWrite.class);
				
				String message;
	    		message = makeMessage((ndefdata)msg.obj);
				
				writendef.putExtra("message", message);
		    	startActivity(writendef);
		    	writeActivity.this.finish();
		    	
				Log.i("writeActivity handleMessage msg", "success");
				break;
			}
		}
	};
}
