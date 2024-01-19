export interface CreateModalProps {

    createModalVisible: boolean;

    setCreateModalVisible: (visible: boolean) => void;

    handleCreateSubmit: () => void;
}

export interface Specification {
    Name: string;
    Parameters: { [key: string]: string[] };
    OrderList: string[];
    Type: string;
    Description: string;
}


export interface ParameterTypeInterfaceArray extends Record<string, ParameterTypeInterface> {}

export interface ParameterTypeInterface {
    Type?: string;
    NoEcho?: boolean;
    Label?: {
        en?: string;
        'zh-cn'?: string;
    };
    AllowedPattern?: string;
    MaxValue?: number;
    MinValue?: number;
    Default?: any;
    Description?: {
        'zh-cn': string;
        en: string;
    };
    AssociationProperty?: string;
    AssociationPropertyMetadata?: {
        Visible?: {
            Condition?: {
                'Fn::Equals': [any, any];
            };
        };
    };
    AllowedValues?: string[];
}

export interface ParameterGroupsInterface {
    "ALIYUN::ROS::Interface"
        : {
        TemplateTags: string[];
        ParameterGroups: {
            Parameters: string[];
            Label: {
                default: {
                    en: string;
                    'zh-cn': string;
                };
            };
        }[];
    };
}