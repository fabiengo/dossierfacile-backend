package fr.dossierfacile.api.front.register.guarantor.natural_person;

import fr.dossierfacile.api.front.exception.GuarantorNotFoundException;
import fr.dossierfacile.api.front.mapper.TenantMapper;
import fr.dossierfacile.api.front.model.tenant.TenantModel;
import fr.dossierfacile.api.front.register.SaveStep;
import fr.dossierfacile.api.front.register.form.guarantor.natural_person.DocumentTaxGuarantorNaturalPersonForm;
import fr.dossierfacile.api.front.repository.DocumentRepository;
import fr.dossierfacile.api.front.repository.FileRepository;
import fr.dossierfacile.api.front.repository.GuarantorRepository;
import fr.dossierfacile.api.front.repository.TenantRepository;
import fr.dossierfacile.api.front.service.OvhService;
import fr.dossierfacile.api.front.service.interfaces.DocumentService;
import fr.dossierfacile.common.entity.Document;
import fr.dossierfacile.common.entity.File;
import fr.dossierfacile.common.entity.Guarantor;
import fr.dossierfacile.common.entity.Tenant;
import fr.dossierfacile.common.enums.DocumentCategory;
import fr.dossierfacile.common.enums.DocumentStatus;
import fr.dossierfacile.common.enums.DocumentSubCategory;
import fr.dossierfacile.common.enums.TypeGuarantor;
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
public class DocumentTaxGuarantorNaturalPerson implements SaveStep<DocumentTaxGuarantorNaturalPersonForm> {

    private final OvhService ovhService;
    private final TenantRepository tenantRepository;
    private final DocumentRepository documentRepository;
    private final TenantMapper tenantMapper;
    private final GuarantorRepository guarantorRepository;
    private final FileRepository fileRepository;
    private final DocumentService documentService;

    @Override
    @Transactional
    public TenantModel saveStep(Tenant tenant, DocumentTaxGuarantorNaturalPersonForm documentTaxGuarantorNaturalPersonForm) {
        documentService.updateOthersDocumentsStatus(tenant);
        Guarantor guarantor = guarantorRepository.findByTenantAndTypeGuarantorAndId(tenant, TypeGuarantor.NATURAL_PERSON, documentTaxGuarantorNaturalPersonForm.getGuarantorId())
                .orElseThrow(() -> new GuarantorNotFoundException(documentTaxGuarantorNaturalPersonForm.getGuarantorId()));

        DocumentSubCategory documentSubCategory = documentTaxGuarantorNaturalPersonForm.getTypeDocumentTax();
        Document document = documentRepository.findByDocumentCategoryAndGuarantor(DocumentCategory.TAX, guarantor)
                .orElse(Document.builder()
                        .documentCategory(DocumentCategory.TAX)
                        .guarantor(guarantor)
                        .build());
        document.setDocumentStatus(DocumentStatus.TO_PROCESS);
        document.setDocumentSubCategory(documentSubCategory);
        document.setCustomText(null);
        if (document.getNoDocument() != null && !document.getNoDocument() && documentTaxGuarantorNaturalPersonForm.getNoDocument()) {
            deleteFilesIfExistedBefore(document);
        }
        document.setNoDocument(documentTaxGuarantorNaturalPersonForm.getNoDocument());
        documentRepository.save(document);

        if (documentSubCategory == MY_NAME
                || (documentSubCategory == OTHER_TAX && !documentTaxGuarantorNaturalPersonForm.getNoDocument())) {
            List<MultipartFile> multipartFiles = documentTaxGuarantorNaturalPersonForm.getDocuments().stream().filter(f -> !f.isEmpty()).collect(Collectors.toList());
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
        if (documentSubCategory == OTHER_TAX && documentTaxGuarantorNaturalPersonForm.getNoDocument()) {
            document.setCustomText(documentTaxGuarantorNaturalPersonForm.getCustomText());
        }
        documentRepository.save(document);
        documentService.generatePdfByFilesOfDocument(document);
        tenant.lastUpdateDateProfile(LocalDateTime.now(), DocumentCategory.TAX);
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
