package project.safenfctaskapp.data;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class databaseClass {
	
	private Context mApp;
	private DatabaseHelper dbManager;
	private SQLiteDatabase db;
	
	public databaseClass(Context con) {
		mApp = con;
		dbManager = new DatabaseHelper(mApp);
	}
	
	public void open(boolean mode) {
		if (mode == true) db = dbManager.getReadableDatabase();
		else db = dbManager.getWritableDatabase();
	}
	
	public Cursor read(String sql) {
		Cursor c = db.rawQuery(sql, null);
		c.moveToFirst();
		return c;
	}
	
	public void write(String sql) {
		db.execSQL(sql);
	}
	
	public void close() {
		db.close();
	}
}
