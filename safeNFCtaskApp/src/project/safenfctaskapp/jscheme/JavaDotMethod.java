package project.safenfctaskapp.jscheme;


import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Vector;
import android.annotation.SuppressLint;
import android.util.Log;

@SuppressLint("UseValueOf")
public class JavaDotMethod extends Procedure {
	private boolean DEBUG = false;
	
	static Vector<String> imports = new Vector<String>();

	static {
		imports.add("");
		imports.add("java.lang.");
	}

	public static void addImport(String pkg) {
		imports.add(pkg+".");
	}
	
	String method;

	public JavaDotMethod(String method) {
		this.method = method;
	}

	protected boolean isMatch(Class<?>[] types, Object[] objs) {
		
		if (DEBUG)
			System.out.println ("isMatch: " + types.length + " =?= " + objs.length);
		
		if (types.length != objs.length) {
			return false;
		}

		for (int i = 0; i < objs.length; i++) {
			
			if (DEBUG)
			{
				System.out.println ("isMatch: types[" + i + "]" + types[i].toString());
			
				if(i == 0)
				{
					System.out.println ("System out println");
				}
				
				if (DEBUG)
					System.out.println ("isMatch: objs[" + i + "].getClass"
										+ objs[i].getClass().toString() + " ----- " + method);
			}
			
			if (types[i].isAssignableFrom(objs[i].getClass())) {
				continue;
			} else if (types[i].isAssignableFrom(String.class)  // This is where a Scheme string in char[] is replaced with a Java string.
					&& objs[i].getClass() == char[].class) {
				continue;
			} else if (types[i].isAssignableFrom(int.class)
					&& objs[i].getClass() == Double.class) {
				continue;
			} else if (types[i].isAssignableFrom(float.class)
					&& objs[i].getClass() == Double.class) {
				continue;
			} else if (types[i].isAssignableFrom(double.class)
					&& objs[i].getClass() == Double.class) {
				continue;
			} else if (types[i] == int.class 
					&& objs[i].getClass() == Integer.class) {
				continue;
			} else if (types[i].isAssignableFrom(boolean.class)				//boolean type 추가.
					&& objs[i].getClass() == Boolean.class)
			{
				continue;
			} else if (types[i].isAssignableFrom(byte.class)				//byte type 추가.
					&& objs[i].getClass() == Double.class)
			{
				continue;
			}
			//throw new RuntimeException("Bailing on: "+types[i]+"<->"+objs[i].getClass());
			return false;
		}
		return true;
	}

	@SuppressLint("UseValueOf")
	protected Object[] coerce(Class<?>[] types, Object[] objs) {
		Object[] res = new Object[objs.length];
		for (int i = 0; i < res.length; i++) {
			if (types[i].isAssignableFrom(objs[i].getClass())) {
				res[i] = objs[i];
			} else if (types[i].isAssignableFrom(String.class)  // This is where a Scheme string in char[] is replaced with a Java string.
					&& objs[i].getClass() == char[].class) {
				res[i] = new String((char[]) objs[i]);
			} else if (types[i].isAssignableFrom(int.class)
					&& objs[i].getClass() == Double.class) {
				res[i] = new Integer(((Double) objs[i]).intValue());
			} else if (types[i].isAssignableFrom(float.class)
					&& objs[i].getClass() == Double.class) {
				res[i] = new Float(((Float) objs[i]).floatValue());
			} else if (types[i].isAssignableFrom(double.class)
					&& objs[i].getClass() == Double.class) {
				res[i] = (Double) objs[i];
			} else if (types[i] == int.class &&
					   objs[i].getClass() == Integer.class) {
				res[i] = objs[i];
			} else if (types[i] == boolean.class					//boolean type 추가
					&& objs[i].getClass() == Boolean.class) {
				res[i] = (Boolean)objs[i];
			} else if (types[i] == byte.class					    //byte type 추가
					&& objs[i].getClass() == Double.class) {
				res[i] = new Byte(((Double)objs[i]).byteValue());
			}
			else {
				throw new RuntimeException("Bailing on: "+types[i]+"<a>"+objs[i].getClass());
			}
		}
		return res;
	}

	protected Object[] getArgs(Object args) {
		int n = length(args);
		Object[] as = new Object[n];
		for (int i = 0; i < n; i++) {
			as[i] = first(args);
			args = rest(args);
		}
		return as;
	}

