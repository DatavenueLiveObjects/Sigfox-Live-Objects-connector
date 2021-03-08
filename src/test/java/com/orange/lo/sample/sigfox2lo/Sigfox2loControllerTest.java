package com.orange.lo.sample.sigfox2lo;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.orange.lo.sample.sigfox2lo.sigfox.model.DataUpDto;
import com.orange.lo.sdk.LOApiClient;
import com.orange.lo.sdk.externalconnector.DataManagementExtConnector;
import com.orange.lo.sdk.externalconnector.model.DataMessage;
import com.orange.lo.sdk.rest.devicemanagement.Inventory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import static com.orange.lo.sdk.rest.devicemanagement.Inventory.XCONNECTOR_DEVICES_PREFIX;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(classes = {Sigfox2loTestConfiguration.class})
@AutoConfigureMockMvc
@ExtendWith({SpringExtension.class, MockitoExtension.class})
class Sigfox2loControllerTest {

    @Autowired
    private LOApiClient loApiClient;
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    private Inventory inventory;
    private DataManagementExtConnector dataManagementExtConnector;

    @BeforeEach
    void setUp() {
        inventory = loApiClient.getDeviceManagement().getInventory();
        dataManagementExtConnector = loApiClient.getDataManagementExtConnector();
    }

    @Test
    void shouldPassDataToLoApiWhenDataUpIsEndpointIsCalled() throws Exception {

        DataUpDto dataUpDto = getDataUpDto();
        String dataUpJson = objectMapper.writeValueAsString(dataUpDto);

        mockMvc.perform(post("/dataUp")
                .contentType(MediaType.APPLICATION_JSON)
                .content(dataUpJson)
        ).andExpect(status().isOk());

        verify(inventory, times(1))
                .createDevice(XCONNECTOR_DEVICES_PREFIX + dataUpDto.getDevice(), "0ZWkDm");
        verify(dataManagementExtConnector, times(1))
                .sendMessage(eq(dataUpDto.getDevice()), argThat(dataMessage -> haveSameValue(dataUpJson, dataMessage)));
    }

    private DataUpDto getDataUpDto() {
        DataUpDto dataUpDto = new DataUpDto();
        dataUpDto.setDevice("N0D3ID1");
        dataUpDto.setDeviceTypeId("5fc8c7d80");
        dataUpDto.setSeqNumber(10L);
        dataUpDto.setTime(1610964015L);
        dataUpDto.setData("abcDFE01");
        return dataUpDto;
    }

    private boolean haveSameValue(String dataUpJson, DataMessage dataMessage) {
        String ca = null;
        try {
            ca = objectMapper.writeValueAsString(dataMessage.getValue());
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        return dataUpJson.equalsIgnoreCase(ca);
    }
}