package com.hawkins.m3u;

import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.hawkins.properties.DmProperties;
import com.hawkins.properties.DownloadProperties;
import com.hawkins.utils.Constants;

public class M3UtoStrm {
	
	private static final Logger logger = LogManager.getLogger(M3UtoStrm.class.getName());
	
	private static String[] videoTypes = {Constants.AVI, Constants.MKV, Constants.MP4};
	
	public static void convertM3UtoStream () {
		
		/*
		 * 1. Get an instance of the group list
		 * 2. For each group decide if it is TV VOD or Film VOD
		 * 3. For films we need to create a folder for each film
		 * 4. In each folder write out the link to a strm file
		 * 5. For TV Shows create a folder
		 * 6. Create a subfolder for each Season
		 * 7. Create a an strm file for each episode within a season
		 */
		
		M3UGroupList groups = M3UGroupList.getInstance();
		M3UPlayList playlist = M3UPlayList.getInstance();
		DmProperties dmProperties = DmProperties.getInstance();
		DownloadProperties downloadProperties = DownloadProperties.getInstance();
		
		playlist.getPlayList().forEach(item -> {
			String groupTitle = item.getGroupTitle();
			M3UGroup thisGroup = M3UParser.getGroupByName(groups, groupTitle);
			
			if (thisGroup!= null) {
				String stream = item.getUrl();
				String streamType = deriveStreamType(item.getName());
				
				thisGroup.setType(streamType);
			}
			
			
		});
	}
		
		
	public static String deriveStreamType (String stream) {

		/*
		 * TO-DO We need the item type as well to run the regex
		 */
			String streamType = null;
			String videoExtension = null;
			
			// Get the last three characters from the stream
			
			if (stream.length() > 3) videoExtension = stream.substring(stream.length() - 3);
			
			if (Arrays.asList(videoTypes).contains(videoExtension)) {
				// Check to see if we have Season and Episode information in the form of S01 E01
				Pattern pattern = Pattern.compile("[S]{1}[0-9]{2} [E]{1}[0-9]{2}", Pattern.CASE_INSENSITIVE);
				Matcher matcher = pattern.matcher(stream);
				boolean matchFound = matcher.find();
				
				if (matchFound) {
					streamType = Constants.TVSHOW;
				} else {
					streamType = Constants.MOVIE;
				}
				
			} else {
				streamType = Constants.LIVE;
			}
			
			
			return streamType;
		}
		
}
