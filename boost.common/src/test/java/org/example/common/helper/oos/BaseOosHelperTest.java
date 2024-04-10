package org.example.common.helper.oos;

import com.aliyun.oos20190601.models.GetParameterResponse;
import com.aliyun.oos20190601.models.GetSecretParameterResponse;
import com.aliyun.oos20190601.models.GetSecretParameterResponseBody;
import com.aliyun.oos20190601.models.UpdateParameterResponse;
import com.aliyun.oos20190601.models.UpdateSecretParameterResponse;
import java.util.Arrays;
import mockit.Expectations;
import mockit.Injectable;
import mockit.Mocked;
import mockit.Tested;
import org.example.common.BaseResult;
import org.example.common.adapter.OosClient;
import org.example.common.errorinfo.ErrorInfo;
import org.example.common.exception.BizException;
import org.example.common.model.ListConfigParametersModel;
import org.example.common.param.parameter.ListConfigParametersParam;
import org.example.common.param.parameter.UpdateConfigParameterParam;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class BaseOosHelperTest {

    @Tested
    private BaseOosHelper baseOosHelper;

    @Injectable
    private OosClient oosClientMock;

    @BeforeEach
    public void setup() {
        baseOosHelper = new BaseOosHelper(oosClientMock);
    }

    @Test
    public void testUpdateConfigParameterSuccess(@Mocked UpdateSecretParameterResponse secretResp,
                                                 @Mocked UpdateParameterResponse plainResp) {
        new Expectations() {{
            oosClientMock.updateSecretParameter(anyString, anyString);
            result = secretResp;

            oosClientMock.updateParameter(anyString, anyString);
            result = plainResp;
        }};

        UpdateConfigParameterParam updateEncryptedParam = new UpdateConfigParameterParam();
        updateEncryptedParam.setName("encrypted-param");
        updateEncryptedParam.setValue("secret-param-id");
        updateEncryptedParam.setEncrypted(true);
        BaseResult<Void> encryptedResult = baseOosHelper.updateConfigParameter(updateEncryptedParam);
        assertEquals("500", encryptedResult.getCode());

        UpdateConfigParameterParam updatePlainParam = new UpdateConfigParameterParam();
        updatePlainParam.setName("plain-param");
        updatePlainParam.setValue("plain-param-id");
        updatePlainParam.setEncrypted(false);
        BaseResult<Void> plainResult = baseOosHelper.updateConfigParameter(updatePlainParam);
        assertEquals("500", plainResult.getCode());
    }

    @Test
    public void testListConfigParameters(@Mocked GetSecretParameterResponse secretResp,
                                         @Mocked GetParameterResponse plainResp) {
        String[] names = {"encrypted-param", "plain-param"};
        Boolean[] encrypteds = {true, false};
        ListConfigParametersParam listParams = new ListConfigParametersParam();
        listParams.setName(Arrays.asList(names));
        listParams.setEncrypted(Arrays.asList(encrypteds));

        new Expectations() {{
            for (int i = 0; i < names.length; i++) {
                if (encrypteds[i]) {
                    oosClientMock.getSecretParameter(names[i]);
                    result = secretResp;
                } else {
                    oosClientMock.getParameter(names[i]);
                    result = plainResp;
                }
            }
        }};

        BaseResult<ListConfigParametersModel> listResult = baseOosHelper.listConfigParameters(listParams);
        assertEquals("200", listResult.getCode());

        ListConfigParametersModel listConfigParametersModel = listResult.getData();
        assertEquals(names.length, listConfigParametersModel.getConfigParameterModels().size());
    }

    @Test
    public void testGetSecretParameter_Success(@Mocked GetSecretParameterResponse responseMock) {
        String expectedParameterValue = "expected_secret_value";
        GetSecretParameterResponseBody responseBodyMock = new GetSecretParameterResponseBody();
        GetSecretParameterResponseBody.GetSecretParameterResponseBodyParameter parameterMock = new GetSecretParameterResponseBody.GetSecretParameterResponseBodyParameter();
        parameterMock.setValue(expectedParameterValue);
        responseBodyMock.setParameter(parameterMock);

        new Expectations() {{
            oosClientMock.getSecretParameter(anyString); result = responseMock;
            responseMock.getBody(); result = responseBodyMock;
        }};

        String actualParameterValue = baseOosHelper.getSecretParameter("test_name");
        assertEquals(expectedParameterValue, actualParameterValue);
    }

    @Test
    public void testGetSecretParameter_InternalError() {
        new Expectations() {{
            oosClientMock.getSecretParameter(anyString); result = new Exception("Some internal error");
        }};

        BizException exception = assertThrows(BizException.class, () -> baseOosHelper.getSecretParameter("error_name"));

        assertEquals(ErrorInfo.RESOURCE_NOT_FOUND.getCode(), exception.getCode());
        assertEquals(ErrorInfo.RESOURCE_NOT_FOUND.getMessage(), exception.getMessage());
    }
}