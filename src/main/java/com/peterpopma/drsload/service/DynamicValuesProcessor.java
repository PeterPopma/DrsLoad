package com.peterpopma.drsload.service;

import com.peterpopma.drsload.controller.DynamicValuesWrapper;

public class DynamicValuesProcessor {
    DynamicValuesWrapper dynamicValues = new DynamicValuesWrapper();

    public String replacePlusPlusWithNumberValue(String text) {
        while(text.contains("++"))
        {
            int index_begin = text.indexOf("++") + 2;
            int index_end = index_begin;
            while(Character.isDigit(text.charAt(index_end))) {
                index_end++;
            }

            int translated_value;
            if (dynamicValues.getDynamicParameters().size()<=dynamicValues.getIndex()) {
                // new value
                translated_value = Integer.valueOf(text.substring(index_begin, index_end));
                dynamicValues.getDynamicParameters().add(translated_value);
            } else {
                // existing value
                Integer value = dynamicValues.getDynamicParameters().get(dynamicValues.getIndex());
                value++;
                dynamicValues.getDynamicParameters().set(dynamicValues.getIndex(), value);
                translated_value = value;
            }
            text = text.substring(0, index_begin-2) + translated_value + text.substring(index_end);

            dynamicValues.setIndex(dynamicValues.getIndex()+1);
        }
        return text;
    }
}
