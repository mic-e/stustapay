import * as React from "react";
import { TextField, TextFieldProps } from "@mui/material";

export type NumericInputProps = {
  onChange: (value: number) => void;
  value?: number | undefined;
} & Omit<TextFieldProps, "value" | "onChange" | "onBlur" | "onKeyUp">;

export const NumericInput: React.FC<NumericInputProps> = ({ value, onChange, ...props }) => {
  const [internalValue, setInternalValue] = React.useState("");

  React.useEffect(() => {
    setInternalValue(String(value));
  }, [value, setInternalValue]);

  const onInternalChange: React.ChangeEventHandler<HTMLInputElement> = (event) => {
    setInternalValue(event.target.value);
  };

  const propagateChange = () => {
    const parsedValue = parseFloat(internalValue);
    if (!isNaN(parsedValue)) {
      setInternalValue(String(parsedValue));
      onChange(parsedValue);
    }
  };

  const onInternalBlur = () => {
    propagateChange();
  };

  const onKeyUp = (event: React.KeyboardEvent<HTMLDivElement>) => {
    if (event.key === "Enter") {
      propagateChange();
    }
  };

  return (
    <TextField
      value={internalValue}
      onChange={onInternalChange}
      onBlur={onInternalBlur}
      onKeyUp={onKeyUp}
      variant="standard"
      {...props}
    />
  );
};
