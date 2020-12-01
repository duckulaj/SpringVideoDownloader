package com.hawkins.controller;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.mobile.device.Device;
import org.springframework.mobile.device.site.SitePreference;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;

import com.hawkins.properties.DmProperties;
import com.hawkins.properties.DownloadProperties;

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

	@GetMapping("/convertToSteam")
	// @RequestMapping(path = "/", method = RequestMethod.GET)
	public String initial(Model model, Device device, SitePreference sitePreference) {

		if (logger.isDebugEnabled()) {
			logger.debug("initial :: Device is {}", device.getDevicePlatform());
			logger.debug("initial :: Mobile is {}", device.isMobile());
			logger.debug("initial :: Normal is {}", device.isNormal());
			logger.debug("initial :: Tablet is {}", device.isTablet());
		}

		return "m3u2strm";
	}

}
