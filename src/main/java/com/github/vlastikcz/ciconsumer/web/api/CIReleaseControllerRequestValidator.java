package com.github.vlastikcz.ciconsumer.web.api;

import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;

@Component
public class CIReleaseControllerRequestValidator implements Validator {
    @Override
    public boolean supports(Class<?> validatedClass) {
        return CIReleaseControllerRequest.class.equals(validatedClass);
    }

    @Override
    public void validate(Object o, Errors errors) {
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "dateTime", "dateTime cannot be empty");
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "numberOfRelease", "numberOfRelease cannot be empty");
    }
}
