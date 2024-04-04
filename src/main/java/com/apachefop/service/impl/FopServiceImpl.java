package com.apachefop.service.impl;

import com.apachefop.service.FopService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.fop.apps.Fop;
import org.apache.fop.apps.FopFactory;
import org.apache.fop.apps.MimeConstants;
import org.springframework.core.io.ResourceLoader;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.sax.SAXResult;
import javax.xml.transform.stream.StreamSource;
import java.io.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class FopServiceImpl implements FopService {

    private final ResourceLoader resourceLoader;

    public ResponseEntity<byte[]> generateImage() throws Exception {
        File foFile = resourceLoader.getResource("classpath:template.fo").getFile();

        try (FileInputStream foInputStream = new FileInputStream(foFile);
             ByteArrayOutputStream pngOutputStream = new ByteArrayOutputStream()) {

            String foTemplate;
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(foInputStream))) {
                foTemplate = reader.lines().collect(Collectors.joining("\n"));
            }

            String transformedFO = foTemplate
                    .replace("${title}", "Manoj R")
                    .replace("${description}", "SDE")
                    .replace("${content}", "DIATOZ Solutions pvt ltd");

            Source foSource = new StreamSource(new StringReader(transformedFO));

            FopFactory fopFactory = FopFactory.newInstance(new File(".").toURI());
            Fop fop = fopFactory.newFop(MimeConstants.MIME_PNG, pngOutputStream);

            TransformerFactory factory = TransformerFactory.newInstance();
            Transformer transformer = factory.newTransformer();
            Result res = new SAXResult(fop.getDefaultHandler());

            transformer.transform(foSource, res);

            byte[] pngBytes = pngOutputStream.toByteArray();

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.IMAGE_PNG);
            headers.setContentLength(pngBytes.length);

            return new ResponseEntity<>(pngBytes, headers, HttpStatus.OK);
        }
    }
}