	protected Object doConstructor(Scheme interpreter, Object args)
			throws ClassNotFoundException, InstantiationException,
			InvocationTargetException, IllegalAccessException {
		Object[] as = getArgs(args);
		String className = method.substring(0, method.length() - 1);
		
		if (DEBUG) System.out.println ("doConstructor: className " + className);
		
		Class<?> clazz = forName(className);
		Constructor<?>[] cs = clazz.getConstructors();
		
		if (DEBUG) System.out.println ("doConstructor: cs.length " + cs.length);
		
		for (int i = 0; i < cs.length; i++) {
			if (isMatch(cs[i].getParameterTypes(), as)) {
				
				// Access check
				interpreter.checkAccess( cs[i], coerce(cs[i].getParameterTypes(), as)); 
				
				// Interfaces to Java
				//디버깅 포인트 출력 다 해보고 스킴이랑 원래 자바코드랑 비교해보자.
				return cs[i].newInstance(coerce(cs[i].getParameterTypes(), as)); 
			}
		}
		error("Couldn't find matching constructor.");
		return null;
	}

	protected Object doMethod(Scheme interpreter, Object target, Object args)
			throws InvocationTargetException, IllegalAccessException {
		String name = method.substring(1);
		Class<?> clazz = target.getClass();

		return doMethod(interpreter, name, target, clazz, args);
	}

	protected Object doMethod(Scheme interpreter, String name, Object target,
			Class<?> clazz, Object args) throws InvocationTargetException,
			IllegalAccessException {
		Object[] as = getArgs(args);
		Method[] ms = clazz.getMethods();
			
		//디버깅 하는 하는 포인트는 이런 곳이다.
		//실제로 어떻게 출력되는지 정확하게 다 체크를 해봐야 한다.
		//이제 자바 코드가 어떻게 되는지 확인해보자.
		//Log.i("method target class", target.getClass().toString());
		//명필 수정 2013-04-30 static class를 사용하는 경우 target class = null 그냥 출력시 error 발생 


		
		
		if(DEBUG && target != null)
		{
			Log.i("method target class", target.getClass().getCanonicalName());
			if(target.getClass().getCanonicalName().equals("java.lang.String"))
			{
				Log.i("doMethod canonicalName : ", target.getClass().getCanonicalName());
			}
		}

		for (int i = 0; i < ms.length; i++) {
			
			if (DEBUG) {
				System.out.println("Method Name: " + ms[i].getName());
			
				for (@SuppressWarnings("rawtypes") Class c : ms[i].getParameterTypes())
					System.out.println("Parameter Type: " + c.toString());
			}
			
			if (ms[i].getName().equals(name)
					&& isMatch(ms[i].getParameterTypes(), as)) {
				
				// Access check
				interpreter.checkAccess(ms[i], target, coerce(ms[i].getParameterTypes(), as)); 
				
				// Interfaces to Java				
				return ms[i].invoke(target, coerce(ms[i].getParameterTypes(), as));
			}
		}
		
		error("Couldn't find matching method: " + name);
		return null;
	}

	protected Object doStaticMethod(Scheme interpreter, Object args)
			throws InvocationTargetException, IllegalAccessException,
			ClassNotFoundException {
		int ix = method.lastIndexOf(".");
		String className = method.substring(0, ix);
		String methodName = method.substring(ix + 1);

		return doMethod(interpreter, methodName, null, forName(className), args);
	}

	protected Object doField(Scheme interpreter, String name, Class<?> clazz,
			Object target, Object args) throws IllegalAccessException {
		Field[] fs = clazz.getFields();		
		for (int i = 0; i < fs.length; i++) {
			if (fs[i].getName().equals(name)) {
				
				// Access check
				interpreter.checkAccess( fs[i], target ); 
				
				if(first(args) != null)
				{
					if(first(args) instanceof char[])
					{
						fs[i].set(target, new String((char[]) first(args)));
						if(DEBUG) Log.i("doField First : ", new String((char[]) first(args)));
					}
					else if(first(args) instanceof Boolean)
					{
						fs[i].setBoolean(target, ((Boolean)first(args)).booleanValue());
					}
					else if(first(args) instanceof Double)
					{
						fs[i].setInt(target, (int)((Double)first(args)).doubleValue());
					}
					else
					{
						
						fs[i].set(target, first(args));
					}
					
				}
				
				// Interfaces to Java
				//디버깅
				//세팅하는 값이랑 타입 이런것 다 출력해보자
				return fs[i].get(target);                                    
			}
		}
		error("No such field: " + method);
		return null;
	}

