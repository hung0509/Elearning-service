package vn.xuanhung.ELearning_Service.constant;

public abstract class AppConstant {
    public static final String[] URL_PUBLIC = {
            "/auth", "/account", "/permissions", "/roles"
    };

    public static final class Role{
        public static final String ADMIN = "ADMIN";
        public static final String USER = "USER";
    }
}