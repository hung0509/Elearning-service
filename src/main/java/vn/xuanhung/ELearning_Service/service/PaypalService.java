package vn.xuanhung.ELearning_Service.service;

import com.paypal.api.payments.Payment;
import com.paypal.base.rest.PayPalRESTException;
import vn.xuanhung.ELearning_Service.dto.request.PaypalRequest;

public interface PaypalService {
    public Payment creatPayment(PaypalRequest request) throws PayPalRESTException;

    public Payment executePayment(String payment_id, String payer_id) throws PayPalRESTException;
}
