import { createStore, combineReducers } from 'redux';
import { providerInfoReducer } from './providerInfo/reducer';
import { paymentMethodReducer } from './paymentMethod/reducer';

const rootReducer = combineReducers({
    providerInfo: providerInfoReducer,
    paymentMethod: paymentMethodReducer,
});

export const store = createStore(rootReducer);
