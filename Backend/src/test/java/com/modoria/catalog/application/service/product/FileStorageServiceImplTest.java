package com.modoria.catalog.application.service.product;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThatCode;

class FileStorageServiceImplTest {

    private final FileStorageServiceImpl service = new FileStorageServiceImpl();

    @Test
    void deleteFile_withBlankPath_doesNothing() {
        assertThatCode(() -> service.deleteFile(" ")).doesNotThrowAnyException();
    }
}
