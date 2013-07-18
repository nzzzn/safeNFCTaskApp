package project.safenfctaskapp.adapter;

import java.util.ArrayList;

import project.safenfctaskapp.R;
import project.safenfctaskapp.data.classes;

import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

public class setAdapter extends ArrayAdapter<classes> {
	
	private Context app;
	private ArrayList<classes> mList;
	private LayoutInflater li;

	public setAdapter(Context context, ArrayList<classes> list) {
		super(context,R.layout.classlist, list);
		// TODO Auto-generated constructor stub
		app = context;
		mList = list;
		
		li = (LayoutInflater)app.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}
	
	private class viewHolder {//리스트에 보여질 레이아웃에 대한 여러 뷰들을 홀드 해주는 작업
		LinearLayout lo;
		TextView name;
		CheckBox check;
	}

	
	//listview의 view에 보여질 데이터를 할당
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		viewHolder holder;
		classes cl = mList.get(position);
		
		if(convertView != null) {
			holder = (viewHolder)convertView.getTag();
		} else {//뷰들이 비어있을 때
			holder = new viewHolder();
			
			convertView = li.inflate(R.layout.classlist, null);
			
			convertView.setLayoutParams(new ListView.LayoutParams(getCellWidthDP()
					,getCellHeightDP()));

			//각각의 뷰에 tag를 걸어줌
			holder.name=(TextView)convertView.findViewById(R.id.classname);
			holder.check = (CheckBox)convertView.findViewById(R.id.classcheck);
			holder.lo = (LinearLayout)convertView.findViewById(R.id.classBackground);
			convertView.setTag(holder);
		}
		
		try {
			holder.name.setText(cl.getName());
			holder.check.setChecked(cl.isChecked());
			holder.check.setClickable(false);
			holder.lo.setBackgroundColor(Color.parseColor(cl.getColor()));
		} catch(NullPointerException e) {
			e.printStackTrace();
			Log.i("setAdapter",e.toString());
		}
		return convertView;
	}
	
	public void setLayoutColor(String a) {
		viewHolder holder = new viewHolder();
		holder.lo.setBackgroundColor(Color.parseColor(a));
	}
	
	private int getCellWidthDP(){//뷰들의 크기를 정함
		int cellWidth = app.getResources().getDisplayMetrics().widthPixels;
		return cellWidth;
	}
	
	private int getCellHeightDP(){
		int cellHeight = app.getResources().getDisplayMetrics().heightPixels / 12;
		return cellHeight;
	}

}
