package vn.xuanhung.ELearning_Service.service.impl;

import com.paypal.api.payments.*;
import com.paypal.base.rest.APIContext;
import com.paypal.base.rest.PayPalRESTException;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import vn.xuanhung.ELearning_Service.dto.request.PaypalRequest;
import vn.xuanhung.ELearning_Service.service.PaypalService;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

@Service
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
@Slf4j
public class IPaypalService implements PaypalService {
    APIContext apiContext;

    @Override
    public Payment creatPayment(PaypalRequest request) throws PayPalRESTException {
        log.info(request.getBalance().toString());
        Amount amount = new Amount();
        amount.setCurrency(request.getCurrency());
        amount.setTotal(String.format(Locale.forLanguageTag(request.getCurrency()), "%.2f", request.getBalance()));

        Transaction transaction = new Transaction();
        transaction.setDescription(request.getDescription());
        transaction.setAmount(amount);

        List<Transaction> transactions = new ArrayList<>();
        transactions.add(transaction);

        RedirectUrls redirectUrls = new RedirectUrls();
        redirectUrls.setCancelUrl(request.getUrlError());
        redirectUrls.setReturnUrl(request.getUrlSuccess());

        Payment payment = new Payment();
        payment.setIntent("sale"); // <<< Phải set intent
        payment.setPayer(new Payer().setPaymentMethod("paypal")); // <<< Phải set payer
        payment.setTransactions(transactions); // <<< Đừng quên set transactions nữa nếu bạn có

        payment.setRedirectUrls(redirectUrls);

        return payment.create(apiContext);
    }

    @Override
    public Payment executePayment(String payment_id, String payer_id) throws PayPalRESTException {
        Payment payment = new Payment();
        payment.setId(payment_id);

        PaymentExecution execution = new PaymentExecution();
        execution.setPayerId(payer_id);

        return payment.execute(apiContext, execution);
    }
}
