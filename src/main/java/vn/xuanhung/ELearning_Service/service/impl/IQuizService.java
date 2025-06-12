package vn.xuanhung.ELearning_Service.service.impl;

import jakarta.transaction.Transactional;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.shaded.com.google.protobuf.Api;
import org.modelmapper.ModelMapper;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import vn.xuanhung.ELearning_Service.common.ApiResponse;
import vn.xuanhung.ELearning_Service.common.RedisCacheFactory;
import vn.xuanhung.ELearning_Service.common.RedisGenericCacheService;
import vn.xuanhung.ELearning_Service.constant.AppConstant;
import vn.xuanhung.ELearning_Service.dto.request.*;
import vn.xuanhung.ELearning_Service.dto.response.*;
import vn.xuanhung.ELearning_Service.entity.*;
import vn.xuanhung.ELearning_Service.entity.view.ArticleUserView;
import vn.xuanhung.ELearning_Service.exception.AppException;
import vn.xuanhung.ELearning_Service.exception.ErrorCode;
import vn.xuanhung.ELearning_Service.repository.*;
import vn.xuanhung.ELearning_Service.service.QuizService;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class IQuizService implements QuizService {
    QuizRepository quizRepository;
    QuestionRepository questionRepository;
    UserInfoRepository userInfoRepository;
    UserQuizRepository userQuizRepository;
    AnswerRepository answerRepository;

    KafkaTemplate<String, Object> kafkaTemplate;
    ModelMapper modelMapper;
    RedisCacheFactory redisCacheFactory;

    @Override
    @Transactional
    public ApiResponse<QuizDetailResponse> add(QuizRequest req) {
        log.info("***Log quiz service - save quiz ***");

        if(req.getId() == null) {
            Quiz quiz = Quiz.builder()
                    .courseId(req.getCourseId())
                    .title(req.getTitle())
                    .description(req.getDescription())
                    .isActive("Y")
                    .timeLimit(req.getTimeLimit())
                    .build();

            quiz = quizRepository.saveAndFlush(quiz);

            if(req.getQuestions() != null && !req.getQuestions().isEmpty()){
                for(QuestionRequest questionRequest: req.getQuestions()){
                    Question question = Question.builder()
                            .quizId(quiz.getId())
                            .questionText(questionRequest.getQuestionText())
                            .questionType("GIF")//Tam thoi
                            .build();

                    question = questionRepository.saveAndFlush(question);

                    if(questionRequest.getAnswers() != null && !questionRequest.getAnswers().isEmpty()){
                        long correctCount = questionRequest.getAnswers().stream()
                                .filter(ans -> "Y".equals(ans.getIsCorrect()))
                                .count();

                        if (correctCount != 1) {
                            throw new AppException(ErrorCode.INVALID_ANSWER_COUNT); // bạn định nghĩa thêm error này
                        }

                        for(AnswerRequest answerRequest: questionRequest.getAnswers()){
                            Answer answer = Answer.builder()
                                    .questionId(question.getId())
                                    .answerText(answerRequest.getAnswerText())
                                    .isCorrect(answerRequest.getIsCorrect())
                                    .build();
                            answerRepository.save(answer);
                        }
                    }else{
                        throw new AppException(ErrorCode.ANSWER_EMPTY);
                    }
                }
            }else{
                throw new AppException(ErrorCode.QUESTION_EMPTY);
            }

            //Clear key voi course da thay doi
            log.info("Send Kafka with topic: {}", AppConstant.Topic.COURSE_UPDATE_EVENT);
            kafkaTemplate.send(AppConstant.Topic.COURSE_UPDATE_EVENT, CourseCacheUpdateEvent.builder()
                    .courseId(req.getCourseId())
                    .action(AppConstant.ACTION.INVALIDATE)
                    .build());

            QuizDetailResponse response = QuizDetailResponse.builder()
                    .id(quiz.getId())
                    .title(quiz.getTitle())
                    .timeLimit(quiz.getTimeLimit())
                    .build();
            return ApiResponse.<QuizDetailResponse>builder()
                    .result(response)
                    .build();
        }

        throw new AppException(ErrorCode.UNAUTHENTICATED);
    }

    @Override
    @Transactional
    public ApiResponse<QuizHeaderResponse> remove(Integer id) {
        log.info("***Log quiz service - remove quiz ***");
        Quiz quiz = quizRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.QUIZ_NOT_EXIST));

        quiz.setIsActive(AppConstant.STATUS_UNACTIVE);

        quiz = quizRepository.save(quiz);

        //Clear key voi course da thay doi
        log.info("Send Kafka with topic: {}", AppConstant.Topic.COURSE_UPDATE_EVENT);
        kafkaTemplate.send(AppConstant.Topic.COURSE_UPDATE_EVENT, CourseCacheUpdateEvent.builder()
                .courseId(quiz.getCourseId())
                .action(AppConstant.ACTION.INVALIDATE)
                .build());

        return ApiResponse.<QuizHeaderResponse>builder()
                .result(modelMapper.map(quiz, QuizHeaderResponse.class))
                .build();
    }

    @Override
    public ApiResponse<QuizDetailResponse> getById(Integer id) {
        log.info("***Log quiz service - get quiz ***");
        RedisGenericCacheService<QuizDetailResponse> redisGenericCacheService = redisCacheFactory
                .create(AppConstant.PREFIX.QUIZ, QuizDetailResponse.class);

        redisGenericCacheService.setPrefix(AppConstant.PREFIX.QUIZ + ":" + id);

        QuizDetailResponse response = redisGenericCacheService.getByKey();
        if(response != null){
            return ApiResponse.<QuizDetailResponse>builder()
                    .result(response)
                    .build();
        }

        Quiz quiz = quizRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.QUIZ_NOT_EXIST));

        List<Question> questions = questionRepository.findAllByQuizId(quiz.getId());
        List<QuestionResponse> questionResponses = new ArrayList<>();
        for(Question question: questions) {
            QuestionResponse questionResponse = QuestionResponse.builder()
                    .id(question.getId())
                    .quizId(question.getQuizId())
                    .description(question.getDescription())
                    .questionText(question.getQuestionText())
                    .build();

            List<Answer> answers = answerRepository.findByQuestionId(question.getId());
            List<AnswerResponse> answerResponses = new ArrayList<>();
            for (Answer answer : answers) {
                AnswerResponse answerResponse = AnswerResponse.builder()
                        .id(answer.getId())
                        .isCorrect("N")
                        .questionId(question.getId())
                        .answerText(answer.getAnswerText())
                        .build();
                answerResponses.add(answerResponse);
            }
            questionResponse.setAnswers(answerResponses);
            questionResponses.add(questionResponse);
        }

        response = QuizDetailResponse.builder()
                .id(quiz.getId())
                .title(quiz.getTitle())
                .description(quiz.getDescription())
                .timeLimit(quiz.getTimeLimit())
                .questions(questionResponses)
                .build();

        //Tao cache
        redisGenericCacheService.saveItem(response, Duration.ofHours(2));

        return ApiResponse.<QuizDetailResponse>builder()
                .result(response)
                .build();
    }

    @Override
    public ApiResponse<QuizResultResponse> calculate(QuizResultRequest req) {
        log.info("***Log quiz service - calculate quiz ***");
        Quiz quiz = quizRepository.findById(req.getQuizId())
                .orElseThrow(() -> new AppException(ErrorCode.QUIZ_NOT_EXIST));

        UserInfo userInfo = userInfoRepository.findById(req.getUserId())
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXIST));

        int correctCount = 0;
        int totalQuestions = 0;
        if(req.getResults() != null && !req.getResults().isEmpty()){
            List<Integer> questionIds = req.getResults().stream()
                    .map(AnswerResultRequest::getQuestionId)
                    .distinct()
                    .collect(Collectors.toList());

            List<Integer> answerIds = req.getResults().stream()
                    .map(AnswerResultRequest::getSelectedAnswerId)
                    .distinct()
                    .collect(Collectors.toList());

            List<Question> questions = questionRepository.findAllById(questionIds);
            totalQuestions = questions.size();

            List<Answer> answers = answerRepository.findAllById(answerIds);

            for(int i = 0; i < answers.size(); i++){
                if("Y".equals(answers.get(i).getIsCorrect()) && answers.get(i).getQuestionId() != null){
                    correctCount++;
                }
            }
        }

        UserQuiz userQuiz = UserQuiz.builder()
                .userId(userInfo.getId())
                .quizId(quiz.getId())
                .submittedAt(new Date())
                .score(BigDecimal.valueOf(correctCount * 100.0 / totalQuestions).setScale(2, RoundingMode.HALF_UP))
                .build();
        userQuizRepository.save(userQuiz);

        QuizResultResponse response = QuizResultResponse.builder()
                .user(UserInfoResponse.builder()
                        .id(userInfo.getId())
                        .firstName(userInfo.getFirstName())
                        .lastName(userInfo.getLastName())
                        .avatar(userInfo.getAvatar())
                        .build())
                .quiz(QuizHeaderResponse.builder()
                        .id(quiz.getId())
                        .courseId(quiz.getCourseId())
                        .title(quiz.getTitle())
                        .timeLimit(quiz.getTimeLimit())
                        .description(quiz.getDescription())
                        .build())
                .amountCorrect(correctCount)
                .totalQuestion(totalQuestions)
                .amountWrong(totalQuestions - correctCount)
                .percentageCorrect(BigDecimal.valueOf(correctCount * 100.0 / totalQuestions).setScale(2, RoundingMode.HALF_UP))
                .build();

        log.info("response: {}", response);
        return ApiResponse.<QuizResultResponse>builder()
                .result(response)
                .build();
    }
}
