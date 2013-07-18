/*
 * project.safenfctaskapp.adapter 어플리케이션에서 사용할 리스트의 어뎁터를 모아둔 패키지입니다.
 * ndefList.java ndef메세지들응 하나의 작업으로 설정 시 생성되는 리스트의 어뎁터 정의
*/
package project.safenfctaskapp.adapter;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import project.safenfctaskapp.R;
import project.safenfctaskapp.data.*;

public class ndefList extends ArrayAdapter<ndefdata>{
	
	public ArrayList<ndefdata> list;
	public Context mActivity;
	private LayoutInflater inf = null;

	public ndefList(Activity context, ArrayList<ndefdata> objects) {
		super(context, R.layout.taglist, objects);

		mActivity = context;//list가 보여질 Activity에 대한 context
		list = objects;//리스트에 보여질 ArrayList
		inf = (LayoutInflater)mActivity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		//Activity에 대한 인필터
	}
	
	private class viewHolder {//리스트에 보여질 레이아웃에 대한 여러 뷰들을 홀드 해주는 작업
		TextView ndef;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		viewHolder holder = new viewHolder();
		ndefdata data = list.get(position);
		Log.i("ndef", position+
				" "+data.getName()+
				" "+data.getContent());
		
		if(convertView != null) {
			holder = (viewHolder)convertView.getTag();
		} else {//뷰들이 비어있을 때
			convertView = inf.inflate(R.layout.taglist, null);
			
			convertView.setLayoutParams(new ListView.LayoutParams(getCellWidthDP()
												,getCellHeightDP()));
			
			holder.ndef=(TextView)convertView.findViewById(R.id.ndef);
			convertView.setTag(holder);
		}
		
		holder.ndef.setText(data.getName());

		return convertView;
	}
	
	private int getCellWidthDP(){//뷰들의 크기를 정함
		int cellWidth = mActivity.getResources().getDisplayMetrics().widthPixels;
		return cellWidth;
	}
	
	private int getCellHeightDP(){
		int cellHeight = mActivity.getResources().getDisplayMetrics().heightPixels / 12;
		return cellHeight;
	}
	
	public ArrayList<ndefdata> getList() {
		return list;
	}
}
