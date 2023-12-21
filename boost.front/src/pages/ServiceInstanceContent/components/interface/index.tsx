export interface ServiceInstanceContentProps {

    serviceInstanceId?: string;

    status?: string;

    onSourceChange: (source: string | undefined) => void; // 函数的类型定义
}