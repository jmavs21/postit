import React, { useState, useRef, useReducer } from 'react';

interface TextFieldProps {
  text: string;
  handleChange: (event: React.ChangeEvent<HTMLInputElement>) => void;
  children: (
    count: number,
    setCount: React.Dispatch<React.SetStateAction<number>>
  ) => JSX.Element | null;
}

type Actions = { type: 'add'; text: string } | { type: 'remove'; idx: number };

interface Todo {
  text: string;
  complete: boolean;
}

type State = Array<Todo>;

const TodoReducer = (state: State, action: Actions) => {
  switch (action.type) {
    case 'add':
      return [...state, { text: action.text, complete: false }];
    case 'remove':
      return state.filter((_, i) => action.idx !== i);
    default:
      return state;
  }
};

export const TextField: React.FC<TextFieldProps> = ({
  text,
  handleChange,
  children,
}) => {
  const [count, setCount] = useState(0);

  const inputRef = useRef<HTMLInputElement>(null);

  const [todos, dispatch] = useReducer(TodoReducer, []);

  return (
    <div>
      <input ref={inputRef} onChange={handleChange} />
      {JSON.stringify(todos)}
      <button
        onClick={() => {
          dispatch({ type: 'add', text: '...' });
        }}
      >
        +
      </button>
      <div>{children(count, setCount)}</div>
    </div>
  );
};

/**
 * 
 * <TextField
          text="hello"
          children={(count, setCount) => (
            <div>
              {count}
              <button onClick={() => setCount(count + 1)}>+</button>
            </div>
          )}
          handleChange={() => {}}
        />
 */
