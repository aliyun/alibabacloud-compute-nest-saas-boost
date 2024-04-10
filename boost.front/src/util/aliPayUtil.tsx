import {createOrder} from "@/services/backend/order";
import {Modal} from "antd";

export const handleAlipaySubmit = async (values: any, index: number) => {

    const response = await createOrder(values);
    //@ts-ignore
    if (response.code !== '200') {
        Modal.error({
            title: 'Error',
            //@ts-ignore
            content: response.message,
        });
    }
    //@ts-ignore
    const formString = response.data;
    const paymentForm = formString?.paymentForm;
    if (formString != undefined && paymentForm != undefined) {
        let divForm = document.getElementsByTagName('divform')
        for(let i=0;i<divForm.length;i++){
            console.log(divForm[i]);
        }
        document.forms.length
        const div = document.createElement('div');
        div.innerHTML = paymentForm;
        document.body.appendChild(div);
        document.forms[index].setAttribute('target', '_self')
        document.forms[index].submit();
    }
};

export const handlePaySubmit = async (paymentForm: string, index: number) => {
    console.log(paymentForm);
    if (paymentForm) {
        const div = document.createElement('div');
        div.style.display = 'none';
        div.innerHTML = paymentForm.trim();

        // 查找在div中创建的表单
        const form = div.querySelector('form');

        if (form) {
            form.setAttribute('target', '_self');
            document.body.appendChild(div);
            form.submit();
            document.body.removeChild(div);
        } else {
            console.error('No form found in paymentForm');
        }
    } else {
        console.error('No paymentForm provided');
    }
};
