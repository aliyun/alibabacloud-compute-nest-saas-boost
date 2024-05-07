package org.example.common.param.parameter;

import lombok.Data;
import java.io.Serializable;
import java.util.List;
import org.example.common.model.ConfigParameterQueryModel;

@Data
public class ListConfigParametersParam implements Serializable {

    private List<ConfigParameterQueryModel> configParameterQueryModels;
}