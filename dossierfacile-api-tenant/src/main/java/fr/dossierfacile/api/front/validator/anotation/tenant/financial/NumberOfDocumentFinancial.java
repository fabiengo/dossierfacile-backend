package fr.dossierfacile.api.front.validator.anotation.tenant.financial;

import fr.dossierfacile.api.front.validator.tenant.financial.NumberOfDocumentFinancialValidator;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.TYPE, ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)

@Constraint(
        validatedBy = {NumberOfDocumentFinancialValidator.class}
)

public @interface NumberOfDocumentFinancial {
    String message() default "you say you have no documents";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
