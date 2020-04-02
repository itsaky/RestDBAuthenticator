package com.itsaky.restdbauth.library.callbacks;
import com.itsaky.restdbauth.library.User;

public interface UpdateUserCallback
{
	public void onSuccess(User user);
	public void onFailure(String message);
}
