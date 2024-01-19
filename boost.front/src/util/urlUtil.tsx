export function isIPv4Address(str: string): boolean {
    const pattern = /^(\d{1,3}\.){3}\d{1,3}$/;
    return pattern.test(str);
}

export function replaceUrlPlaceholders(urlTemplate: string, placeholders: { [key: string]: string }): string {
    return urlTemplate.replace(/\$\{(\w+)\}/g, (_, p1) => placeholders[p1] || '');
}

export function getHashSearchParams() {
    const hash = window.location.hash;
    const indexOfQueryStart = hash.indexOf('?');
    if (indexOfQueryStart > -1) {
        const queryPart = hash.substring(indexOfQueryStart + 1);
        return new URLSearchParams(queryPart);
    }
    return new URLSearchParams();
}

export function removeSpmFromHashUrl(url: string): string {
    const spmRegex = /([?&])spm=[^&]*&?/;
    const hashIndex = url.indexOf('#');

    if (hashIndex !== -1) {
        const beforeHash = url.slice(0, hashIndex);
        const afterHash = url.slice(hashIndex);
        // 移除 spm 参数
        const newBeforeHash = beforeHash.replace(spmRegex, (match, p1) => p1 === '?' ? '?' : '');
        return newBeforeHash + afterHash;
    }

    return url;
}
