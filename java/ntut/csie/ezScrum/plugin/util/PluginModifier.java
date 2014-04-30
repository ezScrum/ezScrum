package ntut.csie.ezScrum.plugin.util;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.InputStreamReader;

public class PluginModifier {
	private String filePath = "./WebContent/Pages/ImportPluginList.jsp";

	public void addPluginImportPath(String pluginPath) throws Exception {
		String result = this.readFileContent();
		if (!result.contains(pluginPath)) {//該plugin不存在時加入該plugin
			//修改成新的內容
			result = result.replace("</head>", "<jsp:include page=\"../pluginWorkspace/" + pluginPath + "/import.jsp\"/>" + "\n" + "</head>");
			//將新的內容寫入檔案中
			this.writeFileContent(result);
		} else {
			throw new Exception("plugin is existed");
		}
	}

	public void removePluginImportPath(String pluginPath) throws Exception {
		String result = "";
		result = this.readFileContent();
		if (result.contains(pluginPath)) {//想被移除的plugin如果存在
			//移除
			result = result.replace("<jsp:include page=\"../pluginWorkspace/" + pluginPath + "/import.jsp\"/>" + System.getProperty("line.separator"), "");
			//將新的內容寫入檔案中
			this.writeFileContent(result);
		} else {
			throw new Exception("plugin is already removed");
		}
	}

	private void writeFileContent(String result) {//寫入檔案內容
		try {
			// Create file 
			FileWriter fireWriter = new FileWriter(filePath);
			BufferedWriter bufferWriter = new BufferedWriter(fireWriter);
			bufferWriter.write(result);
			//Close resource
			bufferWriter.close();
			bufferWriter.close();
			fireWriter.close();
		} catch (Exception e) {//Catch exception if any
			System.err.println("Error: " + e.getMessage());
		}
	}

	private String readFileContent() {//讀出檔案內容
		String result = "";
		try {
			//將舊的內容讀出
			FileInputStream fileInpuStream = new FileInputStream(filePath);
			DataInputStream dataInputStream = new DataInputStream(fileInpuStream);
			BufferedReader bufferReader = new BufferedReader(new InputStreamReader(dataInputStream));
			String strLine;
			String newLine = System.getProperty("line.separator");
			//Read File Line By Line
			while ((strLine = bufferReader.readLine()) != null) {
				// Print the content on the console
				if (!result.equals("")) {
					result += newLine + strLine;
				} else {
					result += strLine;
				}
			}
			//Close resource
			bufferReader.close();
			dataInputStream.close();
			fileInpuStream.close();

		} catch (Exception e) {//Catch exception if any
			System.err.println("Error: " + e.getMessage());
		}
		return result;
	}

	public void setTestFilePath(String testFilePath) {
		this.filePath = testFilePath;
	}
}
