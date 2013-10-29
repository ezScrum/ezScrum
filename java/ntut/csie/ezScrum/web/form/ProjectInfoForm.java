/*
 * Copyright (C) 2005 Chin-Yun Hsieh <hsieh@csie.ntut.edu.tw>
 *                    Yu Chin Cheng <yccheng@csie.ntut.edu.tw>
 *                    Chien-Tsun Chen <ctchen@ctchen.idv.tw>
 *                    Tsui-Chen She <kay_sher@hotmail.com>
 *                    Chia-Hao Wu<chwu2004@gmail.com>
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 */

//Created by MyEclipse Struts
// XSL source (default): platform:/plugin/com.genuitec.eclipse.cross.easystruts.eclipse_3.8.4/xslt/JavaClass.xsl
package ntut.csie.ezScrum.web.form;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.StringTokenizer;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.struts.validator.ValidatorForm;


/**
 * MyEclipse Struts
 * Creation date: 08-02-2005
 *
 * XDoclet definition:
 * @struts:form name="saveProjectInfoForm"
 */
public class ProjectInfoForm extends ValidatorForm {
    /**
         *
         */
    private static final long serialVersionUID = -8098707877077933303L;
    private static Log log = LogFactory.getLog(ProjectInfoForm.class);
    private String m_name = "";
    private String m_id = "";
    private String m_comment = "";
    private Date m_createDate = null;
    private String[] m_sourcePathArray = null;
    private String m_outputPath = "";
    private String m_version = "";
    private String m_state = "";
    private String projectManager = "";
    private String m_cvsHost = "";
    private String m_cvsRepositoryPath = "";
    private String m_cvsUserID = "";
    private String m_cvsPassword = "";
    private String m_cvsModuleName = "";
    private String m_cvsConnectionType = "";
    private String m_serverType ="";
    private String m_svnHook = "";
    //8/31 add Server Type 要注意變數大小寫 ? ex. m_ServerType 跟 getServerType setServerType 會找不到 
    //即使ProjectSummary.jsp寫了 ProjectInfoForm.ServerType也會掛掉
    //改成m_serverType  ProjectSummary改 ProjectInfoForm.serverType即可
    //=============新增有關Iteration Plan的資訊====================
    private String m_iterStartDate = "";
    private String m_iterIterval = "";
    private String m_iterNumber = "";
    private String m_iterMemberNumber = "";
    //
    private String m_attachFileSize = "";
    
    // ITS information
    private String m_serverIP = "127.0.0.1";
    private String m_serverPath = "/mantis/mc/mantisconnect.php";
    private String m_accountOfDB = "";
    private String m_pwdOfDB = "";
    
    public String getSvnHook(){
    	return this.m_svnHook;
    }
    public void setSvnHook(String svnHook){
    	m_svnHook = svnHook;
    }
    public String getServerType()
    {
    	return m_serverType;
    }
    public void setServerType(String serverType)
    {
    	m_serverType = serverType;
    }
    public String getComment() {
        return m_comment;
    }

    public Date getCreateDate() {
        return m_createDate;
    }

    public String getName() {
        return m_id;
    }

    public String getDisplayName() {
        return m_name;
    }

    public void setComment(String comment) {
        log.debug("ProjectInfoForm.setComment()=" + comment);

        this.m_comment = comment;
    }

    public void setCreateDate(Date createDate) {
        this.m_createDate = createDate;
    }

    public void setName(String id) {
        this.m_id = id;
    }

    public void setDisplayName(String name) {
        this.m_name = name;
    }

    public String getCvsConnectionType() {
        return m_cvsConnectionType;
    }

    public String getCvsHost() {
        return m_cvsHost;
    }

    public String getCvsModuleName() {
        return m_cvsModuleName;
    }

    public String getCvsPassword() {
        return m_cvsPassword;
    }

    public String getCvsRepositoryPath() {
        return m_cvsRepositoryPath;
    }

    public String getCvsUserID() {
        return m_cvsUserID;
    }

    public String getOutputPath() {
        return m_outputPath;
    }

