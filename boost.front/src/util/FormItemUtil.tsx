import {
    ParameterTypeInterface,
    Specification
} from "@/pages/ServiceInstanceList/components/interface/ServiceMetadataInterface";
import {ProFormDependency, ProFormDigit, ProFormSelect, ProFormText} from "@ant-design/pro-form";
import {getServiceTemplateParameterConstraints} from "@/services/backend/serviceManager";
import React, {useContext} from "react";
import {RuleObject} from "antd/lib/form";


export function createFormItem(key: string, value: ParameterTypeInterface, templateName: string | undefined, specifications: Specification[], specificationParameterList: string[], form: ReturnType<typeof useContext>,
) {
    const parsedValue = value as ParameterTypeInterface;
    const noEcho = parsedValue?.NoEcho ?? false;
    const allowPattern = parsedValue?.AllowedPattern;
    const patternRegEx = allowPattern !== undefined ? new RegExp(allowPattern) : undefined;
    const label = parsedValue?.Label != undefined ? parsedValue?.Label['zh-cn'] : "";
    const defaultValue = parsedValue?.Default;

    const parsedType = parsedValue?.Type?.toLowerCase() ?? "";

    const dependencyWithSpecification = isDependencyWithSpecification(key, specificationParameterList);

    if (parsedType === 'boolean') {
        return createBooleanFormItem(defaultValue, label, key);
    }
    if (key === "ZoneId") {
        return createZoneIdFormItem(templateName, key, label);
    }

    if (dependencyWithSpecification) {
        return createSpecificationFormItem(specifications, key, label, patternRegEx, form);
    }

    if (parsedType === 'string') {
        return createStringFormItem(defaultValue, label, key, noEcho, allowPattern, patternRegEx, parsedValue);
    }

    if (parsedType === 'number') {
        const minValue = parsedValue?.MinValue;
        const maxValue = parsedValue?.MaxValue;
        return createNumberFormItem(defaultValue, label, key, minValue, maxValue, allowPattern, patternRegEx);
    }

    return createDefaultFormItem(defaultValue, label, key, allowPattern, patternRegEx);
}

function createBooleanFormItem(defaultValue: any, label: string | undefined, key: string) {
    return (
        <ProFormSelect
            initialValue={defaultValue}
            valueEnum={{true: '是', false: '否'}}
            label={label}
            key={key}
            name={key}
            rules={[{required: true}]}
        />
    );
}

function createZoneIdFormItem(templateName: string | undefined, key: string, label: string | undefined) {
    return (
        <ProFormDependency name={["RegionId"]}>
            {({RegionId}) => {
                let regionParameters: { label: string, value: string }[] = [];

                const request = async () => {
                    if (templateName != undefined && RegionId != undefined) {
                        const res = await getServiceTemplateParameterConstraints({
                            templateName: templateName,
                            serviceId: "",
                            deployRegionId: RegionId
                        });

                        if (res !== undefined && res.data !== undefined) {
                            res.data.forEach((parameter: API.GetServiceTemplateParameterConstraintsResponseBodyParameterConstraints) => {
                                if (parameter.parameterKey === "ZoneId" && parameter.allowedValues !== undefined && parameter.allowedValues !== null && parameter.allowedValues.length > 0) {
                                    regionParameters = parameter.allowedValues?.map((item) => {
                                        return {label: item, value: item};
                                    });
                                }
                            });
                        }
                    }
                    return regionParameters;
                };

                return (
                    <ProFormSelect
                        name={key}
                        key={key}
                        label={"部署可用区"}
                        params={{
                            templateName: templateName,
                            serviceId: "",
                            deployRegionId: RegionId
                        }}
                        request={request}
                        rules={[{required: true}]}
                    />
                );
            }}
        </ProFormDependency>
    );
}

