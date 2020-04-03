package com.itsaky.restdbauth.library;
import android.app.Activity;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import com.itsaky.restdbauth.library.User;
import com.itsaky.restdbauth.library.callbacks.DeleteUserCallback;
import com.itsaky.restdbauth.library.callbacks.LoginCallback;
import com.itsaky.restdbauth.library.callbacks.SignUpCallback;
import com.itsaky.restdbauth.library.callbacks.UpdateUserCallback;
import com.itsaky.restdbauth.library.callbacks.VerifyEmailCallback;
import com.itsaky.restdbauth.library.utils.RequestNetwork;
import com.itsaky.restdbauth.library.utils.RequestNetworkController;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import com.itsaky.restdbauth.library.callbacks.CheckEmailCallback;
import com.itsaky.restdbauth.library.callbacks.GetUserCallback;
import android.content.Context;

public class UserManager
{
	private static SharedPreferences sp;
	private static String username, email, password, name, uploaderId;
	private static boolean isLoggedIn = false;
	private static User user;
	private static boolean isEmailVerified = false;
	private static Context con;

	private static String KEY_EMAIL = "email";
	private static String KEY_USERNAME = "username";
	private static String KEY_PASSWORD = "password";
	private static String KEY_NAME = "name";
	private static String KEY_EMAIL_VERIFIED = "isEmailVerified";
	private static final String KEY_UPLOADER_ID = "_id";

	private static UserManagerConfig config;


	/* Intialize the UserManager with the activity and the UserManagerConfig */
	public static void initialize(Context ac, UserManagerConfig c){
		con = ac;
		config = c;
		sp = PreferenceManager.getDefaultSharedPreferences(con);
		KEY_EMAIL = config.getKeyEmail();
		KEY_USERNAME = config.getKeyUsername();
		KEY_NAME = config.getKeyName();
		KEY_PASSWORD = config.getKeyPassword();
		KEY_EMAIL_VERIFIED = config.getKeyIsEmailVerified();
	}

	/* Returns null if the the user is not logged in */
	public static User getCurrentUser(){
		username = sp.getString(KEY_USERNAME, null);
		email = sp.getString(KEY_EMAIL, null);
		password = sp.getString(KEY_PASSWORD, null);
		name = sp.getString(KEY_NAME, null);
		uploaderId = sp.getString(KEY_UPLOADER_ID, null);
		isLoggedIn = username != null?true:false;
		isEmailVerified = sp.getBoolean(KEY_EMAIL_VERIFIED, true);
		if(isLoggedIn){
			user = new User();
			user.setUsername(username);
			user.setEmail(email);
			user.setPassword(password);
			user.setName(name);
			user.setIsEmailVerified(isEmailVerified);
			user.setUserId(uploaderId);
		}

		return user;
	}

	public static User signUpUser(final Activity a, final User u, final SignUpCallback callback){
		new Thread(new Runnable(){

				@Override
				public void run()
				{
					try {
						HashMap<String, Object> j = new HashMap<>();
						j.put(KEY_USERNAME, u.getUsername());
						j.put(KEY_EMAIL, u.getEmail());
						j.put(KEY_PASSWORD, u.getPassword());
						j.put(KEY_NAME, u.getName());
						j.put(KEY_EMAIL_VERIFIED, u.isEmailVerified());
						HashMap<String, Object> k = new HashMap<>();
						k.put("x-apikey", config.getApiKey());

						RequestNetwork n = new RequestNetwork(a);
						n.setHeaders(k);
						n.setParams(j, RequestNetworkController.REQUEST_BODY);
						n.startRequestNetwork(RequestNetworkController.POST, config.getUsersCollectionURL(), "AIDEMate", new RequestNetwork.RequestListener(){

								@Override
								public void onResponse(String tag, String response, int code)
								{
									try {
										JSONObject o = new JSONObject(response);
										if(o.has(KEY_USERNAME)){
											user = new User();
											user.setUserId(o.getString("_id"));
											user.setEmail(o.getString(KEY_EMAIL));
											user.setUsername(o.getString(KEY_USERNAME));
											user.setPassword(o.getString(KEY_PASSWORD));
											user.setIsEmailVerified(o.getBoolean(KEY_EMAIL_VERIFIED));
											user.setName(o.getString(KEY_NAME));

											callback.onSuccess(user);
										} else {
											if(o.has("message") && o.has("list")){
												JSONArray a = o.getJSONArray("list");
												JSONObject ob = a.getJSONObject(0);
												String f = ob.getString("field").toUpperCase();
												String rea = ob.getJSONArray("message").getString(0);
												callback.onFailure(f + " " + rea, response);
											} else {
												callback.onFailure("Unexpected error occured", response);
											}
										}
									} catch (Exception e){
										callback.onFailure(e.getMessage(), response);
									}
								}

								@Override
								public void onErrorResponse(String tag, String message)
								{
									callback.onFailure(message, null);
								}
							});

					} catch (Exception e){
						callback.onFailure(e.getMessage(), null);
					}
				}
			}).start();

		return user;
	}

