package com.hawkins.controller;

import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.annotation.SubscribeMapping;
import org.springframework.mobile.device.Device;
import org.springframework.mobile.device.site.SitePreference;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.hawkins.dmanager.DManagerApp;
import com.hawkins.jobs.DownloadJob;
import com.hawkins.m3u.M3UDownloadItem;
import com.hawkins.m3u.M3UGroup;
import com.hawkins.m3u.M3UGroupList;
import com.hawkins.m3u.M3UItem;
import com.hawkins.m3u.M3UParser;
import com.hawkins.m3u.M3UPlayList;
import com.hawkins.properties.DmProperties;
import com.hawkins.properties.DownloadProperties;
import com.hawkins.service.DownloadService;
import com.hawkins.utils.Constants;
import com.hawkins.utils.MovieDb;
import com.hawkins.utils.Utils;

@Controller
public class DownloadController {

	private static final Logger logger = LogManager.getLogger(DownloadController.class.getName());
	
	private static final String DOWNLOAD = "download";
	private static final String GROUPS = "groups";
	private static final String SELECTEDGROUP = "selectedGroup";
	private static final String SEARCHFILTER = "searchFilter";
	private static final String MOVIEDB = "movieDb";
	private static final String FILMS = "films";
	private static final String JOBLIST = "jobList";
	private static final String SEARCHYEAR = "searchYear";
	private static final String SETTINGS = "settings";
	private static final String ROWS = "rows";
	private static final String STATUS = "status";
	
	@Qualifier("taskExecutor")
	@Autowired
	private ThreadPoolTaskExecutor myExecutor;

	@Autowired
	private SimpMessagingTemplate template;

	private int jobNumber;
	private ArrayList<DownloadJob> myDownloadList = new ArrayList<>(5);

	@Autowired
	private DownloadService myService;
	
	@Autowired
	DownloadController(DownloadService myService) {
		this.myService = myService;
	}

	M3UPlayList playlist = new M3UPlayList();
	M3UGroupList grouplist = new M3UGroupList();
	DownloadProperties downloadProperties = new DownloadProperties();
	DmProperties dmProperties = new DmProperties();
	
	


	@ModelAttribute
	public void initValues(Model model) {

		
		dmProperties = DmProperties.getInstance();;
		downloadProperties = DownloadProperties.getInstance();
		playlist = M3UPlayList.getInstance();
		grouplist = M3UGroupList.getInstance();
		DManagerApp.getInstance();
		

	}

	@GetMapping("/")
	// @RequestMapping(path = "/", method = RequestMethod.GET)
	public String initial(Model model, Device device, SitePreference sitePreference) {

		if (logger.isDebugEnabled()) {
			logger.debug("initial :: Device is {}", device.getDevicePlatform());
			logger.debug("initial :: Mobile is {}", device.isMobile());
			logger.debug("initial :: Normal is {}", device.isNormal());
			logger.debug("initial :: Tablet is {}", device.isTablet());
		}
		model.addAttribute(GROUPS, M3UParser.sortGrouplist(grouplist.getGroupList()));
		model.addAttribute(SELECTEDGROUP, new M3UGroup());
		model.addAttribute(SEARCHFILTER, new String());
		model.addAttribute(MOVIEDB, MovieDb.getInstance());
		
		
		
		return DOWNLOAD;
	}

	@GetMapping("/downloadSubmit")
	public String downloadSubmit(Model model, @ModelAttribute(SELECTEDGROUP) M3UGroup selectedGroup) {
		
		if (logger.isDebugEnabled()) {
			logger.debug(selectedGroup.getName());
		}
		
		if (!selectedGroup.getName().isEmpty()) {
			List<M3UItem> sortedPlayList = playlist.filterPlayList(selectedGroup.getName());
			model.addAttribute(FILMS, sortedPlayList);
		} else {
			model.addAttribute(FILMS, playlist.getPlayList());
		}

		model.addAttribute(MOVIEDB, MovieDb.getInstance());
		model.addAttribute(GROUPS, M3UParser.sortGrouplist(grouplist.getGroupList()));
		return DOWNLOAD;
	}
	
