package fr.dossierfacile.api.front.register.form.guarantor.legal_person;

import fr.dossierfacile.api.front.validator.anotation.DocumentSubcategorySubset;
import fr.dossierfacile.api.front.validator.anotation.Extension;
import fr.dossierfacile.api.front.validator.anotation.SizeFile;
import fr.dossierfacile.api.front.validator.anotation.guarantor.NumberOfDocumentGuarantor;
import fr.dossierfacile.api.front.validator.anotation.guarantor.natural_person.ExistGuarantor;
import fr.dossierfacile.api.front.validator.enums.TypeDocumentValidation;
import fr.dossierfacile.common.enums.DocumentCategory;
import fr.dossierfacile.common.enums.DocumentSubCategory;
import fr.dossierfacile.common.enums.TypeGuarantor;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;

import static fr.dossierfacile.common.enums.DocumentSubCategory.FRENCH_IDENTITY_CARD;
import static fr.dossierfacile.common.enums.DocumentSubCategory.FRENCH_PASSPORT;
import static fr.dossierfacile.common.enums.DocumentSubCategory.FRENCH_RESIDENCE_PERMIT;
import static fr.dossierfacile.common.enums.DocumentSubCategory.OTHER_IDENTIFICATION;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DocumentIdentificationRepresentanGuarantorLegalPersonForm {

    @NotNull
    @ExistGuarantor(typeGuarantor = TypeGuarantor.LEGAL_PERSON)
    private Long guarantorId;

    @NotBlank
    private String firstName;

    @NotNull
    @DocumentSubcategorySubset(anyOf =
            {FRENCH_IDENTITY_CARD, FRENCH_PASSPORT, FRENCH_RESIDENCE_PERMIT, OTHER_IDENTIFICATION})
    private DocumentSubCategory typeDocumentIdentification;

    @NumberOfDocumentGuarantor(max = 15, documentCategory = DocumentCategory.IDENTIFICATION, typeGuarantor = TypeGuarantor.LEGAL_PERSON)
    @SizeFile(max = 5, typeDocumentValidation = TypeDocumentValidation.PER_DOCUMENT)
    private List<@Extension MultipartFile> documents = new ArrayList<>();

}