	public static User logInUser(final Activity a, final User u, final LoginCallback callback){
		new Thread(new Runnable(){

				@Override
				public void run()
				{
					if(u != null){
						HashMap<String, Object> k = new HashMap<>();
						k.put("x-apikey", config.getApiKey());

						RequestNetwork n = new RequestNetwork(a);
						n.setHeaders(k);

						n.startRequestNetwork(RequestNetworkController.GET, config.getUsersCollectionURL(), "AIDEMate", new RequestNetwork.RequestListener(){

								@Override
								public void onResponse(String tag, String response, int code)
								{
									try {

										JSONArray array = new JSONArray(response);

										if(array.length() > 0){
											JSONObject userObject = null;
											for(int i =0;i<array.length();i++){
												JSONObject o = array.getJSONObject(i);
												if(o.has(KEY_USERNAME)){
													if(o.getString(KEY_USERNAME).equals(u.getUsername())){
														userObject = o;
													} else if(o.getString(KEY_EMAIL).equals(u.getEmail())){
														userObject = o;
													}
												} else {
													callback.onFailure("Invalid response from server");
												}
											}

											if(userObject != null){
												if(userObject.getString(KEY_USERNAME).equals(u.getUsername()) && userObject.getString(KEY_PASSWORD).equals(u.getPassword())){
													user = new User();
													user.setUserId(userObject.getString("_id"));
													user.setEmail(userObject.getString(KEY_EMAIL));
													user.setIsEmailVerified(userObject.getBoolean(KEY_EMAIL_VERIFIED));
													user.setUsername(userObject.getString(KEY_USERNAME));
													user.setPassword(userObject.getString(KEY_PASSWORD));
													user.setName(userObject.getString(KEY_NAME));
													if(user.isEmailVerified()){
														loginUser(user);
														callback.onSuccess();
													} else {
														callback.onFailure("Email not verified!");
													}
												} else if(userObject.getString(KEY_EMAIL).equals(u.getEmail()) && userObject.getString(KEY_PASSWORD).equals(u.getPassword())) {
													//callback.onSuccess();
													user = new User();
													user.setUserId(userObject.getString("_id"));
													user.setEmail(userObject.getString(KEY_EMAIL));
													user.setIsEmailVerified(userObject.getBoolean(KEY_EMAIL_VERIFIED));
													user.setUsername(userObject.getString(KEY_USERNAME));
													user.setPassword(userObject.getString(KEY_PASSWORD));
													user.setName(userObject.getString(KEY_NAME));
													if(user.isEmailVerified()){
														loginUser(user);
														callback.onSuccess();
													} else {
														callback.onFailure("Email not verified!");
													}
												} else {
													callback.onFailure("Invalid username/password");
												}
											} else {
												callback.onFailure("User doesn't exist");
											}
										} else {
											callback.onFailure("No response from server");
										}

									} catch (Exception e){
										callback.onFailure("Something went wrong");
									}
								}

								@Override
								public void onErrorResponse(String tag, String message)
								{
									callback.onFailure(message);
								}
							});
					} else {
						user = null;
						callback.onFailure("Cannot proceed without credentials");
					}
				}
			}).start();
		return user;
	}

	public static void verifyEmail(final Activity a, final User u, final VerifyEmailCallback callback){
		verifyUser(a, u, callback);
	}

	private static void loginUser(final User u){
		//sp = a.getSharedPreferences("user", a.MODE_PRIVATE);
		SharedPreferences.Editor edit = sp.edit();
		edit.putBoolean(KEY_EMAIL_VERIFIED, u.isEmailVerified());
		edit.putString(KEY_PASSWORD, u.getPassword());
		edit.putString(KEY_EMAIL, u.getEmail());
		edit.putString(KEY_NAME, u.getName());
		edit.putString(KEY_USERNAME, u.getUsername());
		edit.putString(KEY_UPLOADER_ID, u.getUserId());
		edit.commit();
	}

