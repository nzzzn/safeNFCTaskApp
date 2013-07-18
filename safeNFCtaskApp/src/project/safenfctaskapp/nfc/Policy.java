package project.safenfctaskapp.nfc;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.HashSet;

import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import project.safenfctaskapp.jscheme.AccessControlException;
import project.safenfctaskapp.jscheme.Hook;
import project.safenfctaskapp.jscheme.Scheme;
import project.safenfctaskapp.jscheme.SchemeUtils;

/**
 * This inteface represents a hook interface to calling Java constructors,
 * methods, and accessing fields from elsewhere.
 *      
 * @author Kwanghoon Choi, kwanghoon.choi@yonsei.ac.kr Copyright 2011
 *           
 **/

class Policy implements Hook {
	static private boolean DEBUG = false;
	Handler dialogHandler;
	
	public Policy(Handler h)
	{
		dialogHandler = h;
	}
	
	public static String session = "ANDROID_IMEI356723040343300";
	public static String operation = "use";
	
	// User-defined Policy
	private String defaultUserPolicy = 
			"(define (true3 x y z) #t)\n" +
			"(define (true2 a d) #t)\n" +
			"(define policy (cons true3 (cons true3 (cons true3 (cons true2 ())))))\n" +
			"\n";  
	private String userPolicy = defaultUserPolicy;
	
	public void setUserPolicy(String up) {
		userPolicy = up;
	}
	
	public String getUserPolicy() { return userPolicy; }
	
	public void resetUserPolicy() { setUserPolicy(defaultUserPolicy); }
	// For Cache
	private String cacheLib = "(define cache ())\n" +
			"\n" +
			"(define (addToACList pkgname)\n" +
			"   (set! cache (addToACList_ cache pkgname)))\n" +
			"\n" +
			"(define (addToACList_ cache pkgname)\n" +
			"   (cond ((null? cache)\n" +
			"            (cons\n" +													//cons haskell에서 : []는 nil
			"               pkgname cache))\n" +
			"         (else\n" +
			"            (letrec\n" +
			"                 ((h  (car cache))\n" +
			"                  (h1 (car h))\n" +
			"                  (t  (cdr cache)))\n" +
			"                 (cond ((equal? h1 pkgname) (cons h t))\n" +
			"                       (else (cons h (addToACList_ t pkgname))))))))\n" +
			"                       \n" +
			"(define (Use pcs)\n" +
			"   (cond ((null? pcs) (edu.yonsei.nfc.Policy.checkCache cache))\n" +
			"         (else \n" +
			"            (letrec\n" +												//let 
			"                 ((h (car pcs))\n" +									//car head
			"                  (t (cdr pcs))\n" +									//cdr tail
			"                  (a (addToACList h))\n" +
			"                  (b (Use t)))\n" +
			"                 ()))))\n" +
			"\n";
	
	public String getCacheLib() { return cacheLib; }
	
	// Supervisor Mode:
	// The Scheme interpreter now runs the user-defined policies if supervisormode is true. 
	// Otherwise, it runs a normal code.
	private boolean supervisormode;
	
	public Policy() {
		supervisormode = false;
	}
	
	private Scheme s;
	public void setScheme(Scheme s) {
		this.s = s;
	}
	
	public void setSupervisorMode (boolean b) {
		supervisormode = b;
	}
	
	// Access Control
	public void checkAccess (Constructor<?> cs, Object[] args) throws AccessControlException {
		
		String packageName = new String();
		String className = new String();
		String conName = new String();
		
		if (DEBUG) System.out.print ("checkAccess [Constructor]: ");
		
		if (cs == null) {
			if (DEBUG) System.out.print("null");
		}
		else {
			packageName = Util.getPackageName (cs.getDeclaringClass().toString());
			className   = Util.getClassName (cs.getDeclaringClass().toString());
			conName     = cs.getName();
			
			if (DEBUG) System.out.print (cs.getDeclaringClass().toString());
			if (DEBUG) System.out.print("[" + packageName + "]");
			if (DEBUG) System.out.print("[" + className + "]");
			if (DEBUG) System.out.print ( " " );
			if (DEBUG) System.out.print ( conName );
		}
		
		if (args == null) {
			if (DEBUG) System.out.print(" with " + "null" + " arguments.");
		}
		else {
			if (DEBUG) System.out.print(" with " + args.length + " arguments.");
		}
		
		for (int i = 0; i < args.length; i++) {
			if (args[i] == null) {
				if (DEBUG) System.out.println ("\t[" + i + "] " + "null" + " ");
			}
			else {
				if (DEBUG) System.out.println ("\t[" + i + "] " + args[i].toString() + " ");
			}
		}
		
		if (supervisormode == false) {
			boolean accessible = false;
		
			boolean flag = s.checkCache(packageName, className);
			if(DEBUG) System.out.println("checkAccess: Constructor checkCache=" + flag);
			
			// POLICY: the package name must be available for ANDROID_IMEI356723040343300 
			if ( flag || checkAccessControl (session, operation, packageName) == 1 ) {
				s.checkConstructor(packageName, className, conName);
				accessible = true;
			}
		
			if (DEBUG) System.out.println ("accessible[constructor]: " + accessible);
		
			if ( accessible == false )
				throw new AccessControlException
							("Access Check Error[Constructor]: " + cs.toString());
		}
	}
	
