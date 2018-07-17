package main;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;



public class ZipFileNameGetter {

	private static String lastModyfyFileName = "";
	private static long modifyTime = 0;
	private static String zipFileName;
	private static String zipFilePath;
	
	public static void main(String[] args) {
		
		
		if (args.length >= 1)
			zipFilePath = args[0];
			 
		else {
			System.out.println("No set all input parameters for script: ZipFileNameGetter <path> <mask of filename>");
			System.exit(0);
		}
        
        if (args.length >= 2)
        	zipFileName = args[1];
        else { 
        	System.out.println("No set all input parameters for script: ZipFileNameGetter <path> <mask of filename>");
			System.exit(0);
		}
        
		
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyMMdd");
		zipFileName += "_"+dateFormat.format(new Date());
		
		System.out.println(zipFileName);
		
		getActualZipFileName(zipFilePath);
		System.out.println("Last Modified file :" + lastModyfyFileName);
		//System.out.println("Modify time :" + modifyTime);
		
		
		
	}
	
	private static void getActualZipFileName(String path) {
			
			File zipFilesDir = new File(path);
			
			System.out.println("Get list of files in directory " + path);

			for (File item : zipFilesDir.listFiles()) {
			    if (item.isFile()) {
			    	if (item.getName().startsWith(zipFileName)) {	
			    			
			    			if (item.lastModified() > modifyTime) {
			    				lastModyfyFileName =item.getName(); 
				    			modifyTime = item.lastModified();
			    			}
			    						    			
			    			System.out.println("File :" + item.getName() + "   Seize :" + item.length());

			    	} else
			    			System.out.println("File :" + item.getName() + "   Seize :" + item.length() + " :" + item.lastModified()
			    			+ ". This file will be ignored. It is NO input File.");
			    	}
			}
			
	}


}
