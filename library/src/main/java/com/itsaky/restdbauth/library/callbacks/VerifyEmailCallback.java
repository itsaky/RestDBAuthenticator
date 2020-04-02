package com.itsaky.restdbauth.library.callbacks;

public interface VerifyEmailCallback
{
	public void onSuccess(int OTP);
	public void onFailure(String message);
}
