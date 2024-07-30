package org.example.common.helper.oos;

import com.aliyun.oos20190601.models.GetSecretParametersResponseBody.GetSecretParametersResponseBodyParameters;
import com.aliyun.oos20190601.models.GetSecretParametersResponse;
import com.aliyun.oos20190601.models.GetSecretParametersResponseBody;
import com.aliyun.oos20190601.models.UpdateParameterResponse;
import com.aliyun.oos20190601.models.UpdateParameterResponseBody;
import com.aliyun.oos20190601.models.UpdateSecretParameterResponse;
import com.aliyun.oos20190601.models.UpdateSecretParameterResponseBody;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import mockit.Expectations;
import mockit.Injectable;
import mockit.Mocked;
import mockit.Tested;
import org.example.common.BaseResult;
import org.example.common.adapter.BaseAlipayClient;
import org.example.common.adapter.BaseWechatPayClient;
import org.example.common.adapter.OosClient;
import org.example.common.exception.BizException;
import org.example.common.model.ConfigParameterModel;
import org.example.common.param.parameter.UpdateConfigParameterParam;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class ParameterOosHelperTest {

    @Tested
    private ParameterOosHelper parameterOosHelper;

    @Injectable
    private OosClient oosClient;

    @Injectable
    private BaseAlipayClient baseAlipayClient;

    @Injectable
    private BaseWechatPayClient baseWechatPayClient;

    @BeforeEach
    public void setUp() {
        parameterOosHelper = new ParameterOosHelper(oosClient);
    }

    @Test
    public void testUpdateConfigParameterEncryptedTrue() throws Exception {
        UpdateSecretParameterResponse mockResponse = new UpdateSecretParameterResponse();
        UpdateSecretParameterResponseBody mockResponseBody = new UpdateSecretParameterResponseBody();
        UpdateSecretParameterResponseBody.UpdateSecretParameterResponseBodyParameter mockResponseBodyParameter = new UpdateSecretParameterResponseBody.UpdateSecretParameterResponseBodyParameter();

        // Assuming the ID is some non-empty string to denote a successful operation
        final String parameterId = "test-id";
        mockResponseBodyParameter.setId(parameterId);
        mockResponseBody.setParameter(mockResponseBodyParameter);
        mockResponse.setBody(mockResponseBody);

        UpdateConfigParameterParam param = new UpdateConfigParameterParam();
        param.setName("test-param");
        param.setValue("test-value");
        param.setTag("Alipay");
        param.setEncrypted(true);

        new Expectations() {{
            oosClient.updateSecretParameter(param.getName(), param.getValue());
            result = mockResponse;

            baseAlipayClient.updateClient("test-param", "test-value");
        }};

        BaseResult<Void> result = parameterOosHelper.updateConfigParameter(param);

        assertEquals("200", result.getCode());
    }

    @Test
    public void testUpdateConfigParameterEncryptedFalse(@Mocked UpdateParameterResponse updateParameterResponse) {
        UpdateParameterResponseBody responseBody = new UpdateParameterResponseBody();
        UpdateParameterResponseBody.UpdateParameterResponseBodyParameter parameter = new UpdateParameterResponseBody.UpdateParameterResponseBodyParameter();
        parameter.setId("test-id");
        responseBody.setParameter(parameter);

        new Expectations() {{
            updateParameterResponse.getBody(); result = responseBody;
        }};

        UpdateConfigParameterParam param = new UpdateConfigParameterParam();
        param.setName("test-param");
        param.setValue("test-value");
        param.setTag("WechatPay");
        param.setEncrypted(false);

        BaseResult<Void> result = parameterOosHelper.updateConfigParameter(param);

        assertEquals("OK", result.getMessage());
    }

    @Test
    public void testUpdateConfigParameterInvalidInput(@Mocked UpdateSecretParameterResponse updateSecretParameterResponse) {
        UpdateConfigParameterParam param = new UpdateConfigParameterParam();
        param.setName("test-param");
        param.setValue("test-value");
        param.setEncrypted(null); // Invalid input

        assertThrows(BizException.class, () -> parameterOosHelper.updateConfigParameter(param));
    }
    @Test
    public void testGetSecretParameter(@Mocked GetSecretParametersResponse getSecretParametersResponse) {
        List<ConfigParameterModel> expectedValue = new ArrayList<>();
        ConfigParameterModel model1 = new ConfigParameterModel();
        model1.setName("param1");
        model1.setValue("value1");
        expectedValue.add(model1);
        List<GetSecretParametersResponseBodyParameters> parameters = new ArrayList<>();
        GetSecretParametersResponseBodyParameters parameter = new GetSecretParametersResponseBody.GetSecretParametersResponseBodyParameters();
        parameter.setName("param1");
        parameter.setValue("value1");
        parameters.add(parameter);

        new Expectations() {{
            oosClient.listSecretParameters((List<String>)any); result = getSecretParametersResponse;
            getSecretParametersResponse.getBody().getParameters(); result = parameters;
        }};

        List<ConfigParameterModel> actualValue = parameterOosHelper.listSecretParameters(Collections.singletonList("param1"));

        assertThat(actualValue).isEqualTo(expectedValue);
    }
}