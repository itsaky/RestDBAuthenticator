package com.itsaky.restdbauth.library.callbacks;
import com.itsaky.restdbauth.library.User;

public interface CheckEmailCallback
{
	public void onSuccess(boolean isRegistered, User user);
	public void onFailure(String message);
}
