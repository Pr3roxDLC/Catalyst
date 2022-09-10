package com.krazzzzymonkey.catalyst.managers.accountManager;



public class AlreadyLoggedInException extends Exception {
	private static final long serialVersionUID = -7572892045698003265L;

	@Override
	public String getLocalizedMessage(){
		return "You are already logged into that account!";
	}
}
