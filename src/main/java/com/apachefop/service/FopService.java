package com.apachefop.service;

import org.springframework.http.ResponseEntity;

public interface FopService {
    ResponseEntity<byte[]> generateImage() throws Exception;
}
