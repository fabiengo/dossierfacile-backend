package fr.dossierfacile.api.front.validator.anotation.guarantor.natural_person.tax;

import fr.dossierfacile.api.front.validator.guarantor.natural_person.tax.NumberOfDocumentTaxGuarantorNaturalPersonValidator;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.TYPE, ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)

@Constraint(
        validatedBy = {NumberOfDocumentTaxGuarantorNaturalPersonValidator.class}
)

public @interface NumberOfDocumentTaxGuarantorNaturalPerson {
    String message() default "numbers of documents incorrect";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
