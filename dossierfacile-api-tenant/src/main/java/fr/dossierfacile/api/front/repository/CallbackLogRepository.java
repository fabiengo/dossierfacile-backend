package fr.dossierfacile.api.front.repository;

import fr.dossierfacile.common.entity.CallbackLog;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CallbackLogRepository extends JpaRepository<CallbackLog, Long> {

}
