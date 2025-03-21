package vn.xuanhung.ELearning_Service.config;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp;
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.youtube.YouTube;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Collections;

@Configuration
public class GoogleConfig {
    private static final GsonFactory JSON_FACTORY = GsonFactory.getDefaultInstance();

    @Value("${google.client.id}")
    private String CLIENT_ID;

    @Value("${google.client.secret}")
    private String CLIENT_SECRET;

    @Value("${google.auth.scope}")
    private String SCOPE;

    @Value("${google.client.redirect-uri}")
    private String RERIRECT_URI;

    @Bean
    public Credential authorize() throws Exception {
        // Tạo GoogleAuthorizationCodeFlow từ thông tin trong application.yml
        GoogleAuthorizationCodeFlow flow = new GoogleAuthorizationCodeFlow.Builder(
                GoogleNetHttpTransport.newTrustedTransport(),
                JSON_FACTORY,
                CLIENT_ID,
                CLIENT_SECRET,
                Collections.singletonList(SCOPE)
        )
        .setAccessType("offline")
        .build();
        return new AuthorizationCodeInstalledApp(flow, new LocalServerReceiver.Builder()
                .setPort(8080) // Đảm bảo cổng được dùng là chính xác
                .setCallbackPath("/Callback") // Đảm bảo đường dẫn khớp với cấu hình trên Google Cloud
                .build()).authorize("user");
    }

    @Bean
    public YouTube getInstance() throws Exception {
        // Khởi tạo đối tượng YouTube client
        return new YouTube.Builder(
                GoogleNetHttpTransport.newTrustedTransport(),
                GsonFactory.getDefaultInstance(),
                authorize()
        ).setApplicationName("YouTubeUploader").build();
    }
}
