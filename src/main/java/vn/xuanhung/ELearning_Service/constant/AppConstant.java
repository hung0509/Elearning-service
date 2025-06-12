package vn.xuanhung.ELearning_Service.constant;

public abstract class AppConstant {
    public static final String STATUS_UNACTIVE = "N";
    public static final String STATUS_ACTIVE = "Y";
    public static final String COMPLETE = "COM";
    public static final String INCOMPLETE = "UNCO";
    public static final String REGISTER = "REG";
    public static final String NOATTEND = "NOAT";
    public static final String STATUS_PENDING = "PENDING";

    public static final String[] URL_PUBLIC = {
            "/auth", "/accounts", "/permissions", "/roles", "/auth/logout",
            "/auth/refresh", "auth/outbound/authentication", "/auth/reset"
    };
    public static final String[] GET_URL_PUBLIC = {
            "/accounts/active/**", "/articles/**", "/courses/**", "/payments/**", "/ws-chat/**"
    };

    public static final class Role{
        public static final String ADMIN = "ADMIN";
        public static final String USER = "USER";
    }

    public static final class Topic{
        public static final String EMAIL_TOPIC = "email_topic";
        public static final String VIDEO_TOPIC = "video_topic";
        public static final String COMMENT_TOPIC = "comment_topic";
        public static final String WRITE_LOG = "write_log";
        public static final String USER_CACHE_UPDATE_EVENT = "user-info-cache-topic";
        public static final String ARTICLE_UPDATE_EVENT = "article-cache-topic";
        public static final String COURSE_UPDATE_EVENT = "course-cache-topic";
        public static final String COURSE_SAVE_EVENT = "course-all-cache-topic";
    }

    public static final class Level{
        public static final String BEGINNER = "BEGINNER";
        public static final String INTERMEDIATE = "INTERMEDIATE";
        public static final String EXPERT = "EXPERT";
    }

    public static final class PREFIX{
        public static final String USER_INFO = "user";
        public static final String ARTICLE = "article:user";
        public static final String COURSE_DETAIL = "course:detail";
        public static final String QUIZ = "quiz";
     }

    public static final class ACTION{
        public static final String INVALIDATE = "Invalidate";
        public static final String REBUILD = "Rebuild";
    }

    public static final class AUDIT_STATE{
        public static final String CREATE = "CREATE";
        public static final String DELETE = "DELETE";
        public static final String EDIT = "EDIT";
    }
}