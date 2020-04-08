package com.atguigu.ossservice.service;

import org.springframework.web.multipart.MultipartFile;

public interface FileService {
    String uploadFileOss(MultipartFile file);
}
