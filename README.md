[![](https://jitpack.io/v/itsaky/RestDBAuthenticator.svg)](https://jitpack.io/#itsaky/RestDBAuthenticator)
# RestDBAuthenticator
This library helps you to authenticate users using restdb.io database.

You will need to setup restdb.io before using this library. Watch this video to learn what & how to setup the restdb.io database.

<a href="http://www.youtube.com/watch?feature=player_embedded&v=_nxRzXg8mmw
" target="_blank"><img src="http://img.youtube.com/vi/_nxRzXg8mmw/0.jpg" 
alt="Setting up the restdb.io database" width="240" height="180" border="10" /></a>

Here is an example video that shows how to implement login and signup using RestDBAuthenticator.

<a href="http://www.youtube.com/watch?feature=player_embedded&v=ADqxtpm4Itg
" target="_blank"><img src="http://img.youtube.com/vi/ADqxtpm4Itg/0.jpg" 
alt="Authentication using RestDBAuthenticator" width="240" height="180" border="10" /></a>
## Download
Add library to your project using gradle

Add this to your project's build.gradle

```
maven { url 'https://jitpack.io' }
```

Add this to module-level build.gradle

```
implementation 'com.github.itsaky:RestDBAuthenticator:<latest-verison>'
```
Replace with the latest release


## Initialize

Add this in onCreate of your Application class.

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

Create a new ```User``` object, pass the values and call ```UserManager.loginUser(Activity, user, LoginCallback);```

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
		
UserManager.logInUser(this, user, new LoginCallback(){

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

Create a new ```User``` object and call ```UserManager.signupUser(Activity, user, SignUpCallback);```

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
		
UserManager.signUpUser(this, user, new SignUpCallback(){

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

You can send an email verification code to the user using ```UserManager.verifyEmail(Activity, User, VerifyEmailCallback);```

The ```User``` object must contain the email address
Ex. You must call ```user.setEmail(String);``` before calling the above method.

```java
User user = new User();
user.setEmail("email");
UserManager.verifyEmail(this, user, new VerifyEmailCallback(){

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

You can update user details using ```UserManager.updateUser(Activity, User, UpdateUserCallback);```

The ```User``` object must contain the userId.

```java
User user = UserManager.getCurrentUser();
user.setUsername("username");
UserManager.updateUser(this, user, new UpdateUserCallback(){

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

Delete a user object using ```UserManager.deleteUser(Activity, User, DeleteUserCallback);```

The ```User``` object must constain the userId.

```java
User user = UserManager.getCurrentUser();
UserManager.deleteUser(this, user, new DeleteUserCallback(){

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

## Check User is Registered or not.

You can check if a user is registered with the email address

```java
UserManager.checkEmailRegistered(this, "email", new CheckEmailCallback(){

		@Override
		public void onSuccess(boolean isRegistered, User registeredUser) {
			//isRegistered is true if the collection contains a jser with the provided email address
			//If the user is registered registeredUser will conatain its properties else it will be null
		}

		@Override
		public void onFailure(String err) {
			//Check err
		}
	});
```

## Get All registered users

Get a list of all registered users

```java
UserManager.getUsers(this, new GetUserCallback(){

		@Override
		public void onSuccess(ArrayList<User> users) {
			//users contains all the registered users
		}

		@Override
		public void onFailure(String err) {
			//check err
		}
	});
```
## Developer

Developed by ```Akash Yadav```
+ [Instagram](http://instagram.com/_mr_developer)
+ [Telegram](http://t.me/itsaky)
