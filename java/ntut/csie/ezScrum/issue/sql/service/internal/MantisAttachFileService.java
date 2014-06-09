package ntut.csie.ezScrum.issue.sql.service.internal;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.sql.ResultSet;
import java.sql.SQLException;

import ntut.csie.ezScrum.issue.core.IIssue;
import ntut.csie.ezScrum.issue.internal.IssueAttachFile;
import ntut.csie.ezScrum.issue.sql.service.core.Configuration;
import ntut.csie.ezScrum.issue.sql.service.core.IQueryValueSet;
import ntut.csie.ezScrum.issue.sql.service.core.ITSPrefsStorage;
import ntut.csie.ezScrum.issue.sql.service.tool.ISQLControl;

public class MantisAttachFileService extends AbstractMantisService {

	public MantisAttachFileService(ISQLControl control, Configuration config) {
		setControl(control);
		setConfig(config);
	}

	public void initAttachFile(IIssue issue) {
		IQueryValueSet valueSet = new MySQLQuerySet();
		valueSet.addTableName("mantis_bug_file_table");
		valueSet.addEqualCondition("bug_id", Long.toString(issue.getIssueID()));
		valueSet.setOrderBy("date_added", MySQLQuerySet.DESC_ORDER);
		String query = valueSet.getSelectQuery();

		ResultSet result = getControl().executeQuery(query);
		try {
			while (result.next()) {
				try {
					IssueAttachFile attach = new IssueAttachFile();
					attach.setIssueID(issue.getIssueID());
					attach.setAttachFileId(result.getLong("id"));
					attach.setDescription(result.getString("description"));
					attach.setDiskfile(result.getString("diskfile"));
					attach.setFilesize(result.getInt("filesize"));
					attach.setFileType(result.getString("file_type"));
					attach.setFolder(result.getString("folder"));
					attach.setTitle(result.getString("title"));
					attach.setFilename(result.getString("filename"));
					attach.setDate_added(result.getTimestamp("date_added")
							.getTime());
					issue.addAttachFile(attach);
				} catch (Exception e) {
					// 多一層try只是為了防止取得型態錯誤的值
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 透過 file id 抓取 File，不透過 mantis
	 */
	public File getAttachFile(String fileID){
		//設定SQL
		IQueryValueSet valueSet = new MySQLQuerySet();
		
		valueSet.addTableName("mantis_bug_file_table");
		valueSet.addEqualCondition("id", fileID);
			
		//取得sql語法
		String query = valueSet.getSelectQuery();
		return getSelectAttachFile(query);
	}
	
	/**
	 * 透過 file name 抓取 File，不透過 mantis
	 */
	public File getAttachFileByName(String fileName){
		//設定SQL
		IQueryValueSet valueSet = new MySQLQuerySet();
		
		valueSet.addTableName("mantis_bug_file_table");
		valueSet.addEqualCondition("filename", "'" + fileName + "'");
		valueSet.setOrderBy("date_added", MySQLQuerySet.DESC_ORDER);
			
		//取得sql語法
		String query = valueSet.getSelectQuery();
		return getSelectAttachFile(query);
	}
	
	private File getSelectAttachFile(String query) {
		try {
			ResultSet result = getControl().executeQuery(query);
			//取得file的資訊
			if (result.next()) {
				InputStream stream = result.getBlob("content").getBinaryStream();
				File temp = null;
				temp = File.createTempFile("ezScrum", Long.toString(System.nanoTime()));
				OutputStream out = new FileOutputStream(temp);
			    byte buf[]=new byte[1024];
			    int len;
			    while((len=stream.read(buf))>0){
				    out.write(buf,0,len);
			    }
			    out.close();
			    stream.close();

			    return temp;
			}
		} catch (IOException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}
}
