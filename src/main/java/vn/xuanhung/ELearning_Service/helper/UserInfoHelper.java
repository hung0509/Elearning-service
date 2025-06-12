package vn.xuanhung.ELearning_Service.helper;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.ui.ModelMap;
import vn.xuanhung.ELearning_Service.common.ParseHelper;
import vn.xuanhung.ELearning_Service.dto.request.ArticleUserViewRequest;
import vn.xuanhung.ELearning_Service.dto.response.ArticleUserViewResponse;
import vn.xuanhung.ELearning_Service.dto.response.CertificateResponse;
import vn.xuanhung.ELearning_Service.dto.response.CourseHeaderViewResponse;
import vn.xuanhung.ELearning_Service.dto.response.UserInfoResponse;
import vn.xuanhung.ELearning_Service.entity.UserCertificate;
import vn.xuanhung.ELearning_Service.entity.UserInfo;
import vn.xuanhung.ELearning_Service.entity.view.ArticleUserView;
import vn.xuanhung.ELearning_Service.entity.view.CourseRegisterView;
import vn.xuanhung.ELearning_Service.exception.AppException;
import vn.xuanhung.ELearning_Service.exception.ErrorCode;
import vn.xuanhung.ELearning_Service.repository.UserCertificateRepository;
import vn.xuanhung.ELearning_Service.repository.UserInfoRepository;
import vn.xuanhung.ELearning_Service.repository.view.ArticleUserViewRepository;
import vn.xuanhung.ELearning_Service.repository.view.CourseRegisterViewRepository;
import vn.xuanhung.ELearning_Service.specification.ArticleUserViewSpecification;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class UserInfoHelper {
    UserInfoRepository userInfoRepository;
    ArticleUserViewRepository articleUserViewRepository;
    CourseRegisterViewRepository courseRegisterViewRepository;
    UserCertificateRepository userCertificateRepository;

    ModelMapper modelMapper;
    JdbcTemplate jdbcTemplate;

    public UserInfoResponse buildUserInfoResponse(Integer id){
        UserInfo userInfo = userInfoRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXIST));
        UserInfoResponse userInfoResponse =  modelMapper.map(userInfo, UserInfoResponse.class);

        //Get article have created by UserId
        Pageable pageable = PageRequest.of(0, 100,
                Sort.by(Sort.Direction.DESC, "createdAt"));
        ArticleUserViewRequest req = ArticleUserViewRequest.builder()
                .instructorId(id)
                .build();
        Specification<ArticleUserView> spec = ArticleUserViewSpecification.getSpecification(req);
        Page<ArticleUserView> page = articleUserViewRepository.findAll(spec, pageable);

        userInfoResponse.setArticles(page.getContent().stream()
                .map(item -> modelMapper.map(item, ArticleUserViewResponse.class)).toList());

        //Get courses have registered by UserId
        List<CourseRegisterView> courseRegisterView = courseRegisterViewRepository.findAllByUserId(id);

        userInfoResponse.setCourses(courseRegisterView.stream()
                .map(item -> modelMapper.map(item, CourseHeaderViewResponse.class)).toList());

        //Get certificate had achieved
        List<UserCertificate> userCertificates = userCertificateRepository.findAllByUserId(id);
        userInfoResponse.setCertificates(userCertificates.stream()
                .map(item -> modelMapper.map(item, CertificateResponse.class)).toList());

        StringBuilder sql = new StringBuilder("select\n" +
                "dce.*\n" +
                "from d_user_course duc\n" +
                "left join d_course dc on duc.course_id = dc.course_id\n" +
                "left join d_certificate dce on dc.certificate_id = dce.certificate_id\n" +
                "where duc.status = 'COM' and duc.user_id = ?;");

        List<Map<String, Object>> list1 = jdbcTemplate.queryForList(sql.toString(), id);

        List<CertificateResponse> certificateResponses = new ArrayList<>();
        for (Map<String, Object> row : list1) {
            CertificateResponse certificateResponse = CertificateResponse.builder()
                    .id(ParseHelper.INT.parse(row.get("certificate_id")))
                    .certificateName(ParseHelper.STRING.parse(row.get("certificate_name")))
                    .certificateLevel(ParseHelper.STRING.parse(row.get("certificate_level")))
                    .validityPeriod(ParseHelper.BIGDECIMAL.parse(row.get("validity_period")))
                    .description(ParseHelper.STRING.parse(row.get("description")))
                    .build();
            certificateResponses.add(certificateResponse);
        }

        userInfoResponse.setCertificates(certificateResponses);

        return userInfoResponse;
    }

}
