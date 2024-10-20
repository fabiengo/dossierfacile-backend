package fr.dossierfacile.scheduler.tasks.storagesynchronization;

import fr.dossierfacile.common.entity.StorageFileToDelete;
import fr.dossierfacile.common.repository.StorageFileToDeleteRepository;
import fr.dossierfacile.common.service.interfaces.FileStorageToDeleteService;
import fr.dossierfacile.scheduler.LoggingContext;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.List;

import static fr.dossierfacile.scheduler.tasks.TaskName.STORAGE_FILES_DELETION;

@Slf4j
@Service
@RequiredArgsConstructor
public class DeleteFilesTask {

    private final StorageFileToDeleteRepository storageFileToDeleteRepository;
    private final FileStorageToDeleteService fileStorageToDeleteService;

    @Scheduled(fixedDelayString = "${scheduled.process.storage.delete.delay.ms}", initialDelayString = "${scheduled.process.storage.delete.delay.ms}")
    public void deleteFileInProviderTask() {
        LoggingContext.startTask(STORAGE_FILES_DELETION);
        List<StorageFileToDelete> storageFileToDeleteList = storageFileToDeleteRepository.findAll();
        for (StorageFileToDelete storageFileToDelete : storageFileToDeleteList) {
            fileStorageToDeleteService.delete(storageFileToDelete);
        }
        LoggingContext.endTask();
    }

}
