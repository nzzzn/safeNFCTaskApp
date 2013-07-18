/*
 * projcet.safenfctaskapp.data 어플리케이션 작업에 사용될 데이터에 관한 인터페이스를 모아둔 패키지입니다.
 * DatabaseHelper.java 데이터베이스 설정과 입출력을 도와줄 class 정의
 */
package project.safenfctaskapp.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DatabaseHelper extends SQLiteOpenHelper
{
	private static final String ndef="DataBaseAdapter";
	private static final String DATABASE_NAME="snta.db";
	private static final int DATABASE_VERSION = 1;
	
	public DatabaseHelper(Context context)//어플리케이션의 db 생성
	{
		super(context,DATABASE_NAME,null,DATABASE_VERSION);
	}
	
	public void onCreate(SQLiteDatabase db)
	{
		//테이블 생성
		db.execSQL("create table classlist(_id INTEGER PRIMARY KEY AUTOINCREMENT, "
				+ "classname TEXT, usable INTEGER, askable INTEGER);");
		
		db.execSQL("create table customtag(_id INTEGER PRIMARY KEY AUTOINCREMENT, "
				+ "tagname TEXT, tagcontents TEXT);");
		
		Log.i(ndef, "create database");
	}
	
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)//db 업그레이드
	{
		Log.v(ndef,"Upgrading database from version"+oldVersion+"to"+newVersion+"," +
										"which will destroy all old data");
		
		db.execSQL("DROP TABLE IF EXISTS worklist");

		onCreate(db);
	}
	
	public void getqurey(String query, int tablenum)//퀘리를 받음
	{
		
	}
}