package project.cse5236.parleypirate;

public class UserDatabaseId {
    private static volatile UserDatabaseId ourInstance = null;
    private static String dbId;

    public static String getDbId() {
        return dbId;
    }

    public static void setDbId(String dbId) {
        UserDatabaseId.dbId = dbId;
    }

    /**
     * getInstance method for the CurrentUserSingleton.  Lazy initialized because the object won't
     * be created until the user is logged in.
     * @return the singleton user
     */
    public static UserDatabaseId getInstance() {
        return ourInstance;
    }

    private UserDatabaseId() {
    }


}
