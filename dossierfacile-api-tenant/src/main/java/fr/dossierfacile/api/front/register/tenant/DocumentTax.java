package fr.dossierfacile.api.front.register.tenant;

import fr.dossierfacile.api.front.mapper.TenantMapper;
import fr.dossierfacile.api.front.model.tenant.TenantModel;
import fr.dossierfacile.api.front.register.SaveStep;
import fr.dossierfacile.api.front.register.form.tenant.DocumentTaxForm;
import fr.dossierfacile.api.front.repository.DocumentRepository;
import fr.dossierfacile.api.front.repository.FileRepository;
import fr.dossierfacile.api.front.repository.TenantRepository;
import fr.dossierfacile.api.front.service.OvhService;
import fr.dossierfacile.api.front.service.interfaces.DocumentService;
import fr.dossierfacile.common.entity.Document;
import fr.dossierfacile.common.entity.File;
import fr.dossierfacile.common.entity.Tenant;
import fr.dossierfacile.common.enums.DocumentCategory;
import fr.dossierfacile.common.enums.DocumentStatus;
import fr.dossierfacile.common.enums.DocumentSubCategory;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import static fr.dossierfacile.common.enums.DocumentSubCategory.MY_NAME;
import static fr.dossierfacile.common.enums.DocumentSubCategory.OTHER_TAX;

@Service
@AllArgsConstructor
public class DocumentTax implements SaveStep<DocumentTaxForm> {

    private final OvhService ovhService;
    private final TenantRepository tenantRepository;
    private final DocumentRepository documentRepository;
    private final TenantMapper tenantMapper;
    private final FileRepository fileRepository;
    private final DocumentService documentService;

    @Override
    @Transactional
    public TenantModel saveStep(Tenant tenant, DocumentTaxForm documentTaxForm) {
        DocumentSubCategory documentSubCategory = documentTaxForm.getTypeDocumentTax();
        Document document = documentRepository.findByDocumentCategoryAndTenant(DocumentCategory.TAX, tenant)
                .orElse(Document.builder()
                        .documentCategory(DocumentCategory.TAX)
                        .tenant(tenant)
                        .build());
        document.setDocumentStatus(DocumentStatus.TO_PROCESS);
        document.setDocumentSubCategory(documentSubCategory);
        document.setCustomText(null);
        if (document.getNoDocument() != null && !document.getNoDocument() && documentTaxForm.getNoDocument()) {
            deleteFilesIfExistedBefore(document);
        }
        document.setNoDocument(documentTaxForm.getNoDocument());
        documentRepository.save(document);

        if (documentSubCategory == MY_NAME
                || (documentSubCategory == OTHER_TAX && !documentTaxForm.getNoDocument())) {
            List<MultipartFile> multipartFiles = documentTaxForm.getDocuments().stream().filter(f -> !f.isEmpty()).collect(Collectors.toList());
            for (MultipartFile multipartFile : multipartFiles) {
                String originalName = multipartFile.getOriginalFilename();
                long size = multipartFile.getSize();
                String name = ovhService.uploadFile(multipartFile);
                File file = File.builder()
                        .path(name)
                        .document(document)
                        .originalName(originalName)
                        .size(size)
                        .build();
                fileRepository.save(file);
            }
        }
        if (documentSubCategory == OTHER_TAX && documentTaxForm.getNoDocument()) {
            document.setCustomText(documentTaxForm.getCustomText());
        }
        documentRepository.save(document);
        documentService.generatePdfByFilesOfDocument(document);
        tenant.lastUpdateDateProfile(LocalDateTime.now(), DocumentCategory.TAX);
        documentService.updateOthersDocumentsStatus(tenant);
        return tenantMapper.toTenantModel(tenantRepository.save(tenant));
    }

    private void deleteFilesIfExistedBefore(Document document) {
        if (document.getFiles() != null && !document.getFiles().isEmpty()) {
            document.setFiles(null);
            fileRepository.deleteAll(document.getFiles());
            ovhService.delete(document.getFiles().stream().map(File::getPath).collect(Collectors.toList()));
        }
    }
}
