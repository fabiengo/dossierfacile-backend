package fr.dossierfacile.api.front.partner.controller;

import fr.dossierfacile.api.front.model.tenant.TenantModel;
import fr.dossierfacile.api.front.register.enums.StepRegister;
import fr.dossierfacile.api.front.register.form.guarantor.natural_person.DocumentFinancialGuarantorNaturalPersonForm;
import fr.dossierfacile.api.front.register.form.guarantor.natural_person.DocumentIdentificationGuarantorNaturalPersonFileForm;
import fr.dossierfacile.api.front.register.form.guarantor.natural_person.DocumentIdentificationGuarantorNaturalPersonForm;
import fr.dossierfacile.api.front.register.form.guarantor.natural_person.DocumentProfessionalGuarantorNaturalPersonForm;
import fr.dossierfacile.api.front.register.form.guarantor.natural_person.DocumentResidencyGuarantorNaturalPersonForm;
import fr.dossierfacile.api.front.register.form.guarantor.natural_person.DocumentTaxGuarantorNaturalPersonForm;
import fr.dossierfacile.api.front.register.form.guarantor.natural_person.NameGuarantorNaturalPersonForm;
import fr.dossierfacile.api.front.service.interfaces.TenantService;
import fr.dossierfacile.api.front.validator.group.ApiPartner;
import fr.dossierfacile.common.entity.Tenant;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static org.springframework.http.ResponseEntity.ok;

@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/api-partner/register/guarantorNaturalPerson", consumes = MediaType.MULTIPART_FORM_DATA_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
public class ApiPartnerRegisterGuarantorNaturalPersonController {
    private final TenantService tenantService;

    @PreAuthorize("hasPermissionOnTenant(#documentIdentificationGuarantorNaturalPersonForm.tenantId)")
    @PostMapping("/documentIdentification")
    public ResponseEntity<TenantModel> documentIdentification(@Validated(ApiPartner.class) DocumentIdentificationGuarantorNaturalPersonForm documentIdentificationGuarantorNaturalPersonForm) {
        var tenant = tenantService.findById(documentIdentificationGuarantorNaturalPersonForm.getTenantId());
        var tenantModel = tenantService.saveStepRegister(tenant, documentIdentificationGuarantorNaturalPersonForm, StepRegister.DOCUMENT_IDENTIFICATION_GUARANTOR_NATURAL_PERSON);
        return ok(tenantModel);
    }

    @PreAuthorize("hasPermissionOnTenant(#documentIdentificationGuarantorNaturalPersonFileForm.tenantId)")
    @PostMapping("/documentIdentification/v2")
    public ResponseEntity<TenantModel> documentIdentificationFile(@Validated(ApiPartner.class) DocumentIdentificationGuarantorNaturalPersonFileForm documentIdentificationGuarantorNaturalPersonFileForm) {
        var tenant = tenantService.findById(documentIdentificationGuarantorNaturalPersonFileForm.getTenantId());
        var tenantModel = tenantService.saveStepRegister(tenant, documentIdentificationGuarantorNaturalPersonFileForm, StepRegister.DOCUMENT_IDENTIFICATION_GUARANTOR_NATURAL_PERSON_FILE);
        return ok(tenantModel);
    }

    @PreAuthorize("hasPermissionOnTenant(#nameGuarantorNaturalPersonForm.tenantId)")
    @PostMapping("/name")
    public ResponseEntity<TenantModel> guarantorName(@Validated(ApiPartner.class) NameGuarantorNaturalPersonForm nameGuarantorNaturalPersonForm) {
        var tenant = tenantService.findById(nameGuarantorNaturalPersonForm.getTenantId());
        var tenantModel = tenantService.saveStepRegister(tenant, nameGuarantorNaturalPersonForm, StepRegister.NAME_GUARANTOR_NATURAL_PERSON);
        return ok(tenantModel);
    }

    @PreAuthorize("hasPermissionOnTenant(#documentResidencyGuarantorNaturalPersonForm.tenantId)")
    @PostMapping("/documentResidency")
    public ResponseEntity<TenantModel> documentResidency(@Validated(ApiPartner.class) DocumentResidencyGuarantorNaturalPersonForm documentResidencyGuarantorNaturalPersonForm) {
        Tenant tenant = tenantService.findById(documentResidencyGuarantorNaturalPersonForm.getTenantId());
        TenantModel tenantModel = tenantService.saveStepRegister(tenant, documentResidencyGuarantorNaturalPersonForm, StepRegister.DOCUMENT_RESIDENCY_GUARANTOR_NATURAL_PERSON);
        return ok(tenantModel);
    }

    @PreAuthorize("hasPermissionOnTenant(#documentProfessionalGuarantorNaturalPersonForm.tenantId)")
    @PostMapping("/documentProfessional")
    public ResponseEntity<TenantModel> documentProfessional(@Validated(ApiPartner.class) DocumentProfessionalGuarantorNaturalPersonForm documentProfessionalGuarantorNaturalPersonForm) {
        var tenant = tenantService.findById(documentProfessionalGuarantorNaturalPersonForm.getTenantId());
        var tenantModel = tenantService.saveStepRegister(tenant, documentProfessionalGuarantorNaturalPersonForm, StepRegister.DOCUMENT_PROFESSIONAL_GUARANTOR_NATURAL_PERSON);
        return ok(tenantModel);
    }

    @PreAuthorize("hasPermissionOnTenant(#documentFinancialGuarantorNaturalPersonForm.tenantId)")
    @PostMapping("/documentFinancial")
    public ResponseEntity<TenantModel> documentFinancial(@Validated(ApiPartner.class) DocumentFinancialGuarantorNaturalPersonForm documentFinancialGuarantorNaturalPersonForm) {
        var tenant = tenantService.findById(documentFinancialGuarantorNaturalPersonForm.getTenantId());
        var tenantModel = tenantService.saveStepRegister(tenant, documentFinancialGuarantorNaturalPersonForm, StepRegister.DOCUMENT_FINANCIAL_GUARANTOR_NATURAL_PERSON);
        return ok(tenantModel);
    }


    @PreAuthorize("hasPermissionOnTenant(#documentTaxGuarantorNaturalPersonForm.tenantId)")
    @PostMapping("/documentTax")
    public ResponseEntity<TenantModel> documentTax(@Validated(ApiPartner.class) DocumentTaxGuarantorNaturalPersonForm documentTaxGuarantorNaturalPersonForm) {
        var tenant = tenantService.findById(documentTaxGuarantorNaturalPersonForm.getTenantId());
        var tenantModel = tenantService.saveStepRegister(tenant, documentTaxGuarantorNaturalPersonForm, StepRegister.DOCUMENT_TAX_GUARANTOR_NATURAL_PERSON);
        return ok(tenantModel);
    }
}
