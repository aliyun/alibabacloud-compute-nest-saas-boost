
export const centsToYuan = (cents: number | undefined | string): string | undefined => {
    if (typeof cents === 'undefined') {
        return undefined;
    }

    // 如果cents是字符串，尝试将其转换为数字
    if (typeof cents === 'string') {
        const parsed = Number(cents);
        // 在无效字符串（如，非数字的情况下），返回undefined或者抛出一个错误
        if (isNaN(parsed)) {
            console.error('Invalid string to convert to yuan:', cents);
            return undefined;
        }
        cents = parsed;
    }

    // 确保cents是一个数字后，执行转换
    return (cents / 100).toFixed(2);
};


export const yuanToCents = (yuan: number | undefined): number | undefined => {
    if (yuan === undefined) {
        return undefined;
    }
    return Math.round(yuan * 100);
};