package ru.softaria;

import java.util.*;
import java.util.stream.Collectors;

public class SiteStateTracker {

    public static String generateChangesReport(Map<String, String> yesterdayState, Map<String, String> todayState) {
        if (yesterdayState == null && todayState == null) {
            throw new IllegalArgumentException("Both yesterdayState and todayState maps are null.");
        } else if (yesterdayState == null) {
            throw new IllegalArgumentException("Yesterday's state map is null.");
        } else if (todayState == null) {
            throw new IllegalArgumentException("Today's state map is null.");
        }

        StringBuilder report = new StringBuilder();
        report.append("Здравствуйте, дорогая и.о. секретаря\n\n");
        report.append("За последние сутки во вверенных Вам сайтах произошли следующие изменения:\n");

        List<String> disappearedPages = findDifferencePages(yesterdayState.keySet(), todayState.keySet(), "исчезли");
        List<String> newPages = findDifferencePages(yesterdayState.keySet(), todayState.keySet(), "появились");
        List<String> changedPages = findChangedPages(yesterdayState, todayState);

        if (!disappearedPages.isEmpty()) {
            report.append("\nИсчезли следующие страницы:\n");
            appendPageList(report, disappearedPages);
        }

        if (!newPages.isEmpty()) {
            report.append("\nПоявились следующие новые страницы:\n");
            appendPageList(report, newPages);
        }

        if (!changedPages.isEmpty()) {
            report.append("\nИзменились следующие страницы:\n");
            appendPageList(report, changedPages);
        }

        report.append("\nС уважением,\nавтоматизированная система мониторинга.");
        return report.toString();
    }

    private static List<String> findDifferencePages(Set<String> yesterdayUrls, Set<String> todayUrls, String differenceType) {
        return yesterdayUrls.stream()
                .filter(url -> !todayUrls.contains(url))
                .map(url -> differenceType + ": " + url)
                .collect(Collectors.toList());
    }

    private static List<String> findChangedPages(Map<String, String> yesterdayState, Map<String, String> todayState) {
        return yesterdayState.entrySet().stream()
                .filter(entry -> todayState.containsKey(entry.getKey()) &&
                        !todayState.get(entry.getKey()).equals(entry.getValue()))
                .map(entry -> "изменилась: " + entry.getKey())
                .collect(Collectors.toList());
    }

    private static void appendPageList(StringBuilder report, List<String> pages) {
        for (String page : pages) {
            report.append(" - ").append(page).append("\n");
        }
    }

    public static void main(String[] args) {
        Map<String, String> yesterdayState = Map.of(
                "https://example.com/page1", "<html>...</html>",
                "https://example.com/page2", "<html>...</html>",
                "https://example.com/page3", "<html>...</html>"
        );

        Map<String, String> todayState = Map.of(
                "https://example.com/page1", "<html>...</html>",
                "https://example.com/page3", "<html>...</html>",
                "https://example.com/page4", "<html>...</html>",
                "https://example.com/page5", "<html>...</html>"
        );

        try {
            String report = SiteStateTracker.generateChangesReport(yesterdayState, todayState);
            System.out.println(report);
        } catch (IllegalArgumentException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }
}
