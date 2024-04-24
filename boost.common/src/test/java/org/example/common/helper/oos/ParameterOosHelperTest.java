package org.example.common.helper.oos;

import com.aliyun.oos20190601.models.GetParameterResponse;
import com.aliyun.oos20190601.models.GetSecretParameterResponse;
import com.aliyun.oos20190601.models.UpdateParameterResponse;
import com.aliyun.oos20190601.models.UpdateParameterResponseBody;
import com.aliyun.oos20190601.models.UpdateSecretParameterResponse;
import com.aliyun.oos20190601.models.UpdateSecretParameterResponseBody;
import com.aliyun.oos20190601.models.UpdateSecretParameterResponseBody.UpdateSecretParameterResponseBodyParameter;
import java.util.ArrayList;
import java.util.List;
import mockit.Expectations;
import mockit.Injectable;
import mockit.Mocked;
import static org.assertj.core.api.Assertions.assertThat;
import org.example.common.BaseResult;
import org.example.common.ListResult;
import org.example.common.adapter.OosClient;
import org.example.common.exception.BizException;
import org.example.common.model.ConfigParameterModel;
import org.example.common.model.ConfigParameterQueryModel;
import org.example.common.param.parameter.ListConfigParametersParam;
import org.example.common.param.parameter.UpdateConfigParameterParam;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class ParameterOosHelperTest {

    @Injectable
    private OosClient oosClient;

    private ParameterOosHelper parameterOosHelper;

    @BeforeEach
    public void setUp() {
        parameterOosHelper = new ParameterOosHelper(oosClient);
    }

    @Test
    public void testUpdateConfigParameterEncryptedTrue(@Mocked UpdateSecretParameterResponse updateSecretParameterResponse) {
        UpdateSecretParameterResponseBody responseBody = new UpdateSecretParameterResponseBody();
        UpdateSecretParameterResponseBodyParameter parameter = new UpdateSecretParameterResponseBodyParameter();
        parameter.setId("test-id");
        responseBody.setParameter(parameter);

        new Expectations() {{
            updateSecretParameterResponse.getBody(); result = responseBody;
        }};

        UpdateConfigParameterParam param = new UpdateConfigParameterParam();
        param.setName("test-param");
        param.setValue("test-value");
        param.setEncrypted(true);

        BaseResult<Void> result = parameterOosHelper.updateConfigParameter(param);

        assertEquals("OK", result.getMessage());
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
    public void testListConfigParameters(@Mocked GetParameterResponse getParameterResponse,
                                         @Mocked GetSecretParameterResponse getSecretParameterResponse) {
        ListConfigParametersParam listConfigParametersParam = new ListConfigParametersParam();
        List<ConfigParameterQueryModel> queries = new ArrayList<>();
        ConfigParameterQueryModel query1 = new ConfigParameterQueryModel();
        query1.setEncrypted(false);
        query1.setName("param1");
        queries.add(query1);
        ConfigParameterQueryModel query2 = new ConfigParameterQueryModel();
        query2.setEncrypted(true);
        query2.setName("param2");
        queries.add(query2);
        listConfigParametersParam.setConfigParameterQueryModels(queries);

        ConfigParameterModel model1 = new ConfigParameterModel();
        model1.setValue("id1");
        model1.setName("param1");
        ConfigParameterModel model2 = new ConfigParameterModel();
        model2.setValue("id2");
        model2.setName("param2");

        new Expectations() {{
            oosClient.getParameter("param1"); result = getParameterResponse;
            oosClient.getSecretParameter("param2"); result = getSecretParameterResponse;
        }};

        ListResult<ConfigParameterModel> result = parameterOosHelper.listConfigParameters(listConfigParametersParam);

        assertEquals("OK", result.getMessage());
        assertEquals(2, result.getData().size());
    }
    @Test
    public void testGetSecretParameter(@Mocked GetSecretParameterResponse getSecretParameterResponse) {
        String expectedValue = "secret-value";

        new Expectations() {{
            oosClient.getSecretParameter(anyString); result = getSecretParameterResponse;
            getSecretParameterResponse.getBody().getParameter().getValue(); result = expectedValue;
        }};

        String actualValue = parameterOosHelper.getSecretParameter("test-param");

        assertThat(actualValue).isEqualTo(expectedValue);
    }
}