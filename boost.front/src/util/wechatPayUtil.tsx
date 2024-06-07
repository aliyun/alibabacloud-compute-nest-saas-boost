export const handleWechatPaySubmit = async (QR_code_url: string | undefined) => {
    if (!QR_code_url) {
        console.error('No QR code URL provided');
        return;
    }

    // 创建用于显示二维码的图片元素
    const qrImg = document.createElement('img');
    qrImg.src = QR_code_url;
    qrImg.alt = 'Wechat QR Code';
    qrImg.style.width = '200px'; // 设定二维码的宽度，也可以使用其他尺寸
    qrImg.style.height = '200px'; // 设定二维码的高度
    qrImg.style.margin = '15px'; // 添加边距，让二维码居中显示

    // 创建一个Modal或者Div元素来包裹这个图片，以便显示给用户
    const modalDiv = document.createElement('div');
    modalDiv.style.position = 'fixed';
    modalDiv.style.left = '50%';
    modalDiv.style.top = '50%';
    modalDiv.style.transform = 'translate(-50%, -50%)';
    modalDiv.style.border = '1px solid #000';
    modalDiv.style.backgroundColor = '#fff';
    modalDiv.style.padding = '20px';
    modalDiv.style.boxShadow = '0 4px 8px 0 rgba(0,0,0,0.2)';
    modalDiv.style.textAlign = 'center';
    modalDiv.style.zIndex = '1000'; // 确保二维码悬浮在页面内容之上

    // 添加关闭按钮，允许用户关闭二维码视图
    const closeButton = document.createElement('button');
    closeButton.textContent = 'Close';
    closeButton.onclick = () => {
        document.body.removeChild(modalDiv);
    };

    // 将二维码图片和关闭按钮添加到Modal中
    modalDiv.appendChild(qrImg);
    modalDiv.appendChild(closeButton);

    // 将Modal添加到文档中，使其可见
    document.body.appendChild(modalDiv);
};
