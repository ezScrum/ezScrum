package ntut.csie.ezScrum.SaaS;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import ntut.csie.ezScrum.SaaS.util.DateUtil;
import ntut.csie.ezScrum.web.dataObject.UserInformation;
import ntut.csie.ezScrum.web.form.ProjectInfoForm;
import ntut.csie.ezScrum.web.mapper.AccountMapper;
import ntut.csie.ezScrum.web.mapper.ProjectMapper;
import ntut.csie.jcis.account.core.IAccount;
import ntut.csie.jcis.resource.core.IProject;

public class CreateData {

	private int ProjectCount = 1;
	private List<IProject> ProjectList;
	public String PJ_ID = "TEST_PROJECT_";					// TEST_PROJECT_X
	public String PM_NAME = "Project_Manager_";				// Project_Manager_X	
	public String COMMENT_NAME = "This is Test Project - ";	// This is Test Project - X
	
	public void setupProject(int count) {
		this.ProjectCount = count;
		this.ProjectList = new LinkedList<IProject>();
	}
	
	public List<IProject> getProjectList() {
		return this.ProjectList;
	}
	
	public void exeCreateProject() {
		String projectId = "";
		ProjectMapper projectMapper = new ProjectMapper();
		// 自動產生輸入的專案個數
		for (int i=0 ; i < this.ProjectCount ; i++) {
			int index = i+1;
			projectId = this.PJ_ID + Integer.toString((index));	// TEST_PROJECT_X
			ProjectInfoForm form = this.fillFormByIndex(index);				
			IProject project = null;
		
			try {
				project = projectMapper.createProject(null, null, form);
			} catch (Exception e) {
				System.out.println("create project failed!");
				e.printStackTrace();
			}	
			
			// add to list
			this.ProjectList.add(project);
				
			System.out.println("Create Project [" + (i+1) + "]: " + projectId + " done!");				
		}
	}	
	
	/*
	 * local use
	 */
	
	private ProjectInfoForm fillFormByIndex(int index) {
		ProjectInfoForm form = new ProjectInfoForm();
		
		form.setName(this.PJ_ID + "_" + index);	// ID
		form.setDisplayName(this.PJ_ID + "_" + "DisplayName" + "_" + index);
		form.setComment(this.COMMENT_NAME + "_" + index);
		form.setProjectManager(this.PM_NAME + "_" + index);
		form.setCreateDate(DateUtil.getNowDate());
		
		return form;
	}			
	
	/*
	 * Create Account
	 */
	private int AccountCount = 0;
	private String Account_ID = "TEST_ACCOUNT_ID_";
	private String Account_NAME = "TEST_ACCOUNT_REALNAME_";
	private String Account_PWD = "TEST_ACCOUNT_PWD_";
	private String Account_Mail = "TEST_ACCOUNT_MAIL_";
	
	private List<IAccount> AccountList;
	
	public void setupAccount(int ACcount) {
		this.AccountCount = ACcount;
		this.AccountList = new ArrayList<IAccount>();
	}
	
	/**
	 * 自動產生Account建構時給的 count 個數
	 */
	public void exeCreateAccount() {
		String roles = "user";
		
		AccountMapper accountMapper = new AccountMapper();
		
		for (int i=0 ; i < this.AccountCount ; i++) {
			String ID = Integer.toString(i+1);
			String Acc_ID = this.Account_ID + ID;
			String Acc_RLNAME = this.Account_NAME + ID;
			String Acc_PWD = this.Account_PWD + ID;
			String Acc_Mail = this.Account_Mail + ID;
			String enable = "true";
			
			UserInformation userInformation = new UserInformation(Acc_ID, Acc_RLNAME, Acc_PWD, Acc_Mail, enable);
			IAccount account = accountMapper.createAccount(userInformation, roles);
			
			this.AccountList.add(account);
			System.out.println("Create " + this.AccountCount + " accounts success.");
		}
	}
	
	/**
	 * return ID = TEST_ACCOUNT_ID_X
	 */
	public String getAccount_ID(int i) {
		return (this.Account_ID + Integer.toString(i));
	}
	
	/**
	 * return Name = TEST_ACCOUNT_NAME_X
	 */
	public String getAccount_RealName(int i) {
		return (this.Account_NAME + Integer.toString(i));
	}

	/**
	 * return PWD = TEST_ACCOUNT_PWD_X
	 */
	public String getAccount_PWD(int i) {
		return (this.Account_PWD + Integer.toString(i));
	}

	/**
	 * return MAIL = TEST_ACCOUNT_MAIL_
	 */
	public String getAccount_Mail(int i) {
		return (this.Account_Mail + Integer.toString(i));
	}

	/**
	 * return the added account Object
	 */
	public List<IAccount> getAccountList() {
		return this.AccountList;
	}
	
	/**
	 * return Account counts
	 */
	public int getAccountCount() {
		return this.AccountCount;
	}
	
}
