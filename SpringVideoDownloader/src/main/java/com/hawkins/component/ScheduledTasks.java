package com.hawkins.component;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.hawkins.epg.EpgReader;
import com.hawkins.m3u.M3UtoStrm;
import com.hawkins.properties.DownloadProperties;
import com.hawkins.utils.Utils;
import com.hawkins.utils.Xteve;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class ScheduledTasks {

	@Scheduled(cron = "0 1 1 * * ?") // 1.01am
	public void resetM3UFile() {

		if (log.isDebugEnabled()) {
			log.debug("The time is now {}", Utils.printNow());
		}

		DownloadProperties downloadProperties = DownloadProperties.getInstance();

		Utils.copyUrlToFile(downloadProperties.getChannels(), downloadProperties.getFullM3U());
		if (log.isDebugEnabled()) {
			log.debug("Reloaded m3u file at {}", Utils.printNow());
		}
	}


	@Scheduled(cron = "0 1 5 * * ?") // 5.01am
	public void createStreams() {

		M3UtoStrm.convertM3UtoStream();
		log.info("Scheduled Task createStreams) completed at {}", Utils.printNow());
	}

	@Scheduled(cron = "0 10 1 * * ?") // 1.10am
	public void reloadEPG() {

		DownloadProperties dp = DownloadProperties.getInstance();
		String epgFile = dp.getEpgFileName();

		EpgReader.changeLocalTime(epgFile);
		log.info("Scheduled Task reloadEPG completed at {}", Utils.printNow());
	}
	
	
}