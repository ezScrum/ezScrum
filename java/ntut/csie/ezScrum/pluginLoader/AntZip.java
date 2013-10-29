package ntut.csie.ezScrum.pluginLoader;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Enumeration;

import org.apache.tools.zip.ZipEntry;
import org.apache.tools.zip.ZipFile;
import org.apache.tools.zip.ZipOutputStream;

public class AntZip {

	private ZipFile zipFile;
	private ZipOutputStream zipOut;
	private int bufSize; // size of bytes
	private byte[] buf;
	private int readedBytes;

	public AntZip() {
		this(512);
	}

	public AntZip(int bufSize) {
		this.bufSize = bufSize;
		this.buf = new byte[this.bufSize];
	}

	// Zip target directory
	public void doZip(String zipDirectory) {
		File zipDir;

		zipDir = new File(zipDirectory);
		String zipFileName = zipDir.getName() + ".zip";// file name of zip

		try {
			this.zipOut = new ZipOutputStream(new BufferedOutputStream(
					new FileOutputStream(zipFileName)));
			handleDir(zipDir, this.zipOut);
			this.zipOut.close();
		} catch (IOException ioe) {
			ioe.printStackTrace();
		}
	}

	// Recursive read file, call by doZip
	private void handleDir(File dir, ZipOutputStream zipOut) throws IOException {
		FileInputStream fileIn;
		File[] files;

		files = dir.listFiles();

		if (files.length == 0) {// create if empty
			this.zipOut.putNextEntry(new ZipEntry(dir.toString() + "/"));
			this.zipOut.closeEntry();
		} else {// handle directory and file if not empty
			for (File fileName : files) {
				if (fileName.isDirectory()) {
					handleDir(fileName, this.zipOut);
				} else {
					fileIn = new FileInputStream(fileName);
					this.zipOut.putNextEntry(new ZipEntry(fileName.toString()));

					while ((this.readedBytes = fileIn.read(this.buf)) > 0) {
						this.zipOut.write(this.buf, 0, this.readedBytes);
					}

					this.zipOut.closeEntry();
				}
			}
		}
	}

	// unzip zip file to target directory
	public void unZip(String unZipfileName, String targetDir) {
		FileOutputStream fileOut;
		File file;
		InputStream inputStream;
		try {
			this.zipFile = new ZipFile(unZipfileName);
			for (Enumeration<?> entries = this.zipFile.getEntries(); entries
					.hasMoreElements();) {
				ZipEntry entry = (ZipEntry) entries.nextElement();
				file = new File(targetDir + "/" + entry.getName());

				if (entry.isDirectory()) {
					file.mkdirs();
				} else {
					// create directory if not existed, 
					File parent = file.getParentFile();
					if (!parent.exists()) {
						parent.mkdirs();
					}

					inputStream = zipFile.getInputStream(entry);
					fileOut = new FileOutputStream(file);
					while ((this.readedBytes = inputStream.read(this.buf)) > 0) {
						fileOut.write(this.buf, 0, this.readedBytes);
					}
					fileOut.close();

					inputStream.close();
				}
			}
			this.zipFile.close();
		} catch (IOException ioe) {
			ioe.printStackTrace();
		}
	}

	// set buffer size
	public void setBufSize(int bufSize) {
		this.bufSize = bufSize;
	}
	
}
