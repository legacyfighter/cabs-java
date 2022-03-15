package io.legacyfighter.cabs.transitdetails;

import org.springframework.data.jpa.repository.JpaRepository;

interface TransitDetailsRepository extends JpaRepository<TransitDetails, Long> {

    TransitDetails findByTransitId(Long transitId);

}
