package nl.demo.ted.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface TedRepository extends JpaRepository<TedRecord, String>, JpaSpecificationExecutor<TedRecord> {
}
