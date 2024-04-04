package com.apachefop.service.impl;

import com.apachefop.service.FopService;
import com.apachefop.utils.PdfToJpgConverter;
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

import javax.xml.transform.*;
import javax.xml.transform.sax.SAXResult;
import javax.xml.transform.stream.StreamSource;
import java.io.*;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class FopServiceImpl implements FopService {

    private final ResourceLoader resourceLoader;

    public ResponseEntity<byte[]> generateImage() throws Exception {
        File foFile = resourceLoader.getResource("classpath:template.fo").getFile();

        try (FileInputStream foInputStream = new FileInputStream(foFile);
             ByteArrayOutputStream jpgOutputStream = new ByteArrayOutputStream()) {

            // Read the FO template as a string
            String foTemplate;
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(foInputStream))) {
                foTemplate = reader.lines().collect(Collectors.joining("\n"));
            }

            // Replace placeholders with actual values
            String transformedFO = foTemplate
                    .replace("${title}", "Manoj R")
                    .replace("${description}", "SDE")
                    .replace("${content}", "DIATOZ Solutions pvt ltd");

            // Transform the transformed FO template
            Source foSource = new StreamSource(new StringReader(transformedFO));

            // Create FOP and generate PDF
            FopFactory fopFactory = FopFactory.newInstance(new File(".").toURI());
            Fop fop = fopFactory.newFop(MimeConstants.MIME_PDF, jpgOutputStream);

            TransformerFactory factory = TransformerFactory.newInstance();
            Transformer transformer = factory.newTransformer();
            Result res = new SAXResult(fop.getDefaultHandler());

            // Transform the FO source and generate PDF
            transformer.transform(foSource, res);
            jpgOutputStream.close();

            // Get the generated PDF bytes
            byte[] pdfBytes = jpgOutputStream.toByteArray();
            PdfToJpgConverter.convertPdfToJpg(pdfBytes);

            // Set HTTP headers
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_PDF);
            headers.setContentLength(pdfBytes.length);

            // Return the PDF bytes as ResponseEntity
            return new ResponseEntity<>(pdfBytes, headers, HttpStatus.OK);
        }
    }
}
