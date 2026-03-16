package vn.hoidanit.jobhunter.controller;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import vn.hoidanit.jobhunter.domain.response.file.ResUploadFileDTO;
import vn.hoidanit.jobhunter.service.FileService;
import vn.hoidanit.jobhunter.utils.anotations.ApiMessage;
import vn.hoidanit.jobhunter.utils.error.StorageException;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URISyntaxException;
import java.time.Instant;
import java.util.Arrays;
import java.util.List;

@RestController
@RequestMapping("api/v1")
@Validated
public class FileUploadController {

    @Value("${hoidanit.upload-file.base-uri}")
    private String baseUri;

    private FileService fileService;

    public FileUploadController(FileService fileService) {
        this.fileService = fileService;
    }

    @PostMapping("/files")
    @ApiMessage("Upload single file!")
    public ResponseEntity<ResUploadFileDTO> upload(@Valid
            @RequestParam("file") MultipartFile file,
            @RequestParam("folder") String folder) throws URISyntaxException, IOException,StorageException {
        // check validated
        if (file == null &&  file.isEmpty()) {
            throw new StorageException("File is empty .Please upload a file!");
        }
        //
        String fileName = file.getOriginalFilename();
        List<String> alowedExtentions = Arrays.asList("pdf","png","jpg","jpeg","doc","txt");
        boolean ísValid = alowedExtentions.stream().anyMatch(item->fileName.toLowerCase().endsWith(item));
        if (!ísValid) {
            throw new StorageException("Invalid extention Exception , only alow : " + alowedExtentions.toString());
        }
        // tạo folder
        this.fileService.createNewFolder(baseUri + folder); // vi o  trong application co "/" nen k can nua
        //save file to folder
        String uploadFile = this.fileService.storeFileInFolder(file, folder);
        ResUploadFileDTO res = new ResUploadFileDTO(uploadFile, Instant.now());


        return ResponseEntity.status(HttpStatus.CREATED.value()).body(res);
    }
    @GetMapping("/downloads")
    @ApiMessage("Download a file !")
    public ResponseEntity<Resource>  download(@RequestParam(name = "fileName" ,required = false) String fileName,
    @RequestParam(name="folder",required = false) String folder) throws StorageException, URISyntaxException, FileNotFoundException {
        if (fileName == null || folder == null){
            throw new StorageException("Missing required param !");

        }
        long fileLength = this.fileService.getFileLength(fileName,folder);
        if ( fileLength == 0 ) {
            throw new StorageException("File name with = " + fileName + " not found");
        }
        // download
        InputStreamResource resource = this.fileService.getResource(fileName ,folder);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION,"attachment; filename=\"" + fileName+"\"")
                .contentLength(fileLength)
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(resource);

    }
}

