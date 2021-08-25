package io.legacyfighter.cabs.repository;

import io.legacyfighter.cabs.entity.Invoice;
import org.springframework.data.jpa.repository.JpaRepository;

public interface InvoiceRepository extends JpaRepository<Invoice, Long> {

}
