package com.hawkins.utils;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.hawkins.dmanager.DownloadEntry;

public class FileUtils {
	
	private FileUtils() {
		
	}

	private static final Logger logger = LogManager.getLogger(FileUtils.class.getName());
	
	public static void copyToriginalFileName (DownloadEntry d) {
		
		try {
	    	Path copied = Paths.get(d.getFolder() + d.getFile());
		    Path originalPath = Paths.get(d.getFolder() + d.getOriginalFileName());
			Files.copy(copied, originalPath, StandardCopyOption.REPLACE_EXISTING);
			
			Files.isSameFile(copied, originalPath);
			
			Files.deleteIfExists(copied);
			
		} catch (IOException ioe) {
			if (logger.isDebugEnabled()) {
				logger.debug(ioe.getMessage());
			}
		}
	  
	    
	}
}
