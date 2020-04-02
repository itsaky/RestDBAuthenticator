package com.itsaky.restdbauth.library;
import android.os.Parcelable;
import android.os.Parcel;
import java.util.HashMap;

public class User
{
	private String username, email, password, name, userId;
	private boolean isEmailVerified;
	
	public void setUserId(String userId){
		this.userId = userId;
	}
	
	public String getUserId(){
		return userId;
	}

	public void setUsername(String username)
	{
		this.username = username;
	}

	public String getUsername()
	{
		return username;
	}

	public void setEmail(String email)
	{
		this.email = email;
	}

	public String getEmail()
	{
		return email;
	}

	public void setPassword(String password)
	{
		this.password = password;
	}

	public String getPassword()
	{
		return password;
	}

	public void setName(String name)
	{
		this.name = name;
	}

	public String getName()
	{
		return name;
	}

	public void setIsEmailVerified(boolean isEmailVerified)
	{
		this.isEmailVerified = isEmailVerified;
	}

	public boolean isEmailVerified()
	{
		return isEmailVerified;
	}
}
