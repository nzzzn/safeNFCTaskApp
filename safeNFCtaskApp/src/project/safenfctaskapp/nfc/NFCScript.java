package project.safenfctaskapp.nfc;

import android.content.Context;

import java.lang.reflect.Field;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.SequenceInputStream;
import project.safenfctaskapp.jscheme.AccessControlException;
import project.safenfctaskapp.jscheme.Environment;
import project.safenfctaskapp.jscheme.InputPort;
import project.safenfctaskapp.jscheme.Scheme;

import android.os.Handler;
import android.util.Log;

/**
 * This class represents an NFC tag reader handling special NFC URIs to Scheme
 * programs, e.g., "U" "http://localhost/nfctag.scm". The name of Scheme programs
 * is assumed to end with ".scm".
 *  
 * This is version 0.1.
 *      
 * @author Kwanghoon Choi, kwanghoon.choi@yonsei.ac.kr Copyright 2011
 *           
 **/

public class NFCScript extends Thread {
	boolean DEBUG = false;

	private Policy policy;
	private String schemeCode;
	private Context main;
	private Handler dialogHandler;
	
	private String result = "";
	
	public NFCScript(Context mainActivity, Handler dialogHandler)
	{
		main = mainActivity;
		policy = new Policy(dialogHandler);
		this.dialogHandler = dialogHandler;
	}
    	
	@Override
	public void run()
	{
        //String url_str = resolveIntent(getIntent());
        
//        if (url_str != null) {
        //코멘트를 해서 뭘 말들어 봐라..
        //스트링에서 input 스트림을 만드는 것.
        //StringBufferInputStream
        /*
        try {
        		InputStream is_from_url =
        				new URL ("http://192.168.0.15/~khchoi/nfc.scm").openStream();
        		InputStream is_from_userpolicy =
        				new URL("http://192.168.0.15/~khchoi/user_policy.scm").openStream();
        		InputStream is_from_string = new StringBufferInputStream(p.getCacheLib());
        		
        		SequenceInputStream is = new SequenceInputStream(is_from_string,
        									new SequenceInputStream (is_from_userpolicy, is_from_url));
        		startScheme(new InputPort(is));
       		}
       		catch (IOException e) {
    			Log.i ("NFCScript", "[Exception] : " + e.toString() );
       		}
//        }
 * 
 */
		
		
        String userpolicy = "(define (constr_policy x y z) #t)                               " +
        		"(define (method_policy x y z) #t)                               " +
        		"(define (field_policy x y z) #t)                                " +
        		"(define (intent_policy a d)                                     " +
        		"         (cond ((and (string=? a \"android.intent.action.VIEW\")  " +
        		"                     (string=? d \"http://www.naver.com\")) #t)   " +
        		"               (else #t)))                                      " +
        		"                                                                " +
        		"(define policy (cons constr_policy                              " +
        		"		     (cons method_policy                                     " +
        		"			   (cons field_policy                                      " +
        		"				 (cons intent_policy ())))))  \n";
		
		
        
        result = "";
		try {
			InputStream is_from_schemeCode = new ByteArrayInputStream(schemeCode.getBytes("UTF-8"));
			InputStream is_from_userpolicy = new ByteArrayInputStream(userpolicy.getBytes("UTF-8"));
			InputStream is_from_string = new ByteArrayInputStream(policy.getCacheLib().getBytes("UTF-8"));
			
			SequenceInputStream is = new SequenceInputStream(is_from_string,
										new SequenceInputStream (is_from_userpolicy, is_from_schemeCode));
			startScheme(new InputPort(is));
		}
			catch (IOException e) {
				result = e.toString(); 
				dialogHandler.sendEmptyMessage(1);
				Log.i ("NFCScript", "[Exception] : " + e.toString() );
				return;
		}
      
		
		result += "Ends";
		Log.i("NFCScript", "[NFCScriptActivity] ENDS.");
		dialogHandler.sendEmptyMessage(1);
	}
	
	public String getResult()
	{
		return result;
	}
    
	static Scheme s;

	public Scheme getScheme() {
		if (s == null) {
			s = new Scheme(new String[0], policy); // Creating a Scheme interpreter with a hook interface
			policy.setScheme(s);
		}
		return s;
	}
	
	@SuppressWarnings("static-access")
	public void startScheme(InputPort inputPort) {
		Scheme scheme = getScheme();
		
		Environment e = scheme.getGlobalEnvironment();
		
		//addResourceFields(R.layout.class, "layout", e);
		//addResourceFields(R.drawable.class, "drawable", e);
		//addResourceFields(R.id.class, "id", e);
		
		//addResourceFields (Uri.class, "Uri", e);
		//addResourceFields (Intent.class, "Intent", e);
		
		//명필 인텐드가 this가 아님
		//e.define("context".intern(), this);
		e.define("context".intern(), main);
		
		java.lang.Object ret;
		try {
			ret = scheme.load(inputPort);
		}
		catch(AccessControlException exn) {
    		ret = "exception (AC)";
		}
		catch (RuntimeException exn) {
			
    		Log.i ("NFCScript", "[Exception] : " + exn.toString() );
    		//명필 에러 처리
    		//Toast.makeText(this, exn.toString(), 10000).show();
    		ret = "exception";
    		
    		if (exn instanceof AccessControlException) {
    			AccessControlException eexn = (AccessControlException)exn;
    			Log.i ("NFCScript", "[Exception] : " + eexn.msg );
    			//명필 에러 처리
        		//Toast.makeText(this, eexn.msg, 1000).show();
        		ret = "exception";
    		}
		}
		
		Log.i("NFCScriptActivity:", scheme.SchemeLog);
		if (DEBUG) System.out.println ("NFCScript Result is " + ret.toString());
	}
	
	protected void addResourceFields(Class<?> clazz, String name, Environment e) {
		Field[] fs = clazz.getFields();
		for (Field f : fs) {
			if (f.getType() == int.class) {
				try {
					e.define(("r-"+name+"-"+f.getName()).intern(), f.get(null));
				} catch (IllegalAccessException ex) {
				}
			}
		}
	}
	
	public void setSchemeCode(String code)
	{
		schemeCode = code;
	}
	
}

















