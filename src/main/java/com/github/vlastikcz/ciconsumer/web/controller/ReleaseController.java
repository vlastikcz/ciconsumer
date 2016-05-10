package com.github.vlastikcz.ciconsumer.web.controller;

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

import com.github.vlastikcz.ciconsumer.domain.entity.ReleaseDetail;
import com.github.vlastikcz.ciconsumer.service.ReleaseDetailStateService;
import com.github.vlastikcz.ciconsumer.web.controller.request.ReleaseRequest;
import com.github.vlastikcz.ciconsumer.web.controller.request.ReleaseRequestConverter;
import com.github.vlastikcz.ciconsumer.web.controller.request.ReleaseRequestValidator;

@RestController
@RequestMapping(ReleaseController.RELEASE_ENDPOINT_PATH)
public class ReleaseController {
    static final String RELEASE_ENDPOINT_PATH = "/releases";

    private final ReleaseRequestValidator releaseRequestValidator;
    private final ReleaseRequestConverter releaseRequestConverter;
    private final ReleaseDetailStateService releaseDetailStateService;

    @Autowired
    public ReleaseController(ReleaseRequestValidator releaseRequestValidator,
                             ReleaseRequestConverter releaseRequestConverter,
                             ReleaseDetailStateService releaseDetailStateService) {
        this.releaseRequestValidator = releaseRequestValidator;
        this.releaseRequestConverter = releaseRequestConverter;
        this.releaseDetailStateService = releaseDetailStateService;
    }

    @InitBinder("releaseRequest")
    public void initBinder(WebDataBinder binder) {
        binder.addValidators(releaseRequestValidator);
    }

    @RequestMapping(method = RequestMethod.POST)
    public ResponseEntity<?> create(@RequestBody @Validated ReleaseRequest releaseRequest, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return invalidRequest(bindingResult.getFieldErrors());
        }

        final ReleaseDetail releaseDetail = releaseRequestConverter.convert(releaseRequest);
        releaseDetailStateService.create(releaseDetail);

        return new ResponseEntity<>(releaseDetail, HttpStatus.CREATED);
    }

    private static ResponseEntity<?> invalidRequest(List<FieldError> errors) {
        return new ResponseEntity<>(errors, HttpStatus.BAD_REQUEST);
    }
}
