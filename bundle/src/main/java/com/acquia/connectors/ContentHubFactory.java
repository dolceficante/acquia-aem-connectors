package com.acquia.connectors;

import com.acquia.connectors.impl.ContentHubServiceImpl;

public class ContentHubFactory {

	public static ContentHubService getInstance(){
		return new ContentHubServiceImpl();
	}
	
}
