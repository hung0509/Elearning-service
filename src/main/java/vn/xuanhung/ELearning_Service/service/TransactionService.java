package vn.xuanhung.ELearning_Service.service;

import vn.xuanhung.ELearning_Service.common.ApiResponsePagination;
import vn.xuanhung.ELearning_Service.dto.request.TransactionQueryRequest;
import vn.xuanhung.ELearning_Service.dto.request.TransactionRequest;
import vn.xuanhung.ELearning_Service.dto.response.TransactionViewResponse;
import vn.xuanhung.ELearning_Service.entity.Transaction;

import java.util.List;

public interface TransactionService {
    public Transaction saveTransaction(TransactionRequest transaction);

    public Transaction findByPaymentId(String paymentId);

    public ApiResponsePagination<List<TransactionViewResponse>> getAll(TransactionQueryRequest req);
}
