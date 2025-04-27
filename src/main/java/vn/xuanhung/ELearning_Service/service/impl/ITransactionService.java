package vn.xuanhung.ELearning_Service.service.impl;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import vn.xuanhung.ELearning_Service.dto.request.TransactionRequest;
import vn.xuanhung.ELearning_Service.entity.Transaction;
import vn.xuanhung.ELearning_Service.exception.AppException;
import vn.xuanhung.ELearning_Service.exception.ErrorCode;
import vn.xuanhung.ELearning_Service.repository.TransactionRepository;
import vn.xuanhung.ELearning_Service.service.TransactionService;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class ITransactionService implements TransactionService {
    TransactionRepository transactionRepository;
    ModelMapper modelMapper;

    @Override
    public Transaction saveTransaction(TransactionRequest transaction) {
        Transaction transactionEntity = null;
        if(transaction.getId() != null){
            transactionEntity = transactionRepository.findById(transaction.getId())
                    .orElseThrow(() -> new AppException(ErrorCode.PAYMENT_METHOD_NOT_EXIST));
            modelMapper.map(transaction, transactionEntity);

        }else{
            transactionEntity = modelMapper.map(transaction, Transaction.class);
            transactionRepository.save(transactionEntity);
        }

        transactionEntity = transactionRepository.save(transactionEntity);
        return transactionEntity;
    }

    @Override
    public Transaction findByPaymentId(String paymentId) {
        return transactionRepository.findByPaymentId(paymentId);
    }
}
