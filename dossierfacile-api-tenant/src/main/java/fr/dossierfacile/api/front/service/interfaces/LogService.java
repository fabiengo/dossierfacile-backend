package fr.dossierfacile.api.front.service.interfaces;

import fr.dossierfacile.common.enums.LogType;

public interface LogService {

    void saveLog(LogType logType, Long tenantId);

}
