package vn.xuanhung.ELearning_Service.mapper;

import jakarta.annotation.PostConstruct;
import org.modelmapper.ModelMapper;
import org.modelmapper.PropertyMap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import vn.xuanhung.ELearning_Service.common.BaseMapper;
import vn.xuanhung.ELearning_Service.dto.response.CourseHeaderViewResponse;
import vn.xuanhung.ELearning_Service.entity.view.CourseHeaderView;

@Component
public class CourseHeaderMapper extends BaseMapper<CourseHeaderView, CourseHeaderViewResponse> {
    @Autowired
    public CourseHeaderMapper(ModelMapper modelMapper) {
        super(CourseHeaderView.class, CourseHeaderViewResponse.class, modelMapper);
    }

    @PostConstruct
    public void init() {
        modelMapper.addMappings(new PropertyMap<CourseHeaderView, CourseHeaderViewResponse>() {
            @Override
            protected void configure() {
                map(source.getInstructorId(), destination.getUser().getId());
                map(source.getFullName(), destination.getUser().getLastName());
                map(source.getFullName(), destination.getUser().getFirstName());
                map(source.getCertificateId(), destination.getCertificate().getId());
                map(source.getCertificateName(), destination.getCertificate().getCertificateName());
                map(source.getCategoryId(), destination.getCategory().getId());
                map(source.getCategoryName(), destination.getCategory().getCategoryName());
                map(source.getDiscountId(), destination.getDiscount().getId());
                map(source.getDiscountRate(), destination.getDiscount().getDiscountRate());
                map(source.getDiscountCode(), destination.getDiscount().getDiscountCode());
            }
        });
    }

//    private Discount mapDiscount(Object object) {
//        Discount discount = new Discount();
//        CourseHeaderView source = (CourseHeaderView) object;
//
//        if (source.getDiscountId() != null) {
//            discount.setId(source.getDiscountId());
//        }
//        if (source.getDiscountCode() != null) {
//            discount.setDiscountCode(source.getDiscountCode());
//        }
//        if (source.getDiscountRate() != null) {
//            discount.setDiscountRate(source.getDiscountRate());
//        }
//
//        return discount;
//    }
//    private Category mapCategory(Object object) {
//        Category category = new Category();
//        CourseHeaderView source = (CourseHeaderView) object;
//
//        if (source.getCategoryId() != null) {
//            category.setId(source.getCategoryId());
//        }
//        if (source.getCategoryName() != null) {
//            category.setCategoryName(source.getCategoryName());
//        }
//
//        return category;
//    }
//    private UserInfo mapInstructor(Object object) {
//        UserInfo userInfo = new UserInfo();
//        CourseHeaderView source = (CourseHeaderView) object;
//
//        if (source.getInstructorId() != null) {
//            userInfo.setId(source.getCategoryId());
//        }
//        if (source.getFullName() != null) {
//            userInfo.setFirstName(source.getFullName());
//            userInfo.setLastName(source.getFullName());
//        }
//
//        return userInfo;
//    }
//    private Certificate mapCertificate(Object object) {
//        Certificate certificate = new Certificate();
//        CourseHeaderView source = (CourseHeaderView) object;
//
//        if (source.getCertificateId() != null) {
//            certificate.setId(source.getCertificateId());
//        }
//        if (source.getCertificateName() != null) {
//            certificate.setCertificateName(source.getCertificateName());
//        }
//
//        return certificate;
//    }
}
