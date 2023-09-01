package com.cst438;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class InsulinPump {
	private static final int HI_LEVEL = 130;
	private static final int LO_LEVEL = 70;
	private static final int CRITICAL_HI = 220;
	private static final int MAX_LOG_SIZE = 2000;
	
	private static final SimpleDateFormat sdf = new SimpleDateFormat("MM-dd HH:mm");
	
	private ArrayList<String> log = new ArrayList<>();
	private DeviceReader rdr;
	private DeviceOutput out;
	
	public InsulinPump(DeviceReader rdr, DeviceOutput out) {
		this.rdr = rdr;
		this.out = out;		
	}	
	public synchronized void check(long timeMillis) {
		String str_time = sdf.format(new java.util.Date(timeMillis));
		int glucose = rdr.getGlucoseLevel();
		if (glucose < LO_LEVEL)
		{
			if (log.size()<MAX_LOG_SIZE) {
				log.add(str_time + ": Low Reading | " + glucose + " | Increase blood sugar immediately! | Alarm 2 activated");
			}
			out.alarm2(); //If glucose is low turn on lo alarm
		}
		else if (glucose >= LO_LEVEL && glucose < HI_LEVEL)
		{
			if (log.size()<MAX_LOG_SIZE) {
				log.add(str_time + ": Normal Reading | " + glucose);
			}
		}
		else if (glucose >= HI_LEVEL && glucose < CRITICAL_HI)
		{
			if (log.size()<MAX_LOG_SIZE) {
				log.add(str_time + ": Hi Reading | " + glucose + " | Pumping 1 unit of insulin");
			}
			out.pumpOneUnit(); // if high pump 1 unit
		}
		else if (glucose >= CRITICAL_HI)
		{
			if (log.size()<MAX_LOG_SIZE) {
				log.add(str_time + ": Critical Hi Reading! | " + glucose + " | Pumping 1 unit of insulin | Alarm 1 activated");
			}
			out.pumpOneUnit(); //Pump 1 unit and sound alarm
			out.alarm1();
		}
		
	}
	
	public synchronized String[] getLog(int skip, int limit) {
		//Object s[] = new Object[limit];
		ArrayList<String> temp = new ArrayList<String>();
		temp.addAll(skip, log);
		String[] s = temp.toArray(new String[limit]);
		return s;
	}
}
