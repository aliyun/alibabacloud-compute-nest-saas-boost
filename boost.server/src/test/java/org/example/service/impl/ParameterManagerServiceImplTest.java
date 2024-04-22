package org.example.service.impl;

import mockit.Expectations;
import mockit.Injectable;
import mockit.Tested;
import org.example.common.BaseResult;
import org.example.common.ListResult;
import org.example.common.helper.oos.ParameterOosHelper;
import org.example.common.model.ConfigParameterModel;
import org.example.common.model.UserInfoModel;
import org.example.common.param.parameter.ListConfigParametersParam;
import org.example.common.param.parameter.UpdateConfigParameterParam;
import org.example.service.parameter.impl.ParameterManagerServiceImpl;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class ParameterManagerServiceImplTest {

    @Tested
    private ParameterManagerServiceImpl parameterManagerService;

    @Injectable
    private ParameterOosHelper parameterOosHelper;

    private final UserInfoModel userInfoModel = new UserInfoModel();
    private final ListConfigParametersParam listConfigParametersParam = new ListConfigParametersParam();
    private final UpdateConfigParameterParam updateConfigParameterParam = new UpdateConfigParameterParam();

    @BeforeEach
    void setUp() {
        // Optionally set up default values for test inputs here.
    }

    @Test
    void testListConfigParameters() {
        ListResult<ConfigParameterModel> expectedListResult = new ListResult<>();
        // Set up expected data in the result object.

        new Expectations() {{
            parameterOosHelper.listConfigParameters(listConfigParametersParam);
            result = expectedListResult;
        }};

        ListResult<ConfigParameterModel> actualListResult = parameterManagerService.listConfigParameters(userInfoModel, listConfigParametersParam);

        assertEquals(expectedListResult, actualListResult);
    }

    @Test
    void testUpdateConfigParameter() {
        BaseResult<Void> expectedResult = new BaseResult<>();
        // Set up expected data in the result object.

        new Expectations() {{
            parameterOosHelper.updateConfigParameter(updateConfigParameterParam);
            result = expectedResult;
        }};

        BaseResult<Void> actualResult = parameterManagerService.updateConfigParameter(userInfoModel, updateConfigParameterParam);

        assertEquals(expectedResult, actualResult);
    }

    @Test
    void testListConfigParametersWithNullResponse() {
        new Expectations() {{
            parameterOosHelper.listConfigParameters(listConfigParametersParam);
            result = null;
        }};

        ListResult<ConfigParameterModel> actualListResult = parameterManagerService.listConfigParameters(userInfoModel, listConfigParametersParam);

        assertNotNull(actualListResult); // Assert that the service returns a non-null ListResult object.
    }

    @Test
    void testUpdateConfigParameterWithNullResponse() {
        new Expectations() {{
            parameterOosHelper.updateConfigParameter(updateConfigParameterParam);
            result = null;
        }};

        BaseResult<Void> actualResult = parameterManagerService.updateConfigParameter(userInfoModel, updateConfigParameterParam);

        assertNotNull(actualResult); // Assert that the service returns a non-null BaseResult object.
    }
}