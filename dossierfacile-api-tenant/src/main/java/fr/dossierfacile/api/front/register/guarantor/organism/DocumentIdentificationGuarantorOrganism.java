package fr.dossierfacile.api.front.register.guarantor.organism;

import fr.dossierfacile.api.front.exception.GuarantorNotFoundException;
import fr.dossierfacile.api.front.mapper.TenantMapper;
import fr.dossierfacile.api.front.model.tenant.TenantModel;
import fr.dossierfacile.api.front.register.SaveStep;
import fr.dossierfacile.api.front.register.form.guarantor.organism.DocumentIdentificationGuarantorOrganismForm;
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

@Service
@AllArgsConstructor
public class DocumentIdentificationGuarantorOrganism implements SaveStep<DocumentIdentificationGuarantorOrganismForm> {

    private final OvhService ovhService;
    private final TenantRepository tenantRepository;
    private final DocumentRepository documentRepository;
    private final TenantMapper tenantMapper;
    private final GuarantorRepository guarantorRepository;
    private final FileRepository fileRepository;
    private final DocumentService documentService;

    @Override
    @Transactional
    public TenantModel saveStep(Tenant tenant, DocumentIdentificationGuarantorOrganismForm documentIdentificationGuarantorOrganismForm) {
        documentService.updateOthersDocumentsStatus(tenant);
        Guarantor guarantor = guarantorRepository.findByTenantAndTypeGuarantorAndId(tenant, TypeGuarantor.ORGANISM, documentIdentificationGuarantorOrganismForm.getGuarantorId())
                .orElseThrow(() -> new GuarantorNotFoundException(documentIdentificationGuarantorOrganismForm.getGuarantorId()));
        guarantor.setTenant(tenant);
        guarantorRepository.save(guarantor);

        DocumentSubCategory documentSubCategory = DocumentSubCategory.CERTIFICATE_VISA;
        Document document = documentRepository.findByDocumentCategoryAndGuarantor(DocumentCategory.IDENTIFICATION, guarantor)
                .orElse(Document.builder()
                        .documentCategory(DocumentCategory.IDENTIFICATION)
                        .guarantor(guarantor)
                        .build());
        document.setDocumentStatus(DocumentStatus.TO_PROCESS);
        document.setDocumentSubCategory(documentSubCategory);
        documentRepository.save(document);

        List<MultipartFile> multipartFiles = documentIdentificationGuarantorOrganismForm.getDocuments().stream().filter(f -> !f.isEmpty()).collect(Collectors.toList());
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
        documentService.generatePdfByFilesOfDocument(document);
        tenant.lastUpdateDateProfile(LocalDateTime.now(), DocumentCategory.IDENTIFICATION);
        return tenantMapper.toTenantModel(tenantRepository.save(tenant));
    }
}
