package com.apachefop.controller;

import com.apachefop.service.FopService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@Slf4j
public class FOPController {

    private final FopService fopService;

    @GetMapping("/generateImage")
    public ResponseEntity<byte[]> generateImage() throws Exception {
        return fopService.generateImage();
    }

}
