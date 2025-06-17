
package com.automacao.automacaoweb.repository;

import com.automacao.automacaoweb.model.LogEntry;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository // Marca esta interface como um componente de repositório
public interface LogEntryRepository extends JpaRepository<LogEntry, Long> {
}
