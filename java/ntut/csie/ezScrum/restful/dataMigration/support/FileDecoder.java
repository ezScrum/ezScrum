package ntut.csie.ezScrum.restful.dataMigration.support;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;

import com.sun.org.apache.xerces.internal.impl.dv.util.Base64;

public class FileDecoder {
	public static File toFile(String fileName, String base64BinaryString) throws IOException {
		String tempUploadFolder = "upload_tmp";
		File folder = new File(tempUploadFolder);
		folder.mkdirs();
		File file = new File(tempUploadFolder + File.separator + fileName);
		byte[] originBinary = Base64.decode(base64BinaryString);
		FileUtils.writeByteArrayToFile(file, originBinary);
		return file;
	}
}
