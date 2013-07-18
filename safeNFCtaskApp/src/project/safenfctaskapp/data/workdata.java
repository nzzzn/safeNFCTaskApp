/*
 * projcet.safenfctaskapp.data 어플리케이션 작업에 사용될 데이터에 관한 인터페이스를 모아둔 패키지입니다.
 * workdata.java ndef메세지들로 구성되는 하나의 작업 class 정의
 */
package project.safenfctaskapp.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

public class workdata {
	
	private String name;//이름
	private String Ndeflist;//컨텐츠
	private int size;//컨텐츠 사이즈
	
	public workdata() {
		name = null;
		Ndeflist = "";
		size = 0;
	}
	
	public workdata(String a, String b, int i) {
		name = a;
		Ndeflist = b;
		size = i;
	}
	
	public workdata(String a, int i) {
		name = a;
		size = i;
	}
	
	public void setName (String s) {
		name = s;
	}
	
	public String getName () {
		return name;
	}
	
	public String getNdef () {
		return Ndeflist;
	}
	
	public int size() {
		return size;
	}
	
	//객체 초기화
	public void clear() {
		name = null;
		Ndeflist = "";
		size = 0;
	}
	
	
	//workdata 객체를 데이타베이스에 저장
	public void saveDb(Context context) {
		SQLiteDatabase db;
		DatabaseHelper dbHelper = new DatabaseHelper(context);
		
		db = dbHelper.getWritableDatabase();
		db.execSQL("INSERT INTO worklist (_id, name, contents) VALUES(null,'"
						+name+"','"+Ndeflist+"','"+ size + "');");
		db.close();
	}
}
