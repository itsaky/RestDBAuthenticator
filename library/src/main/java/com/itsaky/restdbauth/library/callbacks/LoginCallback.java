package com.itsaky.restdbauth.library.callbacks;

public interface LoginCallback
{
	public void onSuccess();
	public void onFailure(String message);
}