    public String[] getSourcePaths() {
        return m_sourcePathArray;
    }

    public String getState() {
        return m_state;
    }

    public String getVersion() {
        return m_version;
    }

    public void setCvsConnectionType(String cvsConnectionType) {
        this.m_cvsConnectionType = cvsConnectionType;
    }

    public void setCvsHost(String cvsHost) {
        this.m_cvsHost = cvsHost;
    }

    public void setCvsModuleName(String cvsModuleName) {
        this.m_cvsModuleName = cvsModuleName;
    }

    public void setCvsPassword(String cvsPassword) {
        this.m_cvsPassword = cvsPassword;
    }

    public void setCvsRepositoryPath(String cvsRepositoryPath) {
        this.m_cvsRepositoryPath = cvsRepositoryPath;
    }

    public void setCvsUserID(String cvsUserID) {
        this.m_cvsUserID = cvsUserID;
    }

    public void setOutputPath(String outputPath) {
        this.m_outputPath = outputPath;
    }

    public void setSourcePaths(String[] sourcePathArray) {
        this.m_sourcePathArray = sourcePathArray;
    }

    public void setState(String state) {
        this.m_state = state;
    }

    public void setVersion(String version) {
        this.m_version = version;
    }

    public String getProjectManager() {
        return projectManager;
    }

    public void setProjectManager(String projectManager) {
        this.projectManager = projectManager;
    }

    public static ProjectInfoForm createProjectInfoForm() {
        ProjectInfoForm boundary = new ProjectInfoForm();

        return boundary;
    }

    public String getSourcePathString() {
        StringBuffer sb = new StringBuffer();

        if (m_sourcePathArray == null) {
            return "";
        }

        for (int i = 0; i < m_sourcePathArray.length; i++) {
            log.debug("getSourcePathString3");
            sb.append(m_sourcePathArray[i]);
            sb.append("; ");
        }

        return sb.toString();
    }

    public void setSourcePathString(String sourcePathString) {
        log.info("setSourcePathString=" + sourcePathString);

        StringTokenizer st = new StringTokenizer(sourcePathString, ";");

        List<String> list = new ArrayList<String>();

        while (st.hasMoreTokens()) {
            String path = st.nextToken().trim();

            if (!path.equals("")) {
                log.debug(path);
                list.add(path);
            }
        }

        m_sourcePathArray = (String[]) list.toArray(new String[0]);
    }
    
    //==========新增Iteration Plan的資訊============
	public String getIterStartDate() {
		return m_iterStartDate;
	}
	
	public void setIterStartDate(String startDate) {
		m_iterStartDate = startDate;
	}
	
	public String getIterIterval() {
		return m_iterIterval;
	}
	
	public void setIterIterval(String iterval) {
		m_iterIterval = iterval;
	}
	
	public String getIterNumber() {
		return m_iterNumber;
	}
	
	public void setIterNumber(String number) {
		m_iterNumber = number;
	}
	
	public String getIterMemberNumber() {
		return m_iterMemberNumber;
	}
	
	public void setIterMemberNumber(String memberNumber) {
		m_iterMemberNumber = memberNumber;
	}
	public String getAttachFileSize() {
		return m_attachFileSize;
	}
	public void setAttachFileSize(String fileSize) {
		m_attachFileSize = fileSize;
	}
	
	public String getServerURL() {
		return m_serverIP;
	}
	
	public void setServerURL(String IP) {
		m_serverIP = IP;
	}
	
	public String getServerPath() {
		return m_serverPath;
	}
	
	public void setServerPath(String Path) {
		m_serverPath = Path;
	}
	
	public String getAccountOfDB() {
		return m_accountOfDB;
	}
	
	public void setAccountOfDB(String Acc_DB) {
		m_accountOfDB = Acc_DB;
	}
	
	public String getPwdOfDB() {
		return m_pwdOfDB;
	}
	
	public void setPwdOfDB(String Pwd_DB) {
		m_pwdOfDB = Pwd_DB;
	}
}