	public void checkAccess (Field field, Object callingobj) throws AccessControlException {
		String packageName = new String();
		String className = new String();
		String fieldName = new String();
		
		if (DEBUG) System.out.print ("checkAccess [Field]: ");
		if (field == null) {
			if (DEBUG) System.out.print ("null" + " with ");
		}
		else {
			packageName = Util.getPackageName (field.getDeclaringClass().toString());
			className = Util.getClassName (field.getDeclaringClass().toString());
			fieldName = field.getName();
					
			if (DEBUG) System.out.print (field.getDeclaringClass().toString() + " with ");
			if (DEBUG) System.out.print("[" + packageName + "]");
			if (DEBUG) System.out.print("[" + className + "]");
			if (DEBUG) System.out.print (fieldName + " in ");
		}
			
		if (callingobj == null) {
			if (DEBUG) System.out.println ("null");
		}
		else {
			if (DEBUG) System.out.println (callingobj.toString());
		}
		
		
		if (supervisormode == false) {
			boolean accessible = false;
		
			
			boolean flag = s.checkCache(packageName, className);
			if (DEBUG) System.out.println("checkAccess: Field" + flag);
			
			// POLICY: the package name must be available for ANDROID_IMEI356723040343300 
			if ( flag || checkAccessControl (session, operation, packageName) == 1 ) {
				s.checkField(packageName, className, fieldName);
				accessible = true;
			}
		
			if (DEBUG) System.out.println ("accessible[field]: " + accessible);
		
			if ( accessible == false )
				throw new AccessControlException
								("Access Check Error[Filed]: " + field.toString());
		}
	}
	
	public void checkAccess (Method method, Object callingobj, Object[] args) throws AccessControlException {
		String packageName = new String();
		String className = new String();
		String methodName = new String();
		String action = new String();
		String data = new String();
		
		if (DEBUG) System.out.print ("checkAccess [Method]: ");
		if (method == null) {
			if (DEBUG) System.out.print ( "null" );
		}
		else {
			packageName = Util.getPackageName (method.getDeclaringClass().toString());
			className = Util.getClassName (method.getDeclaringClass().toString());
			methodName = method.getName();
			
			if (DEBUG) System.out.print ( method.getDeclaringClass().toString() );
			if (DEBUG) System.out.print("[" + packageName + "]");
			if (DEBUG) System.out.print("[" + className + "]");
			if (DEBUG) System.out.print ( " " );
			if (DEBUG) System.out.print ( methodName );
		}
		if (callingobj == null) {
			if (DEBUG) System.out.print(" with " + "null");
		}
		else {
			if (DEBUG) System.out.print(" with " + callingobj.toString());
		}
			
		if (args == null) {
			args = new Object[0];
			if (DEBUG) System.out.println(" and " + "null" + " arguments");
		}
		else {
			if (DEBUG) System.out.println(" and " + args.length + " arguments");
		}
			
		for (int i = 0; args != null && i < args.length; i++) {
			if (args[i] == null) {
				if (DEBUG) System.out.println ("\t" + "null" + " ");
			}
			else {
				if (DEBUG) System.out.println ("\t" + args[i].toString() + " ");
			}
		}
		
		if (supervisormode == false) {
			boolean accessible = false;
		
			boolean flag = s.checkCache(packageName, className);
			if (DEBUG) System.out.println("checkAccess: Method" + flag);
			
			// POLICY: the package name must be available for ANDROID_IMEI356723040343300
			if (packageName.equals("edu.yonsei.nfc")) {
				if (DEBUG) System.out.println ("The trusted package, edu.yonsei.nfc");
				accessible = true;
			}
			else if ( flag || checkAccessControl (session, operation, packageName) == 1 ) {
			
				// Is calling the method startActivity of android.app.Activity.startActivity
				// with exactly one Intent argument?
				if (DEBUG) System.out.println (packageName + " " + packageName.equals("android.app"));
				if (DEBUG) System.out.println (className + " " + className.equals("Activity"));
				if (DEBUG) System.out.println (methodName + " " + methodName.equals("startActivity"));
				if (DEBUG) System.out.println (args.length);
			
				if (packageName.equals("android.app") && className.equals ("Activity")
						&& methodName.equals("startActivity") && args.length == 1) {
				
					if (DEBUG) System.out.println ("args[0] is instanceof Intent: " + (args[0] instanceof Intent));
				
					if (args[0] instanceof Intent) {
						Intent intent = (Intent)args[0];
						action = intent.getAction();
						data = intent.getDataString();
					
						boolean flagAction = s.checkCache(packageName, action);
						if (DEBUG) System.out.println("checkAccess: Action" + flagAction);
						
						// If we are calling the method, we do check if the action is allowed or not.
						if ( flagAction || checkAccessControl (session, operation, action) == 1 ) {
							s.checkAction(action, data);
							accessible = true;
						}
					}
				
				}
				// If we are calling a method other than android.app.Activity.startActivity,
				// it is accessible.
				else {
					s.checkMethod (packageName, className, methodName);
					accessible = true;
				}
			}
		
			if (DEBUG) System.out.println ("accessible[method]: " + accessible);
		
			if ( accessible == false )
				throw new AccessControlException
								("Access Check Error[Filed]: " + method.toString());
		}
	}
	
