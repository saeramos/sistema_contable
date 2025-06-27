import React, { useState, useEffect } from 'react';
import {
  Autocomplete,
  TextField,
  CircularProgress,
  Box,
  Chip,
  Typography,
} from '@mui/material';
import { Search, Clear } from '@mui/icons-material';
import { motion, AnimatePresence } from 'framer-motion';

interface AutoCompleteFieldProps {
  label: string;
  value: any;
  onChange: (value: any) => void;
  options: any[];
  loading?: boolean;
  getOptionLabel: (option: any) => string;
  isOptionEqualToValue?: (option: any, value: any) => boolean;
  placeholder?: string;
  error?: boolean;
  helperText?: string;
  disabled?: boolean;
  multiple?: boolean;
  freeSolo?: boolean;
  onInputChange?: (event: any, value: string) => void;
  renderOption?: (props: any, option: any) => React.ReactNode;
  noOptionsText?: string;
  size?: 'small' | 'medium';
  fullWidth?: boolean;
}

const AutoCompleteField: React.FC<AutoCompleteFieldProps> = ({
  label,
  value,
  onChange,
  options,
  loading = false,
  getOptionLabel,
  isOptionEqualToValue,
  placeholder,
  error,
  helperText,
  disabled = false,
  multiple = false,
  freeSolo = false,
  onInputChange,
  renderOption,
  noOptionsText = 'No hay opciones disponibles',
  size = 'medium',
  fullWidth = true,
}) => {
  const [inputValue, setInputValue] = useState('');
  const [open, setOpen] = useState(false);

  const handleInputChange = (event: any, newInputValue: string) => {
    setInputValue(newInputValue);
    if (onInputChange) {
      onInputChange(event, newInputValue);
    }
  };

  const filteredOptions = options.filter((option) =>
    getOptionLabel(option).toLowerCase().includes(inputValue.toLowerCase())
  );

  return (
    <motion.div
      initial={{ opacity: 0, y: 10 }}
      animate={{ opacity: 1, y: 0 }}
      transition={{ duration: 0.3 }}
    >
      <Autocomplete
        value={value}
        onChange={(event, newValue) => {
          onChange(newValue);
        }}
        inputValue={inputValue}
        onInputChange={handleInputChange}
        options={filteredOptions}
        getOptionLabel={getOptionLabel}
        isOptionEqualToValue={isOptionEqualToValue}
        loading={loading}
        open={open}
        onOpen={() => setOpen(true)}
        onClose={() => setOpen(false)}
        multiple={multiple}
        freeSolo={freeSolo}
        disabled={disabled}
        size={size}
        fullWidth={fullWidth}
        noOptionsText={noOptionsText}
        renderInput={(params) => (
          <TextField
            {...params}
            label={label}
            placeholder={placeholder}
            error={error}
            helperText={helperText}
            InputProps={{
              ...params.InputProps,
              startAdornment: (
                <Box sx={{ display: 'flex', alignItems: 'center', mr: 1 }}>
                  <Search sx={{ color: 'text.secondary', fontSize: 20 }} />
                  {loading && (
                    <CircularProgress
                      size={16}
                      sx={{ ml: 1, color: 'text.secondary' }}
                    />
                  )}
                </Box>
              ),
            }}
            sx={{
              '& .MuiOutlinedInput-root': {
                borderRadius: 2,
                '&:hover': {
                  '& .MuiOutlinedInput-notchedOutline': {
                    borderColor: 'primary.main',
                  },
                },
                '&.Mui-focused': {
                  '& .MuiOutlinedInput-notchedOutline': {
                    borderColor: 'primary.main',
                    borderWidth: 2,
                  },
                },
              },
            }}
          />
        )}
        renderOption={renderOption || ((props, option) => (
          <Box component="li" {...props}>
            <Box sx={{ display: 'flex', flexDirection: 'column', width: '100%' }}>
              <Typography variant="body2" sx={{ fontWeight: 500 }}>
                {getOptionLabel(option)}
              </Typography>
              {option.description && (
                <Typography variant="caption" sx={{ color: 'text.secondary' }}>
                  {option.description}
                </Typography>
              )}
            </Box>
          </Box>
        ))}
        renderTags={(value, getTagProps) =>
          value.map((option, index) => (
            <motion.div
              key={index}
              initial={{ opacity: 0, scale: 0.8 }}
              animate={{ opacity: 1, scale: 1 }}
              exit={{ opacity: 0, scale: 0.8 }}
            >
              <Chip
                label={getOptionLabel(option)}
                {...getTagProps({ index })}
                size="small"
                sx={{
                  borderRadius: 1,
                  '& .MuiChip-deleteIcon': {
                    fontSize: 16,
                  },
                }}
              />
            </motion.div>
          ))
        }
        ListboxProps={{
          sx: {
            maxHeight: 300,
            '& .MuiAutocomplete-option': {
              borderRadius: 1,
              mx: 1,
              mb: 0.5,
              '&[aria-selected="true"]': {
                backgroundColor: 'primary.main',
                color: 'white',
                '&:hover': {
                  backgroundColor: 'primary.dark',
                },
              },
            },
          },
        }}
        sx={{
          '& .MuiAutocomplete-tag': {
            margin: 0.5,
          },
        }}
      />
    </motion.div>
  );
};

export default AutoCompleteField; 