	@GetMapping(value = "/searchPage")
	public String searchPage(Model model) {
		
		model.addAttribute(SELECTEDGROUP, new M3UGroup());
		model.addAttribute(GROUPS, grouplist.getGroupList());
		
		return "search";
	}

	@GetMapping(value = "/searchFilter")
	public String searchFilter(Model model,
			@RequestParam(value = "searchFilter", required = false) String searchFilter) {

		if (logger.isDebugEnabled() ) {
			logger.debug("searchFilter is {}", searchFilter);
		}
		
		if (!searchFilter.isEmpty()) {
			List<M3UItem> sortedList = M3UParser.sortPlaylist(M3UPlayList.getInstance().searchplayList(searchFilter));
			model.addAttribute(FILMS, sortedList);
		}
		
		model.addAttribute(MOVIEDB, MovieDb.getInstance());
		model.addAttribute(SELECTEDGROUP, new M3UGroup());
		model.addAttribute(GROUPS, grouplist.getGroupList());
		return DOWNLOAD;

	}
	
	@GetMapping(value = "/searchPerson")
	public String searchPerson(Model model,
			@RequestParam(value = "searchPerson", required = false) String searchPerson) {

		if (logger.isDebugEnabled()) {
			logger.debug("searchPerson is {}", searchPerson);
		}
		if (!searchPerson.isEmpty()) {
			model.addAttribute(FILMS, M3UParser.sortPlaylist(M3UPlayList.getInstance().searchplayListByActor(searchPerson)));
		}
		
		model.addAttribute(MOVIEDB, MovieDb.getInstance());
		model.addAttribute(SELECTEDGROUP, new M3UGroup());
		model.addAttribute("searchPerson", searchPerson);
		model.addAttribute(GROUPS, grouplist.getGroupList());
		return DOWNLOAD;
		
	}
	
	@GetMapping(value = "/searchYear")
	public String searchYear(Model model,
			@RequestParam(value = "searchYear", required = false) String searchYear) {

		if (logger.isDebugEnabled()) {
			logger.debug("searchYear is {}", searchYear);
		}
		
		if (!searchYear.isEmpty()) {
			List<M3UItem> sortedList = M3UParser.sortPlaylist(M3UPlayList.getInstance().searchplayListByYear(searchYear));
			model.addAttribute(FILMS, sortedList);
		}
		
		model.addAttribute(MOVIEDB, MovieDb.getInstance());
		model.addAttribute(SELECTEDGROUP, new M3UGroup());
		model.addAttribute(SEARCHYEAR, searchYear);
		model.addAttribute(GROUPS, grouplist.getGroupList());
		return DOWNLOAD;
	}

	
	  @RequestMapping("/download") public String downloadForm(Model
	  model, @ModelAttribute(SELECTEDGROUP) M3UGroup selectedGroup) {
	  
	  if (!selectedGroup.getName().isEmpty()) { model.addAttribute(FILMS,
	  playlist.filterPlayList(selectedGroup.getName())); }
	  
	  
	  model.addAttribute(MOVIEDB, MovieDb.getInstance());
	  model.addAttribute(GROUPS, grouplist.getGroupList());
	  
	  return DOWNLOAD; }
	 
	
	@PostMapping(value = "/download", params = { "name" })
	public String download(Model model, @RequestParam String name, HttpServletResponse response,
			HttpServletRequest request) {

		if (logger.isDebugEnabled()) {
			logger.debug("download {}", name);
		}

		try {
			URL url = Utils.getFinalLocation(Utils.getURLFromName(name));

			URLConnection u = url.openConnection();

			long length = 0L;
			try {
				length = Long.parseLong(u.getHeaderField("Content-Length"));
			} catch (NumberFormatException nfe) {
				logger.debug(nfe.getMessage());
			}
			String type = u.getHeaderField("Content-Type");
			String lengthString = Utils.format(length, 2);

			if (logger.isDebugEnabled()) {
				logger.debug("File of type {} is {}", type, lengthString);
			}

			M3UDownloadItem downloadItem = new M3UDownloadItem();
			downloadItem.setUrl(url);
			downloadItem.setName(
					downloadProperties.getDownloadPath() + name + "." + Utils.getFileExtension(url.toString()));
			downloadItem.setFilmName(name);
			downloadItem.setSearchPhrase("");
			downloadItem.setSize(length);

			jobNumber ++;
			DownloadJob downloadJob = new DownloadJob(downloadItem.getUrl().toString(), "Job-" + jobNumber, downloadItem.getName(), template);
			downloadJob.setFileName(name + "." + Utils.getFileExtension(url.toString()));
			downloadJob.setFolder(downloadProperties.getDownloadPath());
			
			myService.doWork(downloadJob);
			
			model.addAttribute(MOVIEDB, MovieDb.getInstance());
			model.addAttribute(FILMS, playlist.getPlayList());
			model.addAttribute(GROUPS, grouplist.getGroupList());
			model.addAttribute(SELECTEDGROUP, new M3UGroup());

		} catch (IOException ioe) {
			logger.info(ioe.getMessage());
		} catch (Exception e) {
			logger.info(e.getMessage());
		}

		return STATUS;
	}



