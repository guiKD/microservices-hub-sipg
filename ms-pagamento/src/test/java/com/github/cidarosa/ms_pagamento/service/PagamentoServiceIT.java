package com.github.cidarosa.ms_pagamento.service;

import com.github.cidarosa.ms_pagamento.repository.PagamentoRepository;
import com.github.cidarosa.ms_pagamento.service.exceptions.ResourceNotFoundException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@Transactional

public class PagamentoServiceIT {

    @Autowired
    private PagamentoService service;

    @Autowired
    private PagamentoRepository repository;

    private Long existingId;
    private Long nonExistingId;
    private Long countTotalPagamentos;

    @BeforeEach
    void setup() throws Exception {
        existingId = 1L;
        nonExistingId = 100L;
        countTotalPagamentos = 6L;
    }

    public void deletePagamentoShouldDeleteResourceWhenIdExists() {

        service.deletePagamento(existingId);
        Assertions.assertEquals(countTotalPagamentos - 1, repository.count());

    }

    @Test
    public void deletePagamentoShouldThrowResourceNotFoundExceptionWhenIdDoesNotExist() {

        Assertions.assertThrows(ResourceNotFoundException.class,
                () -> {
            service.deletePagamento(nonExistingId);
                }
                );

    }


}
