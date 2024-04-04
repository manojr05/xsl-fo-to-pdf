package com.apachefop.service;

import org.springframework.http.ResponseEntity;

import java.util.List;

public interface FopService {
    ResponseEntity<byte[]> generateImage() throws Exception;
}
