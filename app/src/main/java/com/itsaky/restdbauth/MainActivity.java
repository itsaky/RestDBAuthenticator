package com.itsaky.restdbauth;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import com.itsaky.restdbauth.library.UserManager;
import com.itsaky.restdbauth.library.UserManagerConfig;
import com.itsaky.restdbauth.library.User;
import com.itsaky.restdbauth.library.callbacks.SignUpCallback;
import android.widget.Toast;
import android.widget.TextView;
import com.itsaky.restdbauth.library.callbacks.LoginCallback;
import com.itsaky.restdbauth.library.callbacks.VerifyEmailCallback;
import com.itsaky.restdbauth.library.callbacks.CheckEmailCallback;
import com.itsaky.restdbauth.library.callbacks.DeleteUserCallback;

public class MainActivity extends AppCompatActivity 
{
    private Toolbar toolbar;
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        toolbar = (Toolbar) findViewById(R.id.mainToolbar);

        setSupportActionBar(toolbar);
        UserManagerConfig config = new UserManagerConfig();
		config.setApiKey("e0096b6410bf3629d37e12eafc02cec0c60bb");
		config.setKeyEmail("email");
		config.setKeyIsEmailVerified("isEmailVerified");
		config.setKeyName("name");
		config.setKeyPassword("password");
		config.setKeyUsername("username");
		config.setCompany("AIDEMate");
		config.setEmailSenderName("AIDEMate");
		config.setEmailUrl("https://testdb-b9f9.restdb.io/mail");
		config.setSubjectVerifyEmail("Verify your Email Adress");
		config.setUsersCollectionURL("https://testdb-b9f9.restdb.io/rest/userss");
		config.setVerifyEmailAppName("AIDEMate");

		UserManager.initialize(this, config);
		
		User user = UserManager.getCurrentUser();
    }
	
	public void toast(String s){
		Toast.makeText(this, s, Toast.LENGTH_SHORT).show();
	}
}
