package com.krazzzzymonkey.catalyst.managers.accountManager.legacySupport;

public class OldJava implements ILegacyCompat {
	@Override
	public int[] getDate() {
		int[] ret = new int[3];
		ret[0]=0;
		ret[1]=0;
		ret[2]=0;
		return ret;
	}

	@Override
	public String getFormattedDate() {
		return "";
	}

}
