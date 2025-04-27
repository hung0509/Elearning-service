package vn.xuanhung.ELearning_Service.controller;

import com.paypal.api.payments.Links;
import com.paypal.api.payments.Payment;
import com.paypal.base.rest.PayPalRESTException;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.checkerframework.checker.units.qual.A;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.view.RedirectView;
import vn.xuanhung.ELearning_Service.common.ApiResponse;
import vn.xuanhung.ELearning_Service.constant.AppConstant;
import vn.xuanhung.ELearning_Service.dto.request.PaypalRequest;
import vn.xuanhung.ELearning_Service.dto.request.TransactionRequest;
import vn.xuanhung.ELearning_Service.dto.request.UserCourseRequest;
import vn.xuanhung.ELearning_Service.entity.Transaction;
import vn.xuanhung.ELearning_Service.exception.AppException;
import vn.xuanhung.ELearning_Service.exception.ErrorCode;
import vn.xuanhung.ELearning_Service.service.PaypalService;
import vn.xuanhung.ELearning_Service.service.TransactionService;
import vn.xuanhung.ELearning_Service.service.UserInfoService;

import java.io.IOException;
import java.util.Date;

@RestController
@RequiredArgsConstructor
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequestMapping("/payments")
@CrossOrigin(origins = "http://localhost:3000")
public class PaymentController {
    PaypalService paypalService;
    TransactionService transactionService;
    UserInfoService userInfoService;

    @PostMapping("/create")
    public ApiResponse<String> createPayment(@RequestBody PaypalRequest request) throws PayPalRESTException, IOException {
        Payment payment = paypalService.creatPayment(request);

        for(Links link: payment.getLinks()){
            if(link.getRel().equals("approval_url")){
                TransactionRequest req = TransactionRequest
                        .builder()
                        .paymentId(payment.getId())
                        .balance(request.getBalance())
                        .courseId(request.getCourseId())
                        .userId(request.getUserId())
                        .currency(request.getCurrency())
                        .description(request.getDescription())
                        .paymentMethod(request.getMethod())
                        .paymentDate(new Date())
                        .build();
                transactionService.saveTransaction(req);
                return ApiResponse.<String>builder()
                        .result(link.getHref())
                        .build();
            }
        }
        throw new AppException(ErrorCode.PAYMENT_CREATE_ERROR);
    }

    @GetMapping("/success")
    public RedirectView paymentSuccess(@RequestParam("paymentId") String paymentId,
                                       @RequestParam("PayerID") String payerId
                                        )
            throws PayPalRESTException, IOException {
        Payment payment = paypalService.executePayment(paymentId, payerId);
        if (payment.getState().equals("approved")) {
            Transaction transaction = transactionService.findByPaymentId(payment.getId());
            if(transaction != null){
                TransactionRequest req = TransactionRequest
                        .builder()
                        .id(transaction.getId())
                        .statusPayment(AppConstant.COMPLETE)
                        .build();
                log.info("Luu transaction");
                Transaction transaction1 = transactionService.saveTransaction(req);

                UserCourseRequest req1 = UserCourseRequest
                        .builder()
                        .userId(transaction1.getUserId())
                        .courseId(transaction1.getCourseId())
                        .build();

                log.info("Đăng ký khóa học");
                userInfoService.registerCourse(req1);
            }
             return new RedirectView("http://localhost:3000/payment/success");
        }
        return new RedirectView("http://localhost:3000/payment/error");
    }

    @GetMapping("/error")
    public RedirectView paymentError(){
        return new RedirectView("http:/localhost:3000/payment/error");
    }

}