	public int checkAccessControl (String session, String operation, String resource) {
		String result = "";
		if (DEBUG)
		{
			Log.i("NFCScript", "[checkAccess] Server Call BEGINS.");
			System.out.println ("checkAccess: session=" + session + ", resource=" + resource + ", operation=" + operation);
		}
		
		
		//Dialog 띠워야 할 곳
		ModalMessageThread msgThread = new ModalMessageThread();
		msgThread.start();
		
		StringHandlerPair shp = new StringHandlerPair();
		shp.packageName = resource;
		shp.handler =  msgThread.getHandler();
		
		Message msg = new Message();
		msg.what = 0;
		msg.obj = shp;		//class 에 멤버가 핸들러, 스트링 그것을 obj에 쓴다.
		
		dialogHandler.sendMessage(msg);
		
		int retval = msgThread.getResult();
		if(DEBUG) Log.i("Policy retval : ", retval + "");
		
		/*
		HttpClient client = new DefaultHttpClient();
		HttpParams params = client.getParams();
		
		HttpConnectionParams.setConnectionTimeout ( params, 120000 );
		HttpConnectionParams.setSoTimeout ( params, 120000 );
		
		
		HttpPost request = new HttpPost ("http://192.168.0.15/~khchoi/openrbac/rbac/SOAP/clients/xacmlCheckAccess.php");
		Vector<NameValuePair> nameValue = new Vector<NameValuePair>();
		
		nameValue.add ( new BasicNameValuePair ("session", session));
		nameValue.add ( new BasicNameValuePair ("resource", resource));
		nameValue.add ( new BasicNameValuePair ("operation", operation)); //패키지 이름, 클래스 이름의 조합으로 되어있을 것임.
																			//키는 메서드 방식으로 우선은 생각하고 나중에 다시 생각.
		
		
		try {
			request.setEntity(new UrlEncodedFormEntity ( nameValue, "UTF-8" ) );
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		
		try {
			HttpResponse response = client.execute ( request );
			
			StatusLine status = response.getStatusLine();
			
			if (status.getStatusCode() != 200) { // HTTP_STATUS_OK
				throw new IOException ("Omvaod response from server: " + status.toString()); 
			}
			
			HttpEntity entity = response.getEntity();
			InputStream is = entity.getContent();
			BufferedReader reader = new BufferedReader ( new InputStreamReader (is) );
			StringBuilder sb = new StringBuilder ();
			String line = null;
			while ((line = reader.readLine()) != null) {
				sb.append ( line ).append("\n");
			}
			is.close();
			result = sb.toString();
		} catch (IOException e) {
			if (DEBUG) System.out.println ("checkAccess: error " + e.toString());
			result = "NotApplicable";
		} finally {
			client.getConnectionManager().shutdown();
		}
		*/
		
		
		if (DEBUG) System.out.println ("checkAccess: result = " + result);
		if (DEBUG) System.out.println ("checkAccess: retval = " + retval);
		
		
		if(DEBUG) Log.i("NFCScript", "[checkAccess] Server Call ENDS.");
		
		return retval;
	}
	
	// This method is called from Use declaration in a Scheme Script
	// Use declaration ->
	// 
	public boolean checkCache (Object list) {
		String ff;
		
		HashSet<String> set = new HashSet<String>();
			
		while (list != null) {
			Object f = SchemeUtils.first(list);
			
			if (f != null) {
				ff = new String ((char[])f);
				set.add (ff);					
			}
			
			list = SchemeUtils.rest(list);
		}
		
		StringBuilder sb = new StringBuilder();
		
		for (String s : set) {
			sb.append(s);
			sb.append(";");
		}
		
		if(DEBUG) Log.i("NFCScript", "checkCache: " + sb.toString());
		
		int ret = checkAccessControl(session, operation, sb.toString());
		
		if(DEBUG) Log.i("NFCScript", "ret= " + ret);
		
		return ret == 1;
	}

}