package com.github.vlastikcz.ciconsumer.web.api;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.github.vlastikcz.ciconsumer.domain.entity.CIRelease;

@RestController
@RequestMapping(CIReleaseController.CI_RELEASE_ENDPOINT_PATH)
public class CIReleaseController {
    static final String CI_RELEASE_ENDPOINT_PATH = "/ci-releases";

    private final CIReleaseControllerRequestValidator ciReleaseControllerRequestValidator;
    private final CIReleaseControllerRequestConverter ciReleaseControllerRequestConverter;

    @Autowired
    public CIReleaseController(CIReleaseControllerRequestValidator ciReleaseControllerRequestValidator,
                               CIReleaseControllerRequestConverter ciReleaseControllerRequestConverter) {
        this.ciReleaseControllerRequestValidator = ciReleaseControllerRequestValidator;
        this.ciReleaseControllerRequestConverter = ciReleaseControllerRequestConverter;
    }

    @InitBinder("CIReleaseControllerRequest")
    public void initBinder(WebDataBinder binder) {
        binder.addValidators(ciReleaseControllerRequestValidator);
    }

    @RequestMapping(method = RequestMethod.POST)
    public ResponseEntity<?> create(@RequestBody @Validated CIReleaseControllerRequest ciReleaseControllerRequest, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return invalidRequest(bindingResult.getFieldErrors());
        }

        final CIRelease ciRelease = ciReleaseControllerRequestConverter.convert(ciReleaseControllerRequest);
        return new ResponseEntity<>(ciRelease, HttpStatus.CREATED);
    }

    private static ResponseEntity<?> invalidRequest(List<FieldError> errors) {
        return new ResponseEntity<>(errors, HttpStatus.BAD_REQUEST);
    }
}