	@GetMapping(value = "/showStatus")
	public String showStatus(Model model) {
		
		model.addAttribute(JOBLIST, this.myDownloadList);
		return STATUS;
	}
	
	@RequestMapping(value = "/status")
	@ResponseBody
	@SubscribeMapping("initial")
	public ArrayList<DownloadJob> fetchStatus() {
		return this.myDownloadList; 
	}
	
	@GetMapping(value = "interrupt", params = { "name" })
	public String interruptJob(Model model, @RequestParam String name) {
		
		DManagerApp.getInstance().pauseDownload(name);
		DownloadJob job = Utils.findJobByName(myDownloadList, name);
		
		if (job != null) job.stop();
		
		return STATUS;
	}
	
	@PostMapping(value = "resumeJobs")
	public String resumeJobs(Model model) {
		
		DManagerApp.getInstance().resumeDownload("123", true);
		return STATUS;
	}
	
	
	
	@GetMapping(value = "pause", params = { "name" })
	public String pauseJob(Model model, @RequestParam String name) {
		
		DManagerApp.getInstance().pauseDownload(name);
		DownloadJob job = Utils.findJobByName(myDownloadList, name);
		
		if (job != null) {
			job.pause();
			job.setState(Constants.PAUSED);
		}
		
		return STATUS;
	}
	
	@PostMapping("/removejobs")
	public String removeJobs() {
		myDownloadList = (ArrayList<DownloadJob>) Utils.removeJobs(myDownloadList);
		return STATUS;
	}
	
	@GetMapping("/settings")
	public String settings (Model model) {
		
		model.addAttribute(SETTINGS, DownloadProperties.getInstance());
		return SETTINGS;
	}
	
	@PostMapping(value = "saveSettings")
	public String saveSettings(Model model, 
			@RequestParam String channels, 
			@RequestParam String fullM3U, 
			@RequestParam String downloadPath,
			@RequestParam String movieDbURL,
			@RequestParam String movieDbAPI,
			@RequestParam String searchMovieURL,
			HttpServletResponse response,
			HttpServletRequest request) {
		
		ArrayList<String> newProperties = new ArrayList<>(Arrays.asList(channels, fullM3U, downloadPath, movieDbURL, movieDbAPI, searchMovieURL));
		
		model.addAttribute(SETTINGS, downloadProperties.updateSettings(newProperties));
		
		model.addAttribute(MOVIEDB, new MovieDb());
		model.addAttribute(SELECTEDGROUP, new M3UGroup());
		model.addAttribute(GROUPS, grouplist.getGroupList());
		
		return "/download";
	}
	
	
	@GetMapping("/group") public String groupForm(Model model) {

		model.addAttribute(ROWS, grouplist.getGroupList()); 
		return "group"; 
	}


	@ModelAttribute("playlist")
	public M3UPlayList populatePlaylist() {

		playlist = M3UPlayList.getInstance();
		return playlist;
	}

	@ModelAttribute("grouplist")
	public M3UGroupList populateGrouplist() {

		grouplist = M3UGroupList.getInstance();
		return grouplist;
	}

}
