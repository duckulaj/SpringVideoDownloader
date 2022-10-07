package com.hawkins.m3u;

import java.util.LinkedList;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import com.hawkins.utils.Constants;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class M3UEdit {

	public static LinkedList<M3UGroup> filterGroups (List<M3UGroup> groups, Predicate<M3UGroup> predicate) {
		
		LinkedList<M3UGroup> list = new LinkedList<M3UGroup>();
		
		list.addAll(groups.stream().filter( predicate ).collect(Collectors.<M3UGroup>toList()));
		return list;
	}
	
	public static Predicate<M3UGroup> ofType(String type) {
		return p -> p.getType().equals(type);
	}
	
	public static LinkedList<M3UGroup> getLiveTVGroups() {
				
		LinkedList<M3UGroup> groups = filterGroups(M3UGroupList.getInstance().getGroupList(), ofType(Constants.LIVE));
		
		log.info("Found {} LiveTV Groups", groups.size());
		
		return groups;
		
	}
	
	
}
