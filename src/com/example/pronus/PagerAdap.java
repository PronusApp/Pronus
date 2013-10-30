package com.example.pronus;

import java.util.List;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

public class PagerAdap extends FragmentPagerAdapter {

	// fragments to instantiate in the viewpager
	private List<Fragment> fragments;

	// constructor
	public PagerAdap(FragmentManager fm,List<Fragment> fragments) {
		super(fm);
		this.fragments = fragments;
	}

	// return access to fragment from position, required override
	@Override
	public Fragment getItem(int position) {
		return this.fragments.get(position);
	}

	// number of fragments in list, required override
	@Override
	public int getCount() {
		return this.fragments.size();
	}

}