package ntut.csie.ezScrum.restful.dataMigration.support;

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;
import org.aspectj.util.FileUtil;
import org.junit.Test;

import com.sun.org.apache.xerces.internal.impl.dv.util.Base64;

public class FileDecoderTest {
	private final String BASE64_DATA_STRING = "U3RvcnkwMQ==";
	private final String FILE_NAME = "Story01.txt";
	@Test
	public void testToFile() throws IOException {
		File file = FileDecoder.toFile(FILE_NAME, BASE64_DATA_STRING);
		assertEquals(FILE_NAME, file.getName());
		byte[] byteArray = FileUtil.readAsByteArray(file);
		assertEquals(BASE64_DATA_STRING, Base64.encode(byteArray));
		// Remove Test Data /upload_tmp/filename
		FileUtils.deleteDirectory(new File(file.getParent()));
	}
}