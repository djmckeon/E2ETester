package com.cs633.team1;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import javax.swing.JFileChooser;

/**
 * Application preferences
 * @author mckeon
 *
 */
public class Preferences {
	private String defaultPath;
	private int loopWaitCount;
	private boolean rewriteResults;
	private String iniFileName;
	
	/**
	 * Default constructor
	 */
	public Preferences() {
    	this.setIniFileName("E2ETester.ini");
		File iniFile = new File(iniFileName);

		// If the default path is missing, assume the ini file isn't loaded yet
		if (this.getDefaultPath() == null || this.getDefaultPath().isEmpty()) {
			if (!iniFile.exists()) {
				createNewIniFile();
			} else {
				readIniFile();
			}
		}
	}
	
	/**
	 * Reload the values from the file
	 */
	public void reloadValues() {
		readIniFile();
	}
	
	/**
	 * Read the ini file and set the preferences
	 */
	private void readIniFile() {
    	BufferedReader br = null;
    	String line = "";
		
		try {
			br = new BufferedReader(new FileReader(this.getIniFileName()));
			while ((line = br.readLine()) != null) {
    			String[] nameValuePair = line.split("=");  // use '=' as separator
    			switch (nameValuePair[0]) {
    			case "DefaultPath":
    				this.setDefaultPath(nameValuePair[1], false);
    				break;
    			case "LoopWaitCount":
    				this.setLoopWaitCount(Integer.parseInt(nameValuePair[1]), false);
    				break;
    			case "RewriteResults":
    				this.setRewriteResults(Boolean.parseBoolean(nameValuePair[1]), false);
    				break;
    			}
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
    	} finally {
    		if (br != null) {
    			try {
    				br.close();
    			} catch (IOException e) {
    				e.printStackTrace();
    			}
    		}
    	}

	}

	/**
	 * Create a new ini file using default values
	 */
	private void createNewIniFile() {
		// Get the default system path
		JFileChooser chooser = new JFileChooser();
		this.setDefaultPath(chooser.getCurrentDirectory().getAbsolutePath(), false);
		this.setLoopWaitCount(60000, false);  //Default to 60 seconds
		this.setRewriteResults(true, false);  //Default to rewriting the test results
		
		writeIniFile();
	}
	
	private void writeIniFile() {
        FileWriter fstream = null;
		try {
			fstream = new FileWriter(this.getIniFileName());
			BufferedWriter out = new BufferedWriter(fstream);
			out.write("DefaultPath=" + this.getDefaultPath());
			out.newLine();
			out.write("LoopWaitCount=" + this.getLoopWaitCount());
			out.newLine();
			out.write("RewriteResults=" + this.isRewriteResults());
			out.newLine();
			out.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	
	}
	
	public String getDefaultPath() {
		return defaultPath;
	}

	public void setDefaultPath(String defaultPath, boolean rewriteFile) {
		try {
			if (!this.defaultPath.equals(defaultPath)) {
				this.defaultPath = defaultPath;
				if (rewriteFile) {
					writeIniFile();
				}
			}
		} catch (NullPointerException e) {
			this.defaultPath = defaultPath;
			if (rewriteFile) {
				writeIniFile();
			}
		}
	}

	public int getLoopWaitCount() {
		return loopWaitCount;
	}

	public void setLoopWaitCount(int loopWaitCount, boolean rewriteFile) {
		if (this.loopWaitCount != loopWaitCount) {
			this.loopWaitCount = loopWaitCount;
			if (rewriteFile) {
				writeIniFile();
			}
		}
	}

	public boolean isRewriteResults() {
		return rewriteResults;
	}

	public void setRewriteResults(boolean rewriteResults, boolean rewriteFile) {
		if (this.rewriteResults != rewriteResults) {
			this.rewriteResults = rewriteResults;
			if (rewriteFile) {
				writeIniFile();
			}
		}
	}

	public String getIniFileName() {
		return iniFileName;
	}

	public void setIniFileName(String iniFileName) {
		this.iniFileName = iniFileName;
	}
	
}
