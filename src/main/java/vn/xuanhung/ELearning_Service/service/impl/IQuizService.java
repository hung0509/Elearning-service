package vn.xuanhung.ELearning_Service.service.impl;

import jakarta.transaction.Transactional;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.shaded.com.google.protobuf.Api;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import vn.xuanhung.ELearning_Service.common.ApiResponse;
import vn.xuanhung.ELearning_Service.constant.AppConstant;
import vn.xuanhung.ELearning_Service.dto.request.AnswerRequest;
import vn.xuanhung.ELearning_Service.dto.request.QuestionRequest;
import vn.xuanhung.ELearning_Service.dto.request.QuizRequest;
import vn.xuanhung.ELearning_Service.dto.response.QuizDetailResponse;
import vn.xuanhung.ELearning_Service.dto.response.QuizHeaderResponse;
import vn.xuanhung.ELearning_Service.entity.Answer;
import vn.xuanhung.ELearning_Service.entity.Question;
import vn.xuanhung.ELearning_Service.entity.Quiz;
import vn.xuanhung.ELearning_Service.exception.AppException;
import vn.xuanhung.ELearning_Service.exception.ErrorCode;
import vn.xuanhung.ELearning_Service.repository.AnswerRepository;
import vn.xuanhung.ELearning_Service.repository.QuestionRepository;
import vn.xuanhung.ELearning_Service.repository.QuizRepository;
import vn.xuanhung.ELearning_Service.service.QuizService;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class IQuizService implements QuizService {
    QuizRepository quizRepository;
    QuestionRepository questionRepository;
    AnswerRepository answerRepository;
    ModelMapper modelMapper;

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
        Quiz quiz = quizRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.QUIZ_NOT_EXIST));

        quiz.setIsActive(AppConstant.STATUS_UNACTIVE);

        quiz = quizRepository.save(quiz);
        return ApiResponse.<QuizHeaderResponse>builder()
                .result(modelMapper.map(quiz, QuizHeaderResponse.class))
                .build();
    }
}
