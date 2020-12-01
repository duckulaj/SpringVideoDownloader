package com.hawkins.controller;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;

import com.hawkins.m3u.M3UtoStrm;
import com.hawkins.properties.DmProperties;
import com.hawkins.properties.DownloadProperties;
import com.hawkins.utils.Constants;

@Controller
public class m3u2strmController {

	private static final Logger logger = LogManager.getLogger(m3u2strmController.class.getName());
	
	DownloadProperties downloadProperties = DownloadProperties.getInstance();
	DmProperties dmProperties = DmProperties.getInstance();
	
	@ModelAttribute
	public void initValues(Model model) {
		
		dmProperties = DmProperties.getInstance();;
		downloadProperties = DownloadProperties.getInstance();

	}

	@GetMapping("/convertToStream") public String convertM3UtoStream(Model model) {
		
		M3UtoStrm.convertM3UtoStream();
		return Constants.DOWNLOAD;
	}
}
