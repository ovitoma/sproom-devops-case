package com.ovi.fileservice;

import com.ovi.fileservice.metrics.MicrometerRegistry;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith({SpringExtension.class})
@WebMvcTest({FileDownloadController.class, FileMonitor.class, MicrometerRegistry.class, RandomFile.class})
public class FileDownloadTest {
    @Autowired
    private MockMvc mockMvc;

    @Test
    void fileDownload() throws Exception {
        RequestBuilder rq = MockMvcRequestBuilders.get("/randomFile");
        MvcResult rs = mockMvc.perform(rq).andReturn();
        assertEquals(200, rs.getResponse().getStatus());
    }
}
