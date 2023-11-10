package org.example.common.param;

import lombok.Data;

import java.io.Serializable;

@Data
public class TemplateParameterParam implements Serializable {

    private static final long serialVersionUID = -956250259557053992L;

    private String parameterKey;

    private String parameterValue;
}
