package vn.xuanhung.ELearning_Service.service.impl;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import vn.xuanhung.ELearning_Service.common.ApiResponsePagination;
import vn.xuanhung.ELearning_Service.dto.request.TransactionQueryRequest;
import vn.xuanhung.ELearning_Service.dto.request.TransactionRequest;
import vn.xuanhung.ELearning_Service.dto.response.ArticleResponse;
import vn.xuanhung.ELearning_Service.dto.response.TransactionViewResponse;
import vn.xuanhung.ELearning_Service.entity.Article;
import vn.xuanhung.ELearning_Service.entity.Transaction;
import vn.xuanhung.ELearning_Service.entity.view.TransactionView;
import vn.xuanhung.ELearning_Service.exception.AppException;
import vn.xuanhung.ELearning_Service.exception.ErrorCode;
import vn.xuanhung.ELearning_Service.repository.TransactionRepository;
import vn.xuanhung.ELearning_Service.repository.view.TransactionViewRepository;
import vn.xuanhung.ELearning_Service.service.TransactionService;
import vn.xuanhung.ELearning_Service.specification.ArticleSpecification;
import vn.xuanhung.ELearning_Service.specification.TransactionSpecification;

import java.util.List;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class ITransactionService implements TransactionService {
    TransactionRepository transactionRepository;
    TransactionViewRepository transactionViewRepository;
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

    @Override
    public ApiResponsePagination<List<TransactionViewResponse>> getAll(TransactionQueryRequest req) {
        log.info("***Log transactions service - get all transaction by pagination***");
        Pageable pageable = PageRequest.of(
                req.getPage(),
                req.getPageSize(),
                Sort.by(Sort.Direction.DESC, "createdAt")
        );
        Specification<TransactionView> specification = TransactionSpecification.getSpecification(req);

        Page<TransactionView> transactions = transactionViewRepository.findAll(specification, pageable);

        List<TransactionView> data = transactions.getContent();
        return ApiResponsePagination.<List<TransactionViewResponse>>builder()
                .result(data.stream().map(item -> modelMapper.map(item , TransactionViewResponse.class)).toList())
                .totalPages(transactions.getTotalPages())
                .currentPage(req.getPage())
                .pageSize(req.getPageSize())
                .totalItems(transactions.getTotalElements())
                .build();
    }


}
