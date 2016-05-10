package com.github.vlastikcz.ciconsumer.web.controller.request;

import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;

@Component
public class ReleaseRequestValidator implements Validator {
    @Override
    public boolean supports(Class<?> validatedClass) {
        return ReleaseRequest.class.equals(validatedClass);
    }

    @Override
    public void validate(Object o, Errors errors) {
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "dateTime", "dateTime cannot be empty");
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "numberOfRelease", "numberOfRelease cannot be empty");
    }
}
