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
