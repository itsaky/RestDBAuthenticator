[![](https://jitpack.io/v/itsaky/RestDBAuthenticator.svg)](https://jitpack.io/#itsaky/RestDBAuthenticator)
# RestDBAuthenticator
This library helps you to authenticate users using restdb.io database.

You will need to setup restdb.io before using this library. Watch [this](http://youtube.com/itsaky) video to learn what & how to setup.

## Download
Add library to your project using gradle

Add this to project-level build.gradle
```
maven { url 'https://jitpack.io' }
```
Add this to module-level build.gradle

```
implementation 'com.github.itsaky:RestDBAuthenticator:<latest-verison>'
```
Replace with the latest release


## Initialize

Use this in the Main Activity of the Application.

```java
UserManagerConfig c = new UserManagerConfig();
c.setApiKey("<API Key here>");
c.setCompany("AIDEMate"); //Will be used in the email that will be sent to user to verify his/her email
c.setEmailSenderName("AIDEMate");
c.setEmailUrl("https://<database-name>.restdb.io/mail"); //replace <database-name> with your database name
c.setKeyEmail("email"); //key that is used to store user's email
c.setKeyIsEmailVerified("isEmailVerified"); //key to check if user's email is verified
c.setKeyName("name"); //key for user's name
c.setKeyPassword("password"); //key for user's password
c.setKeyUsername("username"); //key for user's username
c.setSubjectVerifyEmail("Verify your email for AIDEMate"); //subject for the email verification email
c.setUsersCollectionURL("https://<dabase-name>.restdb.io/rest/<collection-name>"); //replace <database-name> and <collection-name>
c.setVerifyEmailAppName("AIDEMate"); //Verification Email Footer (The <app-name> Team)
		
UserManager.initialize(this, c); //Finally, initialize the UserManager
```

## Get Current User
Use ```UserManager.getCurrentUser();``` to get the currently logged in user.

This returns null if user is not logged in.

## Login user

Create a new ```User``` object, pass the values and call ```UserManager.loginUser(user, LoginCallback);```

```java
User user = new User();
user.setEmail("email");
user.setUsername("email");
user.setPassword("password");
		
/*
		
The email and username fields must not be empty while loggin in
		
If user only enters email then use this :
user.setEmail("<email>");
user.setUsername("<email>");
		 
If user only enters username then use this :
user.setEmail("<username>");
user.setUsername("<username>");
		 
You must take care of this
		
*/
		
UserManager.logInUser(user, new LoginCallback(){

		@Override
		public void onSuccess() {
			//User logged in
			//get the logged in user using UserManager.getCurrentUser();
		}

		@Override
		public void onFailure(String errMessage) {
			//Something went wrong
			//Check the errMessage
		}
	});
```

## SignUp a User

Create a new ```User``` object and call ```UserManager.signupUser(user, SignUpCallback);```

```java
User user = new User();
user.setEmail("<email>");
user.setIsEmailVerified(false);
user.setName("name");
user.setPassword("pass");
user.setUsername("username");
		
/*
		
user.setUserId(String); is necessary only while deleting a user or updating a user
		
*/
		
UserManager.signUpUser(user, new SignUpCallback(){

		@Override
		public void onSuccess(User newUser) {
			//User created
			//get its property using newUser
		}

		@Override
		public void onFailure(String errMessage, String response) {
			//Something went wrong
			//look for errMessage
			//response is the response that you got from restdb.io
		}
	});
```

## Send Email Verification Code

You can send an email verification code to the user using ```UserManager.verifyEmail(User, VerifyEmailCallback);```

The ```User``` object must contain the email address
Ex. You must call ```user.setEmail(String);``` before calling the above method.

```java
User user = new User();
user.setEmail("email");
UserManager.verifyEmail(user, new VerifyEmailCallback(){

		@Override
		public void onSuccess(int otp) {
			//tell the user to enter otp and verify it with this 'otp'
		}

		@Override
		public void onFailure(String errMessage) {
			//check errMessage
		}
	});
```

## Update a user

You can update user details using ```UserManager.updateUser(User, UpdateUserCallback);```

The ```User``` object must contain the userId.

```java
User user = UserManager.getCurrentUser();
user.setUsername("username");
UserManager.updateUser(user, new UpdateUserCallback(){

		@Override
		public void onSuccess(User updatedUser) {
			//User updated
			//updatedUser has been logged in
		}

		@Override
		public void onFailure(String errMessage) {
			//check errMessage
		}
	});
```

## Delete a user

Delete a user object using ```UserManager.deleteUser(User, DeleteUserCallback);```

The ```User``` object must constain the userId.

```java
User user = UserManager.getCurrentUser();
UserManager.deleteUser(user, new DeleteUserCallback(){

		@Override
		public void onSuccess() {
			//User deleted
		}

		@Override
		public void onFailure(String errMessage) {
			//Check errMessage
		}
    });
```
