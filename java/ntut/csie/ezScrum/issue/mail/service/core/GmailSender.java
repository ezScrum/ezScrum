package ntut.csie.ezScrum.issue.mail.service.core;

import java.util.Properties;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

import ntut.csie.ezScrum.dao.AccountDAO;
import ntut.csie.ezScrum.iteration.core.ScrumEnum;
import ntut.csie.ezScrum.pic.core.ScrumRole;
import ntut.csie.ezScrum.web.action.rbac.GetAccountListAction;
import ntut.csie.ezScrum.web.dataObject.AccountObject;

public class GmailSender {
	private final String host_ = "smtp.gmail.com";
	private final int port_ = 465;
	private String username_;
	private String password_;
	private Properties props_ = new Properties();
	private Session session_;

	public GmailSender(String username, String password) {
		username_ = username;
		password_ = password;
		System.setProperty("mail.mime.charset", "big5");
		props_.put("mail.smtp.host", host_);
		props_.put("mail.smtp.socketFactory.port", port_);
		props_.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
		props_.put("mail.smtp.auth", "true");
		props_.put("mail.smtp.port", port_);
		props_.put("mail.smtp.ssl.trust", "smtp.gmail.com");

		session_ = Session.getInstance(props_, new javax.mail.Authenticator() {
			protected PasswordAuthentication getPasswordAuthentication() {
				return new PasswordAuthentication(username_, password_);
			}
		});
	}

	public String send(String address, String subject, String sprintGoal, String storyInfo, String schedule) {
		
		try {
			Message message = new MimeMessage(session_);
			MimeMultipart multipart = new MimeMultipart();
			MimeBodyPart messageBody = new MimeBodyPart();
			message.setFrom(new InternetAddress(username_));
			String[] recivers = address.split(";");
			for(int reciverNum = 0; reciverNum < recivers.length; reciverNum++){
				String recive = recivers[reciverNum];
				AccountObject reciver = AccountDAO.getInstance().get(recive);
				message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(reciver.getEmail()));
				message.setSubject(subject);
				String content = "                             " + "<font size=+4><b>"+subject+"</b></font><br><br>";
				content = content + "<font size=+3><b>Sprint Goal</b></font><br>";
				content = content + "<font size=4><li>"+sprintGoal+"</li></font><br>";
				content = content + "<font size=+3><b>Sprint Backlog(Estimates in Parenthesis)</b></font><br>";
				content = content + "<font size=4>"+storyInfo+"</font><br><br>";
				content = content + "<font size=+3><b>Schedule</b></font><br>";
				content = content + "<font size=4>" + schedule+ "</font>";

				messageBody.setContent(content, "text/html; charset=utf-8");
				multipart.addBodyPart(messageBody);
				message.setContent(multipart);
				
				Transport transport = session_.getTransport("smtp");
				transport.connect(host_, port_, username_, password_);
				Transport.send(message);
			}
			
//			
//			String[] SplittedStr = address.split(";");
//			
//			message.setSubject(subject);
//			message.setContent(text, "text/html");
//			
//			for (String Str : SplittedStr) {
//				
//				message.addRecipient(Message.RecipientType.TO, new InternetAddress(Str));
//			}
//			
			
			return "寄送email結束.";
		} catch (MessagingException e) {
			String errorMessage = e.getMessage().toString();
			if (errorMessage.contains("https://accounts.google.com/signin/continue?")) {
				return "請開啟    安全性較低的應用程式存取權限  或使用設定google兩段式登入";
			} else if (errorMessage.contains("Username and Password not accepted")) {
				return "帳號密碼不正確";
			} else if (errorMessage.contains("Invalid Addresses")) {
				return "送信位址格式不正確";
			} else if (errorMessage.contains("Could not connect to SMTP host: smtp.gmail.com")) {
				return "無法連線到SMTP host，請檢察防火牆或Proxy設定";
			} else if (errorMessage.contains("Unknown SMTP host: smtp.gmail.com")) {
				return "Unknown SMTP host: smtp.gmail.com，請檢察網路連線";
			} else {
				return e.getMessage().toString();
			}
		}
	}

}
