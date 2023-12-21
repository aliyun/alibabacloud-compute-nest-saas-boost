export function isIPv4Address(str: string): boolean {
    const pattern = /^(\d{1,3}\.){3}\d{1,3}$/;
    return pattern.test(str);
}

export function replaceUrlPlaceholders(urlTemplate: string, placeholders: { [key: string]: string }): string {
    return urlTemplate.replace(/\$\{(\w+)\}/g, (_, p1) => placeholders[p1] || '');
}