import {createOrder} from "@/services/backend/order";
import {Modal} from "antd";

export const handleAlipaySubmit = async (values: API.createOrderParams, index: number) => {

    const response = await createOrder(values);
    if (response.code !== '200') {
        Modal.error({
            title: 'Error',
            content: response.message,
        });
    }
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
    if (paymentForm != undefined) {
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