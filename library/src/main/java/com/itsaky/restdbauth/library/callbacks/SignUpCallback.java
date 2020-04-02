package com.itsaky.restdbauth.library.callbacks;
import com.itsaky.restdbauth.library.User;

public interface SignUpCallback
{
	public void onSuccess(User user);
	public void onFailure(String message, String errorContents);
}
