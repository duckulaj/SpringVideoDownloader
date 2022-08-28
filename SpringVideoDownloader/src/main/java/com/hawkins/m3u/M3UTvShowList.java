package com.hawkins.m3u;

import java.util.LinkedList;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import com.hawkins.utils.Constants;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class M3UTvShowList {

	private LinkedList<M3UGroup> groups = new LinkedList<M3UGroup>();
	private static M3UTvShowList thisInstance = null;

	public M3UTvShowList() {

		this.groups = filterGroups(M3UGroupList.getInstance().getGroupList(), ofType(Constants.LIVE));

	}
	public static synchronized M3UTvShowList getInstance()
	{
		log.debug("Requesting M3UTvShowList instance");

		if (M3UTvShowList.thisInstance == null)
		{
			M3UTvShowList.thisInstance = new M3UTvShowList();
		}

		return M3UTvShowList.thisInstance;
	}

	private static Predicate<M3UGroup> ofType(String type) {
		return p -> p.getType().equals(type);
	}

	private static LinkedList<M3UGroup> filterGroups (List<M3UGroup> groups, Predicate<M3UGroup> predicate) {

		LinkedList<M3UGroup> list = new LinkedList<M3UGroup>();

		list.addAll(groups.stream().filter( predicate ).collect(Collectors.<M3UGroup>toList()));
		return list;
	}
	
	public LinkedList<M3UGroup> getGroupList() {
		return this.groups;
	}


}
