package fr.dossierfacile.api.front.partner.controller;

import fr.dossierfacile.api.front.model.tenant.TenantModel;
import fr.dossierfacile.api.front.register.enums.StepRegister;
import fr.dossierfacile.api.front.register.form.guarantor.organism.DocumentIdentificationGuarantorOrganismForm;
import fr.dossierfacile.api.front.security.interfaces.AuthenticationFacade;
import fr.dossierfacile.api.front.service.interfaces.LogService;
import fr.dossierfacile.api.front.service.interfaces.TenantService;
import fr.dossierfacile.api.front.validator.group.ApiPartner;
import fr.dossierfacile.api.front.validator.group.DocumentIdentificationGuarantor;
import fr.dossierfacile.common.enums.LogType;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static org.springframework.http.ResponseEntity.ok;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api-partner/register/guarantorOrganism")
public class ApiPartnerRegisterGuarantorOrganismController {
    private final TenantService tenantService;
    private final AuthenticationFacade authenticationFacade;
    private final LogService logService;

    @PostMapping("/documentIdentification")
    public ResponseEntity<TenantModel> documentIdentification(@Validated({ApiPartner.class, DocumentIdentificationGuarantor.class}) DocumentIdentificationGuarantorOrganismForm documentIdentificationGuarantorOrganismForm) {
        var tenant = authenticationFacade.getTenant(documentIdentificationGuarantorOrganismForm.getTenantId());
        var tenantModel = tenantService.saveStepRegister(tenant, documentIdentificationGuarantorOrganismForm, StepRegister.DOCUMENT_IDENTIFICATION_GUARANTOR_ORGANISM);
        logService.saveLog(LogType.ACCOUNT_EDITED, tenantModel.getId());
        return ok(tenantModel);
    }
}
