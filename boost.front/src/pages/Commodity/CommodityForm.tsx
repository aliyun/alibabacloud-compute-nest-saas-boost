import React from 'react';
import { Form, Input, Button, Select, message } from 'antd';
// 导入创建和更新套餐的API方法

interface CommodityFormProps {
    commodity: API.CommodityDTO;
    // 可能还需要其他属性如套餐列表等
}

const CommodityForm: React.FC<CommodityFormProps> = ({ commodity }) => {
    const [form] = Form.useForm();

    const handleSubmit = async (values: API.CommoditySpecificationDTO) => {
        try {
            // 根据是否有specificationName判断是创建还是更新套餐
            if (values.specificationName) {
                // 调用更新套餐的API方法
                message.success('Specification updated successfully');
            } else {
                // 调用创建套餐的API方法
                message.success('Specification created successfully');
            }
        } catch (error) {
            message.error('Failed to submit specification');
        }
    };

    return (
        <Form form={form} onFinish={handleSubmit}>
            {/* 套餐设置表单字段 */}
            <Form.Item name="specificationName" label="Specification Name" rules={[{ required: true }]}>
                <Input />
            </Form.Item>
            {/* ... 其他套餐设置字段 ... */}
            <Form.Item>
                <Button type="primary" htmlType="submit">
                    {'Submit'}
                </Button>
            </Form.Item>
        </Form>
    );
};

export default CommodityForm;
