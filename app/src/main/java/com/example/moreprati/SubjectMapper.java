package com.example.moreprati;

import java.util.HashMap;
import java.util.Map;

public class SubjectMapper {
    public static Map<String, Boolean> mapSubjects(String[] subjectArray) {
        // Create a map to store subjects and their status
        Map<String, Boolean> subjectMap = new HashMap<>();

        // Iterate over all possible subjects (adjust this list based on your needs)
        String[] allSubjects = {"מתמטיקה", "אנגלית", "עברית", "מוזיקה", "פסנטר","גיטרה"};

        for (String subject : allSubjects) {
            // Check if the subject is in the input array
            boolean isInArray = containsIgnoreCase(subjectArray, subject);

            // Add the subject and its status to the map
            subjectMap.put(subject, isInArray);
        }

        return subjectMap;
    }

    public static Map<String, Boolean> mapCitySubjects(String[] subjectArray, String city) {
        // Create a map to store subjects and their status
        Map<String, Boolean> subjectMap = new HashMap<>();

        // Iterate over all possible subjects (adjust this list based on your needs)
        String[] allSubjects = {"מתמטיקה", "אנגלית", "עברית", "מוזיקה", "פסנטר","גיטרה"};

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
}
