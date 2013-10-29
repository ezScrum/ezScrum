package ntut.csie.ezScrum.tools;

import java.io.FileWriter;
import java.io.IOException;

import org.apache.commons.lang.time.StopWatch;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class FileStopWatch {

	Log log = LogFactory.getLog(FileStopWatch.class);

	private static FileStopWatch instance = null;

	StopWatch watch = null;
	FileWriter writer = null;
	String taskName = "";
	String title = "";

	public static FileStopWatch getInstance() {
		if (instance == null)
			instance = new FileStopWatch();

		return instance;
	}

	public void writeTitle(String s)
	{
		try {
			writer.write("========"+s+"========\n");
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
			
	}
	
	public void start(String taskName)
	{
		try {
			writer.write("--"+taskName+"--\n");
			watch.start();
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
	}
	
	public void stop()
	{
		try {
			watch.stop();
			String time = watch.toString();
			writer.write("time="+time+"\n");
			watch.reset();
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
	}
	
	public void openFile(String file) {
		watch = new StopWatch();
		try {
			writer = new FileWriter(file);
		} catch (IOException e) {
			log.error("開檔發生錯誤");
			e.printStackTrace();
		}
	}

	public void closeFile() {
		try 
		{
			if (writer != null)
				writer.close();
		} 
		catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}

	}

}
