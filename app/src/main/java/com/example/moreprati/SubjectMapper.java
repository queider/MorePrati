package com.example.moreprati;
import android.content.Context;
import android.content.res.Resources;


import android.content.res.Resources;

import java.util.HashMap;
import java.util.Map;

public class SubjectMapper {
    public static Map<String, Boolean> mapSubjects(String[] subjectArray, Context context) {
        // Create a map to store subjects and their status
        Map<String, Boolean> subjectMap = new HashMap<>();

        Resources res = context.getResources();
        String[] allSubjects = res.getStringArray(R.array.subjects);


        for (String subject : allSubjects) {
            // Check if the subject is in the input array
            boolean isInArray = containsIgnoreCase(subjectArray, subject);

            // Add the subject and its status to the map
            subjectMap.put(subject, isInArray);
        }

        return subjectMap;
    }

    public static Map<String, Boolean> mapCitySubjects(String[] subjectArray, String city, Context context) {
        // Create a map to store subjects and their status
        Map<String, Boolean> subjectMap = new HashMap<>();

        // Iterate over all possible subjects (adjust this list based on your needs)
        Resources res = context.getResources();
        String[] allSubjects = res.getStringArray(R.array.subjects);

        for (String subject : allSubjects) {
            // Check if the subject is in the input array
            boolean isInArray = containsIgnoreCase(subjectArray, subject);

            // Add the subject and its status to the map
            subjectMap.put(subject, isInArray);
        }

        return updateMapWithCity(subjectMap, city);
    }

    private static Map<String, Boolean> updateMapWithCity(Map<String, Boolean> map, String city) {
        Map<String, Boolean> updatedMap = new HashMap<>();
        for (Map.Entry<String, Boolean> entry : map.entrySet()) {
            String updatedKey = city + "_" + entry.getKey();
            updatedMap.put(updatedKey, entry.getValue());
        }
        return updatedMap;
    }
    // Helper method to check if a subject is in the array (case-insensitive)
    private static boolean containsIgnoreCase(String[] array, String subject) {
        for (String s : array) {
            if (s.equalsIgnoreCase(subject)) {
                return true;
            }
        }
        return false;
    }
    public static String mapToString(Map<String, Boolean> subjectMap) {
        StringBuilder stringBuilder = new StringBuilder();
        for (Map.Entry<String, Boolean> entry : subjectMap.entrySet()) {
            String subject = entry.getKey();
            boolean isSelected = entry.getValue();
            if (isSelected) {
                stringBuilder.append(subject);
                stringBuilder.append(", "); // Append comma after each selected subject
            }
        }
        // Remove the last comma and space
        if (stringBuilder.length() > 2) {
            stringBuilder.setLength(stringBuilder.length() - 2);
        }
        return stringBuilder.toString();
    }

}
