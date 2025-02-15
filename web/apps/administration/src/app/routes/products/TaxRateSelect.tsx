import { Select, MenuItem, FormControl, InputLabel, SelectChangeEvent, SelectProps } from "@mui/material";
import { useGetTaxRatesQuery } from "@api";
import * as React from "react";

export interface TaxRateSelectProps extends Omit<SelectProps, "value" | "onChange" | "margin"> {
  label: string;
  value: string;
  helperText?: string;
  margin?: SelectProps["margin"] | "normal";
  onChange: (name: string) => void;
}

export const TaxRateSelect: React.FC<TaxRateSelectProps> = ({
  label,
  value,
  onChange,
  error,
  helperText,
  margin,
  ...props
}) => {
  const { data: taxRates } = useGetTaxRatesQuery();

  const handleChange = (evt: SelectChangeEvent<string>) => {
    onChange(evt.target.value);
  };

  return (
    <FormControl fullWidth margin={margin}>
      <InputLabel variant={props.variant} id="taxRateSelectLabel">
        {label}
      </InputLabel>
      <Select labelId="taxRateSelectLabel" value={value} onChange={handleChange as any} error={error} {...props}>
        {(taxRates ?? []).map((taxRate) => (
          <MenuItem key={taxRate.name} value={taxRate.name}>
            {taxRate.description} ({taxRate.rate}%)
          </MenuItem>
        ))}
      </Select>
    </FormControl>
  );
};
