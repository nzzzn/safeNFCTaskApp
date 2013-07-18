package project.safenfctaskapp.data;

public class classes {
	private String name;
	private boolean check;
	private boolean color;
	
	public classes() {
		name = null;
		check = false;
	}
	
	public classes(String a) {
		name = a;
		check = false;
		color = true;
	}
	
	public classes(String a,boolean b) {
		name = a;
		check = b;
		color = true;
	}
	
	public classes(String a,boolean b,boolean c) {
		name = a;
		check = b;
		color = c;
	}
	
	public void setName(String a) {
		name = a;
	}
	
	public void setCheck(boolean a) {
		check = a;
	}
	
	public String getName() {
		return name;
	}
	
	public boolean isChecked() {
		return check;
	}
	public String getColor() {
		if (color == true)
			return "#00000000";
		else
			return "#44ff0000";
	}
}
