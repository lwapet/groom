package fr.groom.android_emulator;

public class UserServiceImpl
		implements UserService {

	public User createUser(String userName, String firstName, String password) {
		User user = new User();
		user.setUserName(userName);
		user.setFirstName(firstName);
		user.setPassword(password);
//		database.saveUser(user);
		return user;
	}

	public User createUser(String userName, String password) {
		return this.createUser(userName, null, password);
	}

	public User findUserByUserName(String userName) {
		return null;
//		return database.findUserByUserName(userName);
	}

	public int getUserCount() {
		return 0;
	}

}
