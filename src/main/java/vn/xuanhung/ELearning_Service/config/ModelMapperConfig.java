package vn.xuanhung.ELearning_Service.config;

import org.modelmapper.Conditions;
import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.modelmapper.convention.NamingConventions;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ModelMapperConfig {
    @Bean
    public ModelMapper modelMapper() {
        ModelMapper modelMapper = new ModelMapper();

        modelMapper.getConfiguration()
                .setFieldMatchingEnabled(true)
                .setFieldAccessLevel(org.modelmapper.config.Configuration.AccessLevel.PRIVATE)
                .setPropertyCondition(Conditions.isNotNull())
                .setMatchingStrategy(MatchingStrategies.STRICT)
                .setSourceNamingConvention(NamingConventions.JAVABEANS_MUTATOR);
        return modelMapper;
    }
}

//Xử lý ghi log
//        Thêm các logic trước hoặc sau khi ánh xạ:
//        TypeMap<SourceClass, TargetClass> typeMap = modelMapper.createTypeMap(SourceClass.class, TargetClass.class);
//        typeMap.setPreConverter(context -> {
//            // Logic trước khi ánh xạ
//            return context.getSource();
//        });
//        typeMap.setPostConverter(context -> {
//            // Logic sau khi ánh xạ
//            return context.getDestination();
//        });
