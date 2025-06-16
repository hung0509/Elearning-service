package vn.xuanhung.ELearning_Service.controller;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import vn.xuanhung.ELearning_Service.common.ApiResponsePagination;
import vn.xuanhung.ELearning_Service.dto.request.TransactionQueryRequest;
import vn.xuanhung.ELearning_Service.dto.response.TransactionViewResponse;
import vn.xuanhung.ELearning_Service.service.TransactionService;

import java.util.List;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/transactions")
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class TransactionController {
    TransactionService transactionService;

    @GetMapping
    public ApiResponsePagination<List<TransactionViewResponse>> getAll(@ModelAttribute TransactionQueryRequest req){
        log.info("Log transaction controller - get transaction-users");
        return transactionService.getAll(req);
    }
}
