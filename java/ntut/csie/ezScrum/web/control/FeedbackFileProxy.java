package ntut.csie.ezScrum.web.control;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;

import edu.emory.mathcs.backport.java.util.concurrent.TimeUnit;

//Simplify file I/O
public class FeedbackFileProxy{
	
	File startRecord = null;
	
	
	String Date = "";
	
	String Canceled = "false";
	
	boolean timeExpires =false;
	
	JSONObject startProperties = new JSONObject();
	
	SimpleDateFormat formatter = new SimpleDateFormat("yyyy/MM/dd/ HH:mm:ss");
	
	
	public FeedbackFileProxy(String fileName) throws ParseException{
		
		if(Date.equals("") && !checkFileExist(fileName)){
			touchFeedbackFile(fileName);
		}
		
		if(Date.equals("") && checkFileExist(fileName)){
			readFromFile();
			calculateDay();
			
		}
		
	}

	private boolean checkFileExist(String fileName) {
		// TODO Auto-generated method stub
		startRecord = new File(fileName);
		return startRecord.exists();
	}

	private void touchFeedbackFile(String fileName) {
		startRecord = new File(fileName);
		FileWriter fileWriter = null;
		try {
			fileWriter = new FileWriter(startRecord);
			Date = formatter.format(System.currentTimeMillis());
			startProperties.put("Date", Date);
			startProperties.put("clickCross", Canceled);
			fileWriter.write(startProperties.toString());
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			closeFileWriter(fileWriter);
		}
		
	}
	private void calculateDay() throws ParseException {
		
		String nowDate= formatter.format(System.currentTimeMillis());
		Date beginDate = formatter.parse(Date);
		Date endDate = formatter.parse(nowDate);
		long days = TimeUnit.MILLISECONDS.toDays(endDate.getTime()-beginDate.getTime());
		if(days>=30){timeExpires =true;}
		
	}
	
	

	private void closeFileWriter(FileWriter fileWriter) {
		try {
			fileWriter.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	
	 private void readFromFile(){
		FileReader freader = null;
		try {
			freader = new FileReader(startRecord);
			char data[]=new char[1024];					
			int num=freader.read(data);			
			String str=new String(data,0,num);		
			JSONObject startProperties;
			startProperties = new JSONObject(str);
			 Date=startProperties.get("Date").toString();
			 Canceled=startProperties.get("clickCross").toString();
			 freader.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}  catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}	finally{
			closeFileReader(freader);
		}
	}
	 public void writeFromFile() throws IOException, JSONException{
		 readFromFile();
		 clearFile();
		 FileWriter fileWriter= new FileWriter(startRecord);
		 	startProperties.remove(Canceled);
		 	Canceled="true";
		 	startProperties.put("Date",Date);
			startProperties.put("clickCross",  Canceled);
          fileWriter.write(startProperties.toString());
          fileWriter.close();
		 
	 }
	 private  void clearFile() throws IOException {
			if(startRecord.exists()){ 
				startRecord.delete();
				startRecord.createNewFile();	 
			}
	 }

	private static void closeFileReader(FileReader freader) {
		try {
			freader.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	public boolean getTimeExpires()
	{
		return timeExpires;
	}
	public String getCanceled()
	{
		return Canceled;
	}
	public  void setTimeExpires(boolean timeExpires)
	{
		this.timeExpires=timeExpires;
	}
	
	
}
