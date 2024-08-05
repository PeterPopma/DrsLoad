package com.peterpopma.drsload.service;

import com.peterpopma.drsload.controller.DynamicValuesWrapper;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@Slf4j
public final class CommandUtils {

    private static Random random = new Random();

    private CommandUtils() {
    }


    public static String getFirstValueNotEmpty(String value1, String value2) {
        if (value1!=null && !value1.isEmpty()) {
            return value1;
        }

        return value2;
    }

    public static List<String> getFirstValueNotEmpty(List<String> value1, String value2) {
        if (value1!=null && !value1.isEmpty()) {
            return value1;
        }

        List<String> value2List = new ArrayList<>();
        value2List.add(value2);
        return value2List;
    }

    public static String replacePlusPlusWithNumberValue(DynamicValuesWrapper dynamicValues, String text) {
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

    public static String replaceQuestionMarkQuestionMarkWitRandomValue(String text) {
        while(text.contains("??"))
        {
            int index_begin = text.indexOf("??") + 2;
            int index_end = index_begin;
            while(Character.isDigit(text.charAt(index_end))) {
                index_end++;
            }

            int random_value = random.nextInt(Integer.valueOf(text.substring(index_begin, index_end)));

            text = text.substring(0, index_begin-2) + random_value + text.substring(index_end);
        }
        return text;
    }


    private static String getTagValue(String xml, String tagName) {
        String value = "";
        try {
            value = xml.split("<"+tagName+">")[1].split("</"+tagName+">")[0];
        } catch (ArrayIndexOutOfBoundsException e) {
            log.debug("could not parse XML response.", e);
        }
        return value;
    }

    public static String extractHandle(String response) {
        return getTagValue(response, "contact:id");
    }

    public static String extractToken(String response) {
        return getTagValue(response, "domain:pw");
    }
}
