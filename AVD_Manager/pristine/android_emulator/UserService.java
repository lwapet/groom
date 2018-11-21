package fr.groom.android_emulator;

public interface UserService {
	User createUser(String userName, String firstName, String password);
	User createUser(String userName, String password);
	User findUserByUserName(String userName);
	int getUserCount();
}
