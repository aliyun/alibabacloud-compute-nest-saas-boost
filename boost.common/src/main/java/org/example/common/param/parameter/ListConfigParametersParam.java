package org.example.common.param.parameter;

import lombok.Data;
import java.io.Serializable;
import java.util.List;
import java.util.Map;

@Data
public class ListConfigParametersParam implements Serializable {

    private List<String> name;

    private List<Boolean> encrypted;

    private Map<String, String> tags;

}