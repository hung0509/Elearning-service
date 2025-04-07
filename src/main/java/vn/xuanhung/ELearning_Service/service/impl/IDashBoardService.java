package vn.xuanhung.ELearning_Service.service.impl;

import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.query.NativeQuery;
import org.hibernate.transform.AliasToEntityMapResultTransformer;
import org.springframework.stereotype.Service;
import vn.xuanhung.ELearning_Service.common.ParseHelper;
import vn.xuanhung.ELearning_Service.dto.response.DashBoardResponse;
import vn.xuanhung.ELearning_Service.exception.AppException;
import vn.xuanhung.ELearning_Service.exception.ErrorCode;
import vn.xuanhung.ELearning_Service.service.DashBoardService;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
@Slf4j
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class IDashBoardService implements DashBoardService {
    EntityManager entityManager;

    @Override
    @SuppressWarnings("unchecked")
    public DashBoardResponse getDashBoard(String year) {
        Query query = entityManager.createNativeQuery("SELECT * FROM d_statistical_view");

        List<Map<String, Object>> data = query.unwrap(NativeQuery.class)
                .setResultTransformer(AliasToEntityMapResultTransformer.INSTANCE)
                .getResultList();
        try{
            return DashBoardResponse.builder()
                    .amountUser(ParseHelper.INT.parse(data.get(0).get("number_of_users")))
                    .amountCourse(ParseHelper.INT.parse(data.get(0).get("number_of_courses")))
                    .amountArticle(ParseHelper.INT.parse(data.get(0).get("number_of_articles")))
                    .amountTeacher(ParseHelper.INT.parse(data.get(0).get("number_of_authors")))
                    .amountUserEMonth(getUserByMonth(year))
                    .build();
        }catch (Exception e){
            log.error("Error: {}", e.getMessage());
            throw new AppException(ErrorCode.ERROR_SQL);
        }
    }

    @SuppressWarnings("unchecked")
    private List<Integer> getUserByMonth(String year){
        Query query = entityManager.createNativeQuery("SELECT * FROM d_monthly_user_enroll_view " +
                "where Year = :year order by Month");

        query.setParameter("year", year);

        List<Map<String, Object>> data = query.unwrap(NativeQuery.class)
                .setResultTransformer(AliasToEntityMapResultTransformer.INSTANCE)
                .getResultList();

        try{
            List<Integer> listUserbyMonth = new ArrayList<>();
            for(Map<String, Object> row: data){
                listUserbyMonth.add(ParseHelper.INT.parse(row.get("number_of_users")));
            }

            return listUserbyMonth;
        }catch (Exception e){
            log.error("Error: {}", e.getMessage());
            throw new AppException(ErrorCode.ERROR_SQL);
        }
    }
}
