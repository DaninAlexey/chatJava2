package Auth;

import java.util.Set;

public class AuthService {

    private  static final Set<User> USERS = Set.of(
            new User("LOGIN1", "PAS1", "userName1"),
            new User("LOGIN2", "PAS2", "userName2"),
            new User ("LOGIN3", "PAS3", "userName3")
    );

    public User getUserByUserName(String name)
    {
        for (User user : USERS) {
            if(user.getUserName().equals(name))
            {
                return user;
            }
        }
        return null;
    }

    public User getUserByLoginAndPassword(String login, String password)
    {
        User requiredUser = new User(login, password);
        for (User user : USERS) {
            if(requiredUser.equals(user))
            {
                return user;
            }
        }
        return null;
    }

}