	protected Class<?> forName(String nme) 
		throws ClassNotFoundException
	{
		String name;
		for (String imprt : imports)
		{
			if (DEBUG) System.out.println ("forName: " + imprt);
			if (DEBUG) System.out.println ("       : " + nme);
			
			name = imprt + nme;
			
			if (DEBUG) System.out.println ("Class.forName : " + name);
			try {
				if (name.indexOf('$') == -1) {
					return Class.forName(name);
				}
				else {
					int ix = name.indexOf('$');
					String className = name.substring(0, ix);
					
					Log.i("JavaDotMethod", "className: " + className);
					
					Class<?> clazz = Class.forName(className);
					Class<?> res = forName(clazz, name);
					
					if (res == null) {
						throw new ClassNotFoundException(name);
					}
					else {
						return res;
					}
				}
			}
			catch (ClassNotFoundException ex) {}
		}
		throw new ClassNotFoundException(nme);
	}

	protected Class<?> forName(Class<?> parent, String name) {
		
		Log.i("JavaDotMethod", "forName name : " + name);
		Log.i("JavaDotMethod", "forName parent : " + parent.toString());
		
		Class<?>[] cs = parent.getClasses();
		if (name.indexOf('$') == -1) {
			
			
			return parent;
			
			/*		//명필 2013 04 08 잘못된 것 같음. inner class 한단계만 들어갈 수 있음 여러 단계 못들어감.
			for (int i = 0; i < cs.length; i++) {
				if (cs[i].getName().equals(name)) {
					return cs[i];
				}
			}
			*/
		} else {
			int ix = name.indexOf('$');
			@SuppressWarnings("unused")
			String className = name.substring(0, ix);
			String internalName = name.substring(ix + 1);
			
			Log.i("JavaDotMethod", "forName className : " + name.substring(0, ix));
			Log.i("JavaDotMethod", "forName internalName: " + name.substring(ix + 1));
			
			
			for (int i = 0; i < cs.length; i++) {
				Log.i("JavaDotMethod", "forName getName: " + cs[i].getName().toString());
				
				String lastClassName = cs[i].getName();
				int lastix = lastClassName.lastIndexOf('$');
				lastClassName = lastClassName.substring(lastix + 1);
				
				String firstClassName = internalName;
				int firstix = firstClassName.indexOf('$');
				firstClassName = firstix == -1 ? firstClassName : firstClassName.substring(0, firstix);
				
				Log.i("JavaDotMethod", "forName lastClassName = " + lastClassName);
				Log.i("JavaDotMethod", "forName firstClassName = " + firstClassName);
				
				if (lastClassName.equals(firstClassName)) {
					return forName(cs[i], internalName);
				}

				
				/*		//명필 2013 04 08 잘못된 것 같음. inner class 한단계만 들어갈 수 있음 여러 단계 못들어감.
				 * 	if (cs[i].getName().equals(className)) {
					return forName(cs[i], internalName);
				    }
				 * 
				if (cs[i].getName().equals(name)) {
					return forName(cs[i], internalName);
				}
				*/
			}
		}
		Log.i("JavaDotMethod", "forName return null error");
		
		return null;
	}

	protected Object applyInternal(Scheme interpreter, Object args) {
		try {
			if (method.equals("import"))
			{
				addImport(new String((char[])first(args)));
				return Scheme.TRUE;
			}
			else if (method.endsWith(".")) {
				return doConstructor(interpreter, args);
			} else if (method.startsWith(".")) {
				Object target = first(args);
				args = rest(args);
				if (method.endsWith("$")) {
					String name = method.substring(1, method.length() - 1);
					return doField(interpreter, name, target.getClass(),
							target, args);
				} else {
					return doMethod(interpreter, target, args);
				}
			} else if (method.endsWith("$")) {
				int ix = method.lastIndexOf('.');
				String className = method.substring(0, ix);
				String fieldName = method
						.substring(ix + 1, method.length() - 1);
				return doField(interpreter, fieldName, forName(className),
						null, args);
			} else if (method.endsWith(".class")) {
				String className = method.substring(0, method.length() - 6);
				return forName(className);
			} else if (method.indexOf(".") != -1) {
				return doStaticMethod(interpreter, args);
			} else {
				error("Unknown: " + method);
			}
		}
		catch (InvocationTargetException ex) {
			error("Exception: "+ex.getTargetException());
		} 
		catch (Exception ex) {
			error("Exception: " + ex);
		}
		return null;
	}

	protected Object interpret(Object o) 
	{
		if (o instanceof String) {
			String s = (String)o;
			char[] res = new char[s.length()];
			s.getChars(0, res.length, res, 0);
			return res;
		}
		else if (o instanceof Number) {
			return num(o);		
		}
		else {
			return o;
		}
	}
	
	@Override
	public Object apply(Scheme interpreter, Object args) {
		Object res = applyInternal(interpreter, args);
		if (res != null) {
			res = interpret(res);
		}
		return res;
	}
}