function createSpecificationFormItem(specifications: Specification[], key: string, label: string | undefined, patternRegEx: RegExp | undefined, form: ReturnType<typeof useContext>,
) {
    // let selectedValue = undefined;
    return (
        <ProFormDependency
            name={["SpecificationName"]}
            shouldUpdate={(prevValues, curValues) => prevValues.type !== curValues.type}
        >
            {({SpecificationName}) => {
                let currentSpecificationParameters: string[] = [];
                for (let specification of specifications) {
                    if (specification.Name == SpecificationName) {
                        currentSpecificationParameters = specification.Parameters[key];
                    }
                }
                const request = async () => {
                    return currentSpecificationParameters?.map((value) => {
                        return {label: value, value: value};
                    });
                }

                if (currentSpecificationParameters !== undefined) {
                    return (
                        <ProFormSelect
                            dependencies={["SpecificationName"]}
                            label={label}
                            key={key}
                            name={key}
                            request={request}
                            params={SpecificationName}
                            rules={[{required: true}]}
                        />
                    )
                } else {
                    return (
                        <ProFormText
                            label={label}
                            key={key}
                            name={key}
                            rules={[{pattern: patternRegEx, message: label, required: true}]}
                        />
                    );
                }
            }}
        </ProFormDependency>
    );
}

function createStringFormItem(defaultValue: any, label: string | undefined, key: string, noEcho: boolean, allowPattern: string | undefined, patternRegEx: RegExp | undefined, parsedValue: ParameterTypeInterface) {
    const allowedValues = parsedValue.AllowedValues ?? [];

    if (allowedValues.length > 0) {
        const valueEnum = allowedValues.reduce((obj, value) => {
            obj[value] = value;
            return obj;
        }, {} as { [key: string]: string });

        return (
            <ProFormSelect
                initialValue={defaultValue}
                label={label}
                key={key}
                name={key}
                valueEnum={valueEnum}
                rules={[{required: true}]}
            />
        );
    }

    return noEcho ? (
        <ProFormText.Password
            initialValue={parsedValue.Default}
            label={label}
            key={key}
            name={key}
            rules={[
                {required: true, message: label},
                validatePassword,
            ]}
        />
    ) : (
        <ProFormText
            initialValue={defaultValue}
            label={label}
            key={key}
            name={key}
            rules={[{pattern: patternRegEx, message: label, required: true}]}
        />
    );
}

function createNumberFormItem(defaultValue: any, label: string | undefined, key: string, minValue: number | undefined, maxValue: number | undefined, allowPattern: string | undefined, patternRegEx: RegExp | undefined) {
    return (
        <ProFormDigit
            initialValue={defaultValue}
            label={label}
            key={key}
            name={key}
            rules={[{pattern: patternRegEx, message: label, required: true}]}
            min={minValue}
            max={maxValue}
        />
    );
}

function createDefaultFormItem(defaultValue: any, label: string | undefined, key: string, allowPattern: string | undefined, patternRegEx: RegExp | undefined) {
    return (
        <ProFormText
            initialValue={defaultValue}
            label={label}
            key={key}
            name={key}
            rules={[{pattern: patternRegEx, message: label, required: true}]}
        />
    );
}

export function isDependencyWithSpecification(parameterName: string, specificationParameterList: string[]) {
    return specificationParameterList.includes(parameterName);
}

export const validatePassword: RuleObject = {
    validator(_, value) {
        if (value === undefined || value === null || value === '') {
            return Promise.reject('密码不能为空');
        }
        const password = value.trim(); // 删除首尾空格
        const uppercaseRegex = /[A-Z]/;
        const lowercaseRegex = /[a-z]/;
        const digitRegex = /\d/;
        const specialCharRegex = /[()\-`~!@#$%^&*_+=|{}[\]:;'<>,.?/]/;

        const conditions = [
            uppercaseRegex.test(password),
            lowercaseRegex.test(password),
            digitRegex.test(password),
            specialCharRegex.test(password),
        ];
        const validConditionsCount = conditions.filter(condition => condition).length;
        if (validConditionsCount < 3 || password.length < 8 || password.length > 30 || value.includes(' ')) {
            return Promise.reject('密码必须包含大写字母、小写字母、数字和特殊字符中的其中三个，且长度为8-30，不能包含空格');
        }
        return Promise.resolve();
    },
};