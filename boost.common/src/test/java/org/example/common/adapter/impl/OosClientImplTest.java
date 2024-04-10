package org.example.common.adapter.impl;

import com.aliyun.oos20190601.Client;
import com.aliyun.oos20190601.models.GetParameterRequest;
import com.aliyun.oos20190601.models.GetParameterResponse;
import com.aliyun.oos20190601.models.GetSecretParameterRequest;
import com.aliyun.oos20190601.models.GetSecretParameterResponse;
import com.aliyun.oos20190601.models.UpdateParameterRequest;
import com.aliyun.oos20190601.models.UpdateParameterResponse;
import com.aliyun.oos20190601.models.UpdateSecretParameterRequest;
import com.aliyun.oos20190601.models.UpdateSecretParameterResponse;
import com.aliyun.teaopenapi.models.Config;
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
    public void testGetSecretParameter(
            @Injectable final GetSecretParameterResponse expectedResponse) throws Exception {
        // Set expectations: when mockedClient.getSecretParameter is called with the above initialized request, return expectedResponse
        new Expectations() {{
            mockedClient.getSecretParameter((GetSecretParameterRequest) any);
            result = expectedResponse;
        }};

        // Call the method under test and verify the result
        GetSecretParameterResponse actualResponse = oosClient.getSecretParameter("testName");
        assertEquals(expectedResponse.getBody(), actualResponse.getBody());
    }

    @Test
    public void testUpdateSecretParameter(
            @Injectable UpdateSecretParameterResponse expectedResponse) throws Exception {
        // Mock the Client.updateSecretParameter method to return the expected response
        new Expectations() {{
            mockedClient.updateSecretParameter((UpdateSecretParameterRequest) any);
            result = expectedResponse;
        }};

        // Call the method under test and verify the result
        UpdateSecretParameterResponse actualResponse = oosClient.updateSecretParameter("testName", "testValue");
        assertEquals(expectedResponse, actualResponse);
    }

    @Test
    public void testGetParameter(
            @Injectable GetParameterResponse expectedResponse) throws Exception {
        // Mock the Client.getParameter method to return the expected response
        new Expectations() {{
            mockedClient.getParameter((GetParameterRequest) any);
            result = expectedResponse;
        }};

        // Call the method under test and verify the result
        GetParameterResponse actualResponse = oosClient.getParameter("testName");
        assertEquals(expectedResponse, actualResponse);
    }

    @Test
    public void testUpdateParameter(
            @Injectable UpdateParameterResponse expectedResponse) throws Exception {
        // Mock the Client.updateParameter method to return the expected response
        new Expectations() {{
            mockedClient.updateParameter((UpdateParameterRequest) any);
            result = expectedResponse;
        }};

        // Call the method under test and verify the result
        UpdateParameterResponse actualResponse = oosClient.updateParameter("testName", "testValue");
        assertEquals(expectedResponse, actualResponse);
    }

    @Test
    public void testCreateClientWithAliyunConfig(@Injectable AliyunConfig aliyunConfig) throws Exception {
        // Verify that the createClient method creates a Client using the AliyunConfig
        oosClient.createClient(aliyunConfig);
        new Verifications() {{
            Client client;
        }};
    }

    @Test
    public void testCreateClientWithAccessKeysAndToken(
            @Injectable String accessKeyId,
            @Injectable String accessKeySecret,
            @Injectable String securityToken) throws Exception {
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
    public void testCreateClientWithAccessKeysOnly(
            @Injectable String accessKeyId,
            @Injectable String accessKeySecret) throws Exception {
        // Verify that the createClient method creates a Client using only Access Key ID and Access Key Secret
        oosClient.createClient(accessKeyId, accessKeySecret);
        new Verifications() {{
            Client client;
        }};
    }
}