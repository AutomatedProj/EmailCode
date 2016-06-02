package com.test.demo.Email;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Properties;
import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;
import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

/**
 * SendMail class is basically used for sending out mail to the specified
 * recipients, and the body of the mail basically contains the details of the
 * report of the test runs and a zip file is also attached along with mail that
 * is also being sent.
 * 
 * @author Jaidip Ghosh,Shantam Khare
 * @version 1.5
 *
 */

public class SendMail {
	public static void main(String args[]) throws MessagingException {
		String recipients[] = { "jaidip1994@gmail.com", "shantam23khare@gmail.com" };
		boolean debug = false;
		Properties props = new Properties();
		// Mail Prop
		props.put("mail.transport.protocol", "smtp");
		props.put("mail.smtp.host", "smtp.gmail.com");
		props.put("mail.smtp.user", "sbsdbdj@gmail.com");
		props.put("mail.smtp.password", "sbsdbj2ee");
		props.put("mail.smtp.starttls.enable", "true");
		props.put("mail.smtp.auth", "true");
		props.put("mail.smtp.port", "25");
		// props.put("mail.smtp.socketFactory.class",
		// "javax.net.ssl.SSLSocketFactory");

		Authenticator auth = (new SendMail()).new SMTPAuthenticator();
		Session session1 = Session.getDefaultInstance(props, auth);
		session1.setDebug(debug);
		Message msg = new MimeMessage(session1);
		InternetAddress addressFrom = new InternetAddress("sbsdbdj@gmail.com");
		msg.setFrom(addressFrom);
		InternetAddress[] addressTo = new InternetAddress[recipients.length];
		for (int i = 0; i < recipients.length; i++) {
			addressTo[i] = new InternetAddress(recipients[i]);
		}
		msg.setRecipients(Message.RecipientType.TO, addressTo);
		msg.addHeader("MyHeaderName", "myHeaderValue");
		Calendar calendar = Calendar.getInstance();
		Date dat = calendar.getTime();
		SimpleDateFormat formatter = new SimpleDateFormat("d MMM yyyy");
		String date = formatter.format(dat);
		msg.setSubject("Reports for "+date+", of Automation Scripts");

		String filepath = AppZip.start();
		String filename = filepath.substring(filepath.lastIndexOf("\\") + 1);
		BodyPart messageBodyPart = new MimeBodyPart();
		messageBodyPart.setContent(
				"Hi,<br/>This is an automatically generated mail.<br/>To render the screenshots in the reports, the folders should be extracted and stored in the following directories.<br/>"
						+ AppZip.SOURCE_FOLDERSDEMO + "<br/><p>The Report is in this location :- <a href="
						+ AppZip.FINAL_REPORT_LOCATION + ">" + AppZip.FINAL_REPORT_LOCATION + "</a>. The File name is "
						+ filename + ".</p><br/><br/><h3>Summary of Reports:-</h3>" + AppZip.MessageBody
						+ "<br/><br/>Regards,<br/>Auto-Bot",
				"text/html");
		Multipart mpart = new MimeMultipart();
		mpart.addBodyPart(messageBodyPart);
		// This part basically is used for sending the attachment to recipient.

		messageBodyPart = new MimeBodyPart();
		DataSource source = new FileDataSource(filepath);
		messageBodyPart.setDataHandler(new DataHandler(source));
		messageBodyPart.setFileName(filename);
		mpart.addBodyPart(messageBodyPart);

		msg.setContent(mpart);
		msg.saveChanges();

		Transport transport = session1.getTransport("smtp");
		transport.connect("smtp.gmail.com", "sbsdbdj@gmail.com", "sbsdbj2ee");
		System.out.println("Message sending is in progress");

		// transport.sendMessage(msg, msg.getAllRecipients());

		transport.close();
		Transport.send(msg);
		System.out.println("Message Sent Successfully");
	}

	/**
	 * SMTPAuthenticator class basically sends an instance of
	 * PasswordAuthentication that is going to be needed for authentication
	 * purpose while sending the mail.
	 * 
	 * @author Jaidip Ghosh
	 *
	 */
	private class SMTPAuthenticator extends javax.mail.Authenticator {
		public PasswordAuthentication getPasswordAuthentication() {

			String user = "sbsdbdj@gmail.com";
			String pwd = "sbsdbj2ee";
			return new PasswordAuthentication(user, pwd);
		}
	}

}
