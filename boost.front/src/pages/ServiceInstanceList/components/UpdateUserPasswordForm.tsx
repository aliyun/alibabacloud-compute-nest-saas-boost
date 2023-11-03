/*
*Copyright (c) Alibaba Group;
*Licensed under the Apache License, Version 2.0 (the "License");
*you may not use this file except in compliance with the License.
*You may obtain a copy of the License at

*   http://www.apache.org/licenses/LICENSE-2.0

*Unless required by applicable law or agreed to in writing, software
*distributed under the License is distributed on an "AS IS" BASIS,
*WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
*See the License for the specific language governing permissions and
*limitations under the License.
*/

import { Form, Input, Modal } from 'antd';


interface UpdateUserPasswordProps {
  open: boolean;
  onSubmit: (values: API.ServiceInstanceModel) => void;
  onCancel: () => void;
  values: API.ServiceInstanceModel
}

const UpdateUserPasswordForm: React.FC<UpdateUserPasswordProps> = ({
  open,
  onSubmit,
  onCancel,
  values,
}) => {
  let defaultUserName = ''
  try{
    const parameters = JSON.parse(values?.parameters as string)
    defaultUserName = parameters['UserName']
  } catch (error) {
  }

  const [form] = Form.useForm();
  return (
    <Modal
      open={open}
      title="修改密码"
      okText="修改成功"
      cancelText="取消"
      onCancel={onCancel}
      onOk={() => {
        form
          .validateFields()
          .then((values) => {
            form.resetFields();
            onSubmit(values);
          })
          .catch((info) => {
            console.log('Validate Failed:', info);
          });
      }}
    >
      <Form
        form={form}
        layout="vertical"
        name="form_in_modal"
        initialValues={{ modifier: 'public' }}
      >
        <Form.Item
          name="UserName"
          label="UserName"
          rules={[{ required: false, message: 'Please input the username!' }]}
        >
          <Input defaultValue={defaultUserName}/>
        </Form.Item>
        <Form.Item name="Password"
                   label="Password"
                   rules={[{ required: true, message: 'Please input the password!' }]}
        >
          <Input type="password" />
        </Form.Item>
      </Form>
    </Modal>
  );
};

export default UpdateUserPasswordForm;
