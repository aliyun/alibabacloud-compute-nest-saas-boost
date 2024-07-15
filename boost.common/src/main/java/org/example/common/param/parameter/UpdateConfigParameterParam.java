package org.example.common.param.parameter;

import java.io.Serializable;
import javax.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UpdateConfigParameterParam implements Serializable {

    @NotNull(message = "name is mandatory for this action.")
    private String name;

    @NotNull(message = "value is mandatory for this action.")
    private String value;

    private Boolean encrypted = false;

    private String tag;
}
