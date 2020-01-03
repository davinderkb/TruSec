package com.app.trusec;

import android.content.res.Resources;

import java.util.ArrayList;
import java.util.List;


public class NavigationDrawerItem {

	private String title;
	private int imageId;
	public static final String NAV_ITEM_LOGOUT = "Logout";
	public static final String NAV_ITEM_ROSTER = "Roster";
	public static final String NAV_ITEM_LEAVES = "Manage Leaves";
	public static final String NAV_ITEM_PROFILE = "Profile";



	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public int getImageId() {
		return imageId;
	}

	public void setImageId(int imageId) {
		this.imageId = imageId;
	}

	public static List<NavigationDrawerItem> getData() {
		List<NavigationDrawerItem> dataList = new ArrayList<>();

		int[] imageIds = getImages();
		String[] titles = getTitles();

		for (int i = 0; i < titles.length; i++) {
			NavigationDrawerItem navItem = new NavigationDrawerItem();
			navItem.setTitle(titles[i]);
			navItem.setImageId(imageIds[i]);
			dataList.add(navItem);
		}
		return dataList;
	}

	private static int[] getImages() {

		return new int[]{
							R.drawable.ic_assignment,  R.drawable.ic_profile_nav_item, R.drawable.my_leaves_icon, R.drawable.logout
			};
	}

	private static String[] getTitles() {

		return new String[] {
				NAV_ITEM_ROSTER, NAV_ITEM_PROFILE, NAV_ITEM_LEAVES, NAV_ITEM_LOGOUT
		};
	}
}
