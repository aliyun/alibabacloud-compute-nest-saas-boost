package org.example.common.adapter.impl;

import com.aliyun.oos20190601.Client;
import com.aliyun.oos20190601.models.GetParametersRequest;
import com.aliyun.oos20190601.models.GetParametersResponse;
import com.aliyun.oos20190601.models.GetSecretParametersRequest;
import com.aliyun.oos20190601.models.GetSecretParametersResponse;
import com.aliyun.oos20190601.models.UpdateParameterRequest;
import com.aliyun.oos20190601.models.UpdateParameterResponse;
import com.aliyun.oos20190601.models.UpdateSecretParameterRequest;
import com.aliyun.oos20190601.models.UpdateSecretParameterResponse;
import com.aliyun.teaopenapi.models.Config;
import java.util.Collections;
import mockit.Expectations;
import mockit.Injectable;
import mockit.Tested;
import mockit.Verifications;
import org.example.common.config.AliyunConfig;
import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class OosClientImplTest {

    @Tested
    private OosClientImpl oosClient;

    @Injectable
    private Client mockedClient;

    @BeforeEach
    public void setUp() {
        oosClient = new OosClientImpl();
    }

    @Test
    public void testListSecretParameters() throws Exception {
        GetSecretParametersResponse expectedResponse = new GetSecretParametersResponse();
        new Expectations() {{
            mockedClient.getSecretParameters((GetSecretParametersRequest) any);
            result = expectedResponse;
        }};

        // Call the method under test and verify the result
        GetSecretParametersResponse actualResponse = oosClient.listSecretParameters(Collections.singletonList("testName"));
        assertEquals(expectedResponse.getBody(), actualResponse.getBody());
    }

    @Test
    public void testUpdateSecretParameter() throws Exception {
        UpdateSecretParameterResponse expectedResponse = new UpdateSecretParameterResponse();
        new Expectations() {{
            mockedClient.updateSecretParameter((UpdateSecretParameterRequest) any);
            result = expectedResponse;
        }};

        // Call the method under test and verify the result
        UpdateSecretParameterResponse actualResponse = oosClient.updateSecretParameter("testName", "testValue");
        assertEquals(expectedResponse, actualResponse);
    }

    @Test
    public void testGetParameter() throws Exception {
        GetParametersResponse expectedResponse = new GetParametersResponse();
        new Expectations() {{
            mockedClient.getParameters((GetParametersRequest) any);
            result = expectedResponse;
        }};

        // Call the method under test and verify the result
        GetParametersResponse actualResponse = oosClient.listParameters(Collections.singletonList("testName"));
        assertEquals(expectedResponse, actualResponse);
    }

    @Test
    public void testUpdateParameter() throws Exception {
        UpdateParameterResponse expectedResponse = new UpdateParameterResponse();
        new Expectations() {{
            mockedClient.updateParameter((UpdateParameterRequest) any);
            result = expectedResponse;
        }};

        // Call the method under test and verify the result
        UpdateParameterResponse actualResponse = oosClient.updateParameter("testName", "testValue");
        assertEquals(expectedResponse, actualResponse);
    }

    @Test
    public void testCreateClientWithAliyunConfig() throws Exception {
        AliyunConfig aliyunConfig = new AliyunConfig();
        // Verify that the createClient method creates a Client using the AliyunConfig
        oosClient.createClient(aliyunConfig);
        new Verifications() {{
            Client client;
        }};
    }

    @Test
    public void testCreateClientWithAccessKeysAndToken() throws Exception {
        String accessKeyId = "testAccessKeyId";
        String accessKeySecret = "testAccessKeySecret";
        String securityToken = "testSecurityToken";
        // Verify that the createClient method creates a Client using Access Key ID, Access Key Secret, and Security Token
        oosClient.createClient(accessKeyId, accessKeySecret, securityToken);
        new Verifications() {{
            Client client;
            new Client(withEqual(new Config()
                    .setAccessKeyId(accessKeyId)
                    .setAccessKeySecret(accessKeySecret)
                    .setSecurityToken(securityToken)
                    .setEndpoint(withSubstring("oos.your-region-id.aliyuncs.com"))));
        }};
    }

    @Test
    public void testCreateClientWithAccessKeysOnly() throws Exception {
        String accessKeyId = "testAccessKeyId";
        String accessKeySecret = "testAccessKeySecret";
        // Verify that the createClient method creates a Client using only Access Key ID and Access Key Secret
        oosClient.createClient(accessKeyId, accessKeySecret);
        new Verifications() {{
            Client client;
        }};
    }
}