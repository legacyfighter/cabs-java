package io.legacyfighter.cabs.service;

import io.legacyfighter.cabs.entity.Invoice;
import io.legacyfighter.cabs.repository.InvoiceRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
public class InvoiceGenerator {

    @Autowired
    InvoiceRepository invoiceRepository;

    public Invoice generate(Integer amount, String subjectName) {
        return invoiceRepository.save(new Invoice(new BigDecimal(amount), subjectName));
    }
}
