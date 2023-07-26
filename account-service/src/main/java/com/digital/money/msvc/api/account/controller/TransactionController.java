package com.digital.money.msvc.api.account.controller;

import com.digital.money.msvc.api.account.service.impl.TransactionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/transactions")
public class TransactionController {
    @Autowired
    private TransactionService transactionService;
/*
    @Operation(summary = "Save an transaction", hidden = true)
    @PostMapping
    public ResponseEntity<TransactionGetDto> save(@RequestBody TransactionPostDto transactionPostDto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(transactionService.save(transactionPostDto));
    }*/

//    @GetMapping
//    public List<TransactionGetDto> getLastFive(@RequestParam Long id) throws ResourceNotFoundException {
//        return transactionService.getLastFive(id);
//    }
}
