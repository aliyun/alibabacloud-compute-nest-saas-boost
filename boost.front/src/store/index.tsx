import { createStore, combineReducers } from 'redux';
import { providerInfoReducer } from './providerInfo/reducer';

const rootReducer = combineReducers({
    providerInfo: providerInfoReducer,
});

export const store = createStore(rootReducer);
