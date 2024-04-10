package org.example.common.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ListConfigParametersModel {
    
    private List<ConfigParameterModel> configParameterModels;

}