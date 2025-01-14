package com.ovi.fileservice;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class FileServiceApplicationTests {

	@Autowired
	private FileDownloadController fileDownloadController;
	@Test
	void contextLoads() {
		assertThat(fileDownloadController).isNotNull();
	}

}
