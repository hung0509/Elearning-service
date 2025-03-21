package vn.xuanhung.ELearning_Service.constant;

public abstract class AppConstant {
    public static final String[] URL_PUBLIC = {
            "/auth", "/accounts", "/permissions", "/roles"
    };
    public static final String[] GET_URL_PUBLIC = {
            "accounts/active/**"
    };

    public static final String STATUS_UNACTIVE = "N";
    public static final String STATUS_ACTIVE = "Y";

    public static final class Role{
        public static final String ADMIN = "ADMIN";
        public static final String USER = "USER";
    }

    public static final class Topic{
        public static final String EMAIL_TOPIC = "email_topic";
    }
}