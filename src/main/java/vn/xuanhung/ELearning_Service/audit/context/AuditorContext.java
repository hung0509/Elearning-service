package vn.xuanhung.ELearning_Service.audit.context;

public class AuditorContext {
    private static final ThreadLocal<String> currentUser = new ThreadLocal<>();

    public static void setCurrentUser(String username) {
        currentUser.set(username);
    }

    public static String getCurrentUser() {
        return currentUser.get();
    }

    public static void clear() {
        currentUser.remove();
    }
}
