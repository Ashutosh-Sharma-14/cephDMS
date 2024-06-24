export const reducer = (state, action) => {
    switch (action.type) {
      case 'INCREMENT':
        return { ...state, count: state.count + 1 };
      case 'DECREMENT':
        return { ...state, count: state.count - 1 };
      case 'SET_USER':
        return { ...state, user: action.payload };
    case 'm':
        return {};
      default:
        return state;
    }
  };


  export const initialState = {
    count: 0,
    user: null
  };