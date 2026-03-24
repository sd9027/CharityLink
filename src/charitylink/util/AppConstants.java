package charitylink.util;

public class AppConstants {
    public static final String DATA_DIR       = "data/";
    public static final String USERS_FILE     = DATA_DIR + "users.csv";
    public static final String DONATIONS_FILE = DATA_DIR + "donations.csv";

    public static final String[] CAUSES = {
        "Education", "Healthcare", "Food & Water",
        "Disaster Relief", "Animal Welfare", "Environment"
    };

    private AppConstants() {}
}
