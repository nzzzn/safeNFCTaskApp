/*
 * project.safenfctaskapp.adapter 어플리케이션에서 사용할 리스트의 어뎁터를 모아둔 패키지입니다.
 * myWork.java ndef메세지들로 구성된 작업들을 띄워줄 리스트의 어뎁터 정의
*/
package project.safenfctaskapp.adapter;

import java.util.ArrayList;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.TextView;
import android.widget.Toast;
import project.safenfctaskapp.R;
import project.safenfctaskapp.data.*;

public class myWork extends ArrayAdapter<workdata> {
	
	private ArrayList<workdata> list;
	private Context mActivity;
	private LayoutInflater inf;

	public myWork(Context context, ArrayList<workdata> objects) {//어뎁터에 대한 생성자
		super(context, R.layout.worklist, objects);
		
		mActivity = context;//list가 보여질 Activity에 대한 context
		list = objects;//리스트에 보여질 ArrayList
		inf = (LayoutInflater)mActivity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		//Activity에 대한 인필터
	}
	
	private class viewHolder {//리스트에 보여질 레이아웃에 대한 여러 뷰들을 홀드 해주는 작업
		TextView name;
	}

	
	//listview의 view에 보여질 데이터를 할당
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		viewHolder holder;
		workdata data = list.get(position);
		
		if(convertView != null) {
			holder = (viewHolder)convertView.getTag();
		} else {//뷰들이 비어있을 때
			holder = new viewHolder();
			
			convertView = inf.inflate(R.layout.worklist, null);
			
			convertView.setLayoutParams(new GridView.LayoutParams(getCellWidthDP()
												,getCellHeightDP()));
			//각각의 뷰에 tag를 걸어줌
			holder.name=(TextView)convertView.findViewById(R.id.content);
			convertView.setTag(holder);
		}
		
		try {
			holder.name.setText(data.getName());
		} catch(NullPointerException e) {
			e.printStackTrace();
			Toast.makeText(mActivity,
					"NullPoninterException", 
					Toast.LENGTH_LONG).show();
		}
		return convertView;
	}
	
	private int getCellWidthDP(){//각 뷰들의 크기 설정
		int cellWidth = mActivity.getResources().getDisplayMetrics().widthPixels;
		return cellWidth;
	}
	
	private int getCellHeightDP(){
		int cellHeight = mActivity.getResources().getDisplayMetrics().heightPixels / 12;
		return cellHeight;
	}
}
