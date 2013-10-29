package ntut.csie.ezScrum.tools;

import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.LinkedList;
import java.util.List;

public class DiscardHandler {
	final static public long DAYTIME_INMILLIS = 24 * 60 * 60 * 1000;	// 以天為單位的毫秒

	/**
	 * DiscardHandler判斷天數決定要不要刪除檔案
	 * 
	 * @param args[0] 要刪除的"build artifact"檔案的資料夾
	 * @param args[1] 要保留的天數
	 * @param args[2 ...] 要保留的檔案
	 */
	static public void main(String[] args) {
		String buildsDir = args[0];
		int daysToKeep = Integer.parseInt(args[1]);
		List<String> filesToKeep = new ArrayList<String>();

		for (int i = 2; i < args.length; i++)
			filesToKeep.add(args[i]);

		DiscardHandler handler = new DiscardHandler();
		try {
			handler.execute(buildsDir, daysToKeep, filesToKeep);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 取出檔案(資料夾)的名字("yyyy-MM-dd_HH-mm-ss")，轉換成毫秒
	 * 
	 * @param buildsDir "build artifact"的資料夾
	 * @param daysToKeep 要保留的天數
	 * @param filesToKeep 要保留的檔案
	 */
	public void execute(String buildsDir, int daysToKeep, List<String> filesToKeep) throws IOException {
		List<File> buildFolders = new LinkedList<File>();
		String path = buildsDir;

		// select the folders
		for (File f : new File(path).listFiles()) {
			if (f.getName().matches("\\d{4}-\\d{2}-\\d{2}_\\d{2}-\\d{2}-\\d{2}")) {
				buildFolders.add(f);
			}
		}

		// pick the folders which should be handle
		for (File f : buildFolders) {
			long time = parseFolderNameDateTime(f);
			if (isDayTimeOverdue(time, daysToKeep)) {
				killFilesInArtifactFolder(f, filesToKeep);
			}
		}
	}

	/**
	 * 取出檔案(資料夾)的名字("yyyy-MM-dd_HH-mm-ss")，轉換成毫秒
	 * 
	 * @param file 要轉換的格式的檔案
	 */
	private long parseFolderNameDateTime(File file) {
		String fileName = file.getName();
		DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss");
		long time = 0;
		try {
			time = formatter.parse(fileName).getTime();
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return time;
	}

	/**
	 * 比較是否到達使用者規定要刪除artifact的"天數"標準
	 * 
	 * @param time 資料夾的時間
	 * @param days 要保留的天數
	 */
	private boolean isDayTimeOverdue(long time, int days) {
		long delta = Calendar.getInstance().getTimeInMillis() - time;
		if (delta > days * DAYTIME_INMILLIS) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * 找到archive的資料夾，並進入裡面尋找要刪除的檔案
	 * 
	 * @param buildFolder 所要刪除的資料夾
	 * @param filesToKeep 要保留的檔案
	 * @author Zam
	 */
	private void killFilesInArtifactFolder(File buildFolder, List<String> filesToKeep) {
		File archiveDir = null;
		for (File dir : buildFolder.listFiles()) {
			if (dir.getName().equals("archive")) {
				archiveDir = dir;
				break;
			}
		}
		if (archiveDir != null) {
			killFiles(archiveDir, filesToKeep);
		}
	}

	/**
	 * 遞回直到找到擋案而非資料夾，找到檔案後執行刪除
	 * 
	 * @param dir 所要刪除的資料夾目錄
	 * @param filesToKeep 要保留的檔案
	 * @author Zam
	 */
	private void killFiles(File dir, List<String> filesToKeep) {
		for (File f : dir.listFiles()) {
			if (f.isDirectory()) {
				killFiles(f, filesToKeep);
			} else {
				if (isFileShouldBeKeep(f, filesToKeep)) {
					// System.out.println(f.getParentFile().getParentFile().getName() + " " + f.getName() + " KEEP");
				} else {
					System.out.println(f.getParentFile().getParentFile().getName() + " " + f.getName() + " KILLED");
					f.delete();
				}
			}
		}
	}

	private boolean isFileShouldBeKeep(File file, List<String> filesToKeep) {
		for (String keepFileName : filesToKeep)
			if (file.getName().equals(keepFileName)) return true;
		return false;
	}
}
