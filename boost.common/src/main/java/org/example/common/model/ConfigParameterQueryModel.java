package org.example.common.model;

import javax.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ConfigParameterQueryModel {

    @NotNull(message = "name is mandatory for this action.")
    private String name;
    @NotNull(message = "encrypted is mandatory for this action.")
    private Boolean encrypted;
}
