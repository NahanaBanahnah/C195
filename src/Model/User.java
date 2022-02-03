package Model;

/** User Model */
public class User {

    public static int id;
    public static String name;

    /** Creates a new User of user logged in - Main Use is to store global for userID and UserName of logged in
     * @param id int - logged-in user ID
     * @param name String - logged-in user
     */
    public User(int id, String name) {
        User.id = id;
        User.name = name;
    }

    /** get user ID
     *
     * @return user ID
     */
    public static int getId() {
        return id;
    }

    /** get Username
     * @return Username
     */
    public static String getUsername() {
        return name;
    }

    public static void setId(int userId) {
        id = userId;
    }
}
