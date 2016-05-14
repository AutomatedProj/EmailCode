package com.test.demo.Email;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

/**
 * AppZip class basically contains methods that helps in generating the file
 * list and zippping of the file on the specified directory.
 * 
 * @author Jaidip Ghosh,Shantam Khare
 * @version 1.3s
 *
 */
public class AppZip {
	List<String> fileList;
	public static final String FINAL_REPORT_LOCATION = "\\\\MO-8DF1B838C\\Email_Zip";

	private static final String OUTPUT_ZIP_FILE = "C:\\Email_Zip\\Reports.zip";
	public static final String SOURCE_FOLDERSDEMO = "C:\\ExtentReportsOutput\\DEMO";
	public static FileOutputStream fos = null;
	public static ZipOutputStream zos = null;
	public static String MessageBody = "";
	public static String FolderName = "";

	/**
	 * AppZip is the constructor for initializing the filelist which of List
	 * type.
	 */
	public AppZip() {
		fileList = new ArrayList<String>();
	}

	/**
	 * start method basically have the calls to the function which prepares the
	 * files list as well as well the zipping of the folders.
	 * 
	 * @return the output zipfile path
	 */
	public static String start() {
		AppZip appZip = new AppZip();
		List<String> srcs = new ArrayList<String>();
		srcs.add(0, SOURCE_FOLDERSDEMO);
		try {
			fos = new FileOutputStream(OUTPUT_ZIP_FILE);
			zos = new ZipOutputStream(fos);
			for (String source : srcs) {
				appZip.getTheRecentDirectory(new File(source), source);
				appZip.zipIt(OUTPUT_ZIP_FILE, source);
				appZip = new AppZip();
			}
			zos.close();
			System.out.println("Done Compressing The File");
		} catch (IOException ex) {
			ex.printStackTrace();
		}
		return OUTPUT_ZIP_FILE;
	}

	/**
	 * Zip it methods zips the content of the file list.
	 * 
	 * @param zipFile
	 *            output ZIP file location
	 */
	public void zipIt(String zipFile, String SOURCE_FOLDER) {

		byte[] buffer = new byte[1024];

		try {
			System.out.println("Output to Zip : " + zipFile);

			for (String file : this.fileList) {
				System.out.println("File Added : " + file);
				ZipEntry ze = new ZipEntry(file);
				zos.putNextEntry(ze);
				if (file.contains(SOURCE_FOLDER.substring(SOURCE_FOLDER.lastIndexOf("\\") + 1) + "LogFile.txt")) {
					Path path = Paths.get(SOURCE_FOLDER + File.separator + file);
					Charset charset = Charset.forName("UTF-8");
					BufferedReader reader = Files.newBufferedReader(path, charset);
					String line = "";
					if (!MessageBody.isEmpty()) {
						MessageBody += "<br/><br/>";
					}
					while ((line = reader.readLine()) != null) {
						MessageBody += line;
					}
					MessageBody += FolderName;
				}
				FileInputStream in = new FileInputStream(SOURCE_FOLDER + File.separator + file);
				int len;
				while ((len = in.read(buffer)) > 0) {
					zos.write(buffer, 0, len);
				}

				in.close();
			}

			zos.closeEntry();
		} catch (IOException ex) {
			ex.printStackTrace();
		}
	}

	/**
	 * Traverse a directory and get all files, and add the file into fileList
	 * 
	 * @param node
	 *            file or directory
	 */
	public void generateFileList(File node, String SOURCE_FOLDER) {

		// add file only
		if (node.isFile()) {
			fileList.add(generateZipEntry(node.getAbsoluteFile().toString(), SOURCE_FOLDER));
		}

		if (node.isDirectory()) {
			String[] subNote = node.list();
			for (String filename : subNote) {
				generateFileList(new File(node, filename), SOURCE_FOLDER);
			}
		}

	}

	/**
	 * getTheRecentDirectory method basically determines the recent directive.
	 * 
	 * @param node
	 *            file or directory
	 */
	public void getTheRecentDirectory(File node, String SOURCE_FOLDER) {
		if (node.isDirectory()) {
			Long recentTime = Long.MIN_VALUE;
			String[] subNote = node.list();
			File recent = null;
			for (String filename : subNote) {
				File file = new File(node, filename);
				if (file.lastModified() > recentTime) {
					recent = file;
					recentTime = file.lastModified();
				}
			}
			String systemName = FINAL_REPORT_LOCATION.substring(0, FINAL_REPORT_LOCATION.lastIndexOf("\\"));
			String fldrName = SOURCE_FOLDER.substring(SOURCE_FOLDER.indexOf("\\") + 1);
			systemName += "\\" + fldrName + "\\" + recent.getName();
			FolderName = "The File shortcut is " + "<a href=\"" + systemName + "\">" + systemName + "</a>";
			generateFileList(recent, SOURCE_FOLDER);
		}

	}

	/**
	 * Format the file path for zip
	 * 
	 * @param file
	 *            file path
	 * @return Formatted file path
	 */
	private String generateZipEntry(String file, String SOURCE_FOLDER) {
		return file.substring(SOURCE_FOLDER.length() + 1, file.length());
	}
}