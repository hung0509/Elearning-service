package vn.xuanhung.ELearning_Service.service;

import vn.xuanhung.ELearning_Service.dto.request.TransactionRequest;
import vn.xuanhung.ELearning_Service.entity.Transaction;

public interface TransactionService {
    public Transaction saveTransaction(TransactionRequest transaction);

    public Transaction findByPaymentId(String paymentId);
}
