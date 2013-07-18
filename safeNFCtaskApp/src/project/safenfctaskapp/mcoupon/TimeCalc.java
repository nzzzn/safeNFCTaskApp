package project.safenfctaskapp.mcoupon;

public class TimeCalc {
	private long startTime;
	private long endTime;
	private long lTime;
	
	public void startTime()
	{
		startTime = System.currentTimeMillis();
	}
	
	public void endTime()
	{
		endTime = System.currentTimeMillis(); 
		lTime = endTime - startTime;
	}
	
	public long getProcessTime()
	{
		return lTime;
	}
	
}