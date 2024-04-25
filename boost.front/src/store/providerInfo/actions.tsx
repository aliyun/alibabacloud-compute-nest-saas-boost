export const SET_PROVIDER_NAME = 'SET_PROVIDER_NAME';
export const SET_PROVIDER_OFFICIAL_LINK = 'SET_PROVIDER_OFFICIAL_LINK';
export const SET_PROVIDER_DESCRIPTION = 'SET_PROVIDER_DESCRIPTION';

export const setProviderName = (name: string) => ({
    type: SET_PROVIDER_NAME,
    payload: name,
});

export const setProviderOfficialLink = (link: string) => ({
    type: SET_PROVIDER_OFFICIAL_LINK,
    payload: link,
});

export const setProviderDescription = (description: string) => ({
    type: SET_PROVIDER_DESCRIPTION,
    payload: description,
});
