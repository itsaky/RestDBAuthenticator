package com.itsaky.restdbauth.library.callbacks;
import java.util.ArrayList;
import com.itsaky.restdbauth.library.User;

public interface GetUserCallback
{
	public void onSuccess(ArrayList<User> users);
	public void onFailure(String message);
}
