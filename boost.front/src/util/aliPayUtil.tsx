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
    if (formString != undefined) {
        let divForm = document.getElementsByTagName('divform')
        for(let i=0;i<divForm.length;i++){
            console.log(divForm[i]);
        }
        document.forms.length
        const div = document.createElement('div');
        div.innerHTML = formString;
        document.body.appendChild(div);
        document.forms[index].setAttribute('target', '_self')
        document.forms[index].submit();
    }
};