	public static void logoutUser(){
		//sp = a.getSharedPreferences("user", a.MODE_PRIVATE);
		SharedPreferences.Editor edit = sp.edit();
		edit.putBoolean(KEY_EMAIL_VERIFIED, false);
		edit.putString(KEY_PASSWORD, null);
		edit.putString(KEY_EMAIL, null);
		edit.putString(KEY_NAME, null);
		edit.putString(KEY_USERNAME, null);
		edit.putString(KEY_UPLOADER_ID, null);
		edit.commit();
//		initialize(a);
	}

	private static int getOtp(){
		int otp = 0;
		Random rnd = new Random();
		otp = 100000 + rnd.nextInt(900000);
		return otp;
	}

	public static User updateUser(final Activity a, final User u, final UpdateUserCallback callback){
		HashMap<String, Object> k = new HashMap<>();
		k.put("x-apikey", config.getApiKey());

		HashMap<String, Object> l = new HashMap<>();
		l.put(KEY_USERNAME, u.getUsername());
		l.put(KEY_EMAIL, u.getEmail());
		l.put(KEY_EMAIL_VERIFIED, u.isEmailVerified());
		l.put(KEY_PASSWORD, u.getPassword());
		l.put(KEY_NAME, u.getName());
		RequestNetwork n = new RequestNetwork(a);
		n.setHeaders(k);
		n.setParams(l, RequestNetworkController.REQUEST_BODY);

		final String URL = config.getUsersCollectionURL() + "/" + u.getUserId();

		n.startRequestNetwork(RequestNetworkController.PUT, URL, "AIDEMate", new RequestNetwork.RequestListener(){

				@Override
				public void onResponse(String tag, String response, int code)
				{
					if(code != 500){
						try {
							JSONObject o = new JSONObject(response);
							if(o.has(KEY_USERNAME)){
								user = new User();
								user.setUserId(o.getString("_id"));
								user.setEmail(o.getString(KEY_EMAIL));
								user.setUsername(o.getString(KEY_USERNAME));
								user.setPassword(o.getString(KEY_PASSWORD));
								user.setIsEmailVerified(o.getBoolean(KEY_EMAIL_VERIFIED));
								user.setName(o.getString(KEY_NAME));
								loginUser(user);
								callback.onSuccess(user);
							} else {
								if(o.has("message") && o.has("list")){
									JSONArray a = o.getJSONArray("list");
									JSONObject ob = a.getJSONObject(0);
									String f = ob.getString("field").toUpperCase();
									String rea = ob.getJSONArray("message").getString(0);
									callback.onFailure(f + " " + rea);
								} else {
									callback.onFailure("Unexpected error occured");
								}
							}
						} catch (Exception e){
							callback.onFailure("Something went wrong");
						}
					} else {
						callback.onFailure("Invalid response from server. Contact developer");
					}
				}

				@Override
				public void onErrorResponse(String tag, String message)
				{
					callback.onFailure(message);
				}
			});
		return user;
	}

	private static void verifyUser(final Activity a, final User u, final VerifyEmailCallback callback){
		HashMap<String, Object> k = new HashMap<>();
		k.put("x-apikey", config.getApiKey());

		final int OTP = getOtp();
		final String S_OTP = String.valueOf(OTP);

		HashMap<String, Object> l = new HashMap<>();
		l.put("to", u.getEmail());
		l.put("sendername", config.getEmailSenderName());
		l.put("company", config.getCompany());
		l.put("subject", config.getSubjectVerifyEmail());
		l.put("html", config.getVerifyEmailContents().replace("$otp$", S_OTP).replace("$_appname_$", config.getVerifyEmailAppName()));

		RequestNetwork n = new RequestNetwork(a);
		n.setHeaders(k);
		n.setParams(l, RequestNetworkController.REQUEST_BODY);

		n.startRequestNetwork(RequestNetworkController.POST, config.getEmailUrl(), "AIDEMate", new RequestNetwork.RequestListener(){

				@Override
				public void onResponse(String tag, String response, int code)
				{
					try {
						JSONObject o = new JSONObject(response);
						JSONArray a = o.getJSONArray("result");
						for(int i = 0;i<a.length();i++){
							JSONObject ob = a.getJSONObject(i);
							if(ob.has("email_outbound_id")){
								callback.onSuccess(OTP);
							} else {
								callback.onFailure("Failed to send verification email");
							}
						}
					} catch (Exception e){
						callback.onFailure("Failed to send email");
					}
				}

				@Override
				public void onErrorResponse(String tag, String message)
				{
					callback.onFailure(message);
				}
			});
	}

	public static void getUsers(final Activity a, final GetUserCallback callback){
		HashMap<String, Object> k = new HashMap<>();
		k.put("x-apikey", config.getApiKey());

		RequestNetwork n = new RequestNetwork(a);
		n.setHeaders(k);

		n.startRequestNetwork(RequestNetworkController.GET, config.getUsersCollectionURL(), "AIDEMate", new RequestNetwork.RequestListener(){

				@Override
				public void onErrorResponse(String tag, String message)
				{
					callback.onFailure(message);
				}


				@Override
				public void onResponse(String tag, String response, int code)
				{
					try {
						JSONArray a = new JSONArray(response);
						if(a.length() > 0){
							final ArrayList<User> users = new ArrayList<>();
							for(int i = 0; i<a.length();i++){
								JSONObject o = a.getJSONObject(i);
								user = new User();
								user.setUserId(o.getString("_id"));
								user.setEmail(o.getString(KEY_EMAIL));
								user.setUsername(o.getString(KEY_USERNAME));
								user.setPassword(o.getString(KEY_PASSWORD));
								user.setIsEmailVerified(o.getBoolean(KEY_EMAIL_VERIFIED));
								user.setName(o.getString(KEY_NAME));
								if(!users.contains(user)){
									users.add(user);
								}
							}
							callback.onSuccess(users);
						} else {
							callback.onFailure("No users found");
						}
					} catch (Exception e) {
						callback.onFailure(e.getMessage());
					}
				}
			});
	}

	public static void deleteUser(final Activity a, final User u, final DeleteUserCallback callback){
		new Thread(new Runnable(){

				@Override
				public void run()
				{
					HashMap<String, Object> k = new HashMap<>();
					k.put("x-apikey", config.getApiKey());

					RequestNetwork n = new RequestNetwork(a);
					n.setHeaders(k);

					final String URL = config.getUsersCollectionURL() + "/" + u.getUserId();

					n.startRequestNetwork(RequestNetworkController.DELETE, URL, "AIDEMate", new RequestNetwork.RequestListener(){

							@Override
							public void onResponse(String tag, String response, int code)
							{
								callback.onSuccess();
							}

							@Override
							public void onErrorResponse(String tag, String message)
							{
								callback.onFailure("");
							}
						});
				}
			}).start();
	}


	public static void checkEmailRegistered(final Activity a, final String email, final CheckEmailCallback callback){
		HashMap<String, Object> k = new HashMap<>();
		k.put("x-apikey", config.getApiKey());

		RequestNetwork n = new RequestNetwork(a);
		n.setHeaders(k);

		n.startRequestNetwork(RequestNetworkController.GET, config.getUsersCollectionURL(), "Tag", new RequestNetwork.RequestListener(){

				@Override
				public void onResponse(String tag, String response, int responseCode)
				{
					try {

						JSONArray array = new JSONArray(response);
						if(array.length() > 0){
							JSONObject obj = null;
							for(int i=0;i<array.length();i++){
								JSONObject obj2 = array.getJSONObject(i);
								if(obj2.has(KEY_EMAIL)){
									if(obj2.getString(KEY_EMAIL).equals(email)){
										obj = obj2;
									}
								}
							}

							if(obj != null){
								User u = new User();
								u.setEmail(obj.getString(KEY_EMAIL));
								u.setIsEmailVerified(obj.getBoolean(KEY_EMAIL_VERIFIED));
								u.setName(obj.getString(KEY_NAME));
								u.setPassword(obj.getString(KEY_PASSWORD));
								u.setUserId(obj.getString(KEY_UPLOADER_ID));
								u.setUsername(obj.getString(KEY_USERNAME));
								callback.onSuccess(true, u);
							} else {
								callback.onFailure("Email is not registered with us.");
							}
						} else {
							callback.onFailure("Email is not registered with us");
						}

					} catch (Exception e){
						callback.onFailure("Invalid response from server");
					}
				}

				@Override
				public void onErrorResponse(String tag, String message)
				{
					callback.onFailure(message);
				}
			});
	}
}
