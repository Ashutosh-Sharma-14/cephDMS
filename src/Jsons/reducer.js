import actionRequired from './actionReducer.json'

export const reducer = (state, action) => {
    switch (action.type) {
      case actionRequired.logout:
        return { ...state, count: state.count + 1 };
      default:
        return state;
    }
  };


  export const initialState = {
    login:null,
    logout:null
  };