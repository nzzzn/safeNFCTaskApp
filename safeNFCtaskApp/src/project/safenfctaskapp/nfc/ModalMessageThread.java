package project.safenfctaskapp.nfc;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;

public class ModalMessageThread extends Thread
{
	boolean DEBUG = false;
	boolean flag;
	int result;
	Looper myLooper;
	
	public ModalMessageThread()
	{
		flag = true;
	}
	
	Handler mBackHandler;
	
	public Handler getHandler()
	{
		while(mBackHandler == null)
		{
		
		}
		
		return mBackHandler;
	}
	
	@Override
	public void run()
	{
		Looper.prepare();
		myLooper = Looper.myLooper();
		mBackHandler = new Handler()
		{
			public void handleMessage(Message msg)
			{
				if(DEBUG) Log.i("message get", msg.what + "");
				flag = false;
				result = msg.what;
				/*
				 * 2013-07-11 명필 루퍼가 5초이상 대기하면 쓰레드 종료시킴
				 * 메시지를 받으면 종료하도록 처리 했음.
				 * 원래 사용할때도 한번 사용하면 다시 생성해서 사용하는 방식이였음.
				 */
				myLooper.quit();
			}
		};
		Looper.loop();
	}
	
	public int getResult()
	{
		while(flag == true)
		{
			
		}
		
		return result;
	}
}