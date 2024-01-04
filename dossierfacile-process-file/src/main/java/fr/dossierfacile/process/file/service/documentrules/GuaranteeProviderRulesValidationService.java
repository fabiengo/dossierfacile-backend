package fr.dossierfacile.process.file.service.documentrules;

import fr.dossierfacile.common.entity.*;
import fr.dossierfacile.common.entity.ocr.GuaranteeProviderFile;
import fr.dossierfacile.common.enums.DocumentCategory;
import fr.dossierfacile.common.enums.DocumentSubCategory;
import fr.dossierfacile.common.enums.ParsedStatus;
import fr.dossierfacile.process.file.util.PersonNameComparator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@Service
@RequiredArgsConstructor
@Slf4j
public class GuaranteeProviderRulesValidationService implements RulesValidationService {
    @Override
    public boolean shouldBeApplied(Document document) {
        return document.getDocumentCategory() == DocumentCategory.IDENTIFICATION
                && document.getDocumentSubCategory() == DocumentSubCategory.CERTIFICATE_VISA;
    }


    private boolean checkNamesRule(Document document, GuaranteeProviderFile parsedFile) {
        Tenant tenant = document.getTenant();
        return parsedFile.getNames().stream().anyMatch(
                (fullname) -> PersonNameComparator.bearlyEqualsTo(fullname.firstName(), tenant.getFirstName())
                        && PersonNameComparator.bearlyEqualsTo(fullname.lastName(), tenant.getLastName()));
    }

    private boolean checkValidityRule(GuaranteeProviderFile parsedFile) {
        LocalDate validityDate = LocalDate.parse(parsedFile.getValidityDate(), DateTimeFormatter.ofPattern("dd/MM/yyyy"));
        return validityDate.isAfter(LocalDate.now());
    }

    @Override
    public DocumentAnalysisReport process(Document document, DocumentAnalysisReport report) {

        try {
            if (CollectionUtils.isEmpty(document.getFiles())
                    || document.getFiles().get(0).getParsedFileAnalysis() == null
                    || document.getFiles().get(0).getParsedFileAnalysis().getParsedFile() == null) {
                report.setAnalysisStatus(DocumentAnalysisStatus.UNDEFINED);
                return report;
            }

            GuaranteeProviderFile parsedFile = (GuaranteeProviderFile) document.getFiles().get(0).getParsedFileAnalysis().getParsedFile();
            if (parsedFile.getStatus() == ParsedStatus.INCOMPLETE) {
                log.error("Document was not correctly parsed :" + document.getTenant().getId());
                report.setAnalysisStatus(DocumentAnalysisStatus.UNDEFINED);
            } else if (!checkNamesRule(document, parsedFile)) {
                log.error("Document names mismatches :" + document.getTenant().getId());
                report.getBrokenRules().add(DocumentBrokenRule.builder()
                        .rule(DocumentRule.R_GUARANTEE_NAMES)
                        .message(DocumentRule.R_GUARANTEE_NAMES.getDefaultMessage())
                        .build());
                report.setAnalysisStatus(DocumentAnalysisStatus.DENIED);
            } else if (!checkValidityRule(parsedFile)) {
                log.error("Document is expired :" + document.getTenant().getId());
                report.getBrokenRules().add(DocumentBrokenRule.builder()
                        .rule(DocumentRule.R_GUARANTEE_EXIRED)
                        .message(DocumentRule.R_GUARANTEE_EXIRED.getDefaultMessage())
                        .build());
                report.setAnalysisStatus(DocumentAnalysisStatus.DENIED);
            } else {
                report.setAnalysisStatus(DocumentAnalysisStatus.CHECKED);
            }

        } catch (Exception e) {
            log.error("Error during the rules validation execution pocess");
            report.setAnalysisStatus(DocumentAnalysisStatus.UNDEFINED);
        }
        return report;
    }

}