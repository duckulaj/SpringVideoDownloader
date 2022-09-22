package com.hawkins.controller;

import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.hawkins.utils.Constants;
import com.hawkins.utils.Xteve;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
public class ExternalAppsController {
	
	@GetMapping("/xteve")
	public String updateXteve (Model model) {
		
		Xteve.updateXteve();
		
		return Constants.DOWNLOAD; 
	}

}
