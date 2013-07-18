package project.safenfctaskapp;

import project.safenfctaskapp.data.databaseClass;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;

public class makeTag extends Activity implements OnClickListener{

	
	private String contents;
	private databaseClass db;
	private EditText tagName;
	private EditText tagContents;
	public static final String NAMING = "name";
	private String comment[]= {"//스킴코드를 작성합니다(인터넷접속 예시).\n","//클래스를 정의\n","//엑티비티를 실행\n"};
	private int nameIndex;
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
	
	    // TODO Auto-generated method stub
	    
	    requestWindowFeature(Window.FEATURE_NO_TITLE);
		
		setContentView(R.layout.maketag);
		
		SharedPreferences prefs = getSharedPreferences("safeNFCs", MODE_PRIVATE); 
		
		nameIndex = prefs.getInt(NAMING, 0);
		db = new databaseClass(this);
		
		tagName = (EditText)findViewById(R.id.tagname);
		tagContents = (EditText)findViewById(R.id.tagcontents);
		tagName.setText("태그-"+nameIndex);
		tagContents.setText(comment[0] + "(define url (android.net.Uri.parse \"http://www.naver.com\"))\n" +
				"(define web_view (android.content.Intent. (android.content.Intent.ACTION_VIEW$) url))\n" +
				comment[1] + "(.startActivity context web_view))" + 
				comment[2]);

		Button back = (Button)findViewById(R.id.back3);
		Button done = (Button)findViewById(R.id.done2);
		back.setOnClickListener(this);
		done.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch(v.getId()) {
		case R.id.back3:
			makeTag.this.finish();
			break;
		case R.id.done2:
			contents = tagContents.getText().toString();
			contents = contents.replace(comment[0], "");
			contents = contents.replace(comment[1], "");
			contents = contents.replace(comment[2], "");
			
			db.open(false);
			
			db.write("INSERT INTO customtag values(null,'"+tagName.getText().toString()+"','"+contents+"')");
			nameIndex++;
			Intent writendef = new Intent(makeTag.this, ndefWrite.class);
			writendef.putExtra("message", contents);
			startActivity(writendef);
			makeTag.this.finish();
			db.close();
			break;
		}
	}
	
	@Override
	public void onStop() {
		super.onStop();
			SharedPreferences prefs = getSharedPreferences("safeNFCs", MODE_PRIVATE); 

			SharedPreferences.Editor editor = prefs.edit(); 

			editor.putInt(NAMING, nameIndex); 

			editor.commit(); 

	}

}
