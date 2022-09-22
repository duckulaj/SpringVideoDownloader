package com.hawkins.utils;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import com.hawkins.properties.DownloadProperties;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class Xteve {

	public static void updateXteve () {
		
		DownloadProperties downloadProperties = DownloadProperties.getInstance();
		
		// downloadProperties.setXteveInstalled(true);
		
		if (downloadProperties.isXteveInstalled()) {
			
			String username = downloadProperties.getXteveUser();
			String xteveUrl = downloadProperties.getXteveUrl();
			
			// String username = "duckulaj";
			String password = "ja9juja9ju";
			// String xteveUrl = "http://localhost:34400/api/";
			
			RestTemplate template = new RestTemplate();
			
			// create headers
			HttpHeaders headers = new HttpHeaders();
			// set `content-type` header
			headers.setContentType(MediaType.APPLICATION_JSON);
			// set `accept` header
			headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
			
			// request body parameters
			Map<String, Object> map = new HashMap<>();
			map.put("cmd", "login");
			map.put("username", username);
			map.put("password", password);
			
			// build the request
			HttpEntity<Map<String, Object>> entity = new HttpEntity<>(map, headers);

			// send POST request
			ResponseEntity<String> response = template.postForEntity(xteveUrl, entity, String.class);

			// check response
			if (response.getStatusCode() == HttpStatus.OK) {
			    System.out.println("Request Successful");
			    System.out.println(response.getBody());
			    
			    map.replace("cmd", "update.m3u");
			    response = template.postForEntity(xteveUrl, entity, String.class);
			    log.info("Updated xteve m3u status {}", response.getStatusCode());
			    log.info(response.getBody());
			    
			    map.replace("cmd", "update.xepg");
			    response = template.postForEntity(xteveUrl, entity, String.class);
			    log.info("Updated xteve xepg status {}", response.getStatusCode());
			    log.info(response.getBody());
			    
			} else {
			    System.out.println("Request Failed");
			    System.out.println(response.getStatusCode());
			}
			
			
		}
	}
}
