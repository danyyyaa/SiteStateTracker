package ru.softaria.sitestatetracker;

import java.util.*;
import java.util.stream.Collectors;

public class SiteStateTracker {

    public static String generateChangesReport(Map<String, String> yesterdayState, Map<String, String> todayState) {
        Objects.requireNonNull(yesterdayState, "Yesterday's state map is null.");
        Objects.requireNonNull(todayState, "Today's state map is null.");

        StringBuilder report = new StringBuilder();
        report.append("Здравствуйте, дорогая и.о. секретаря\n\n");
        report.append("За последние сутки во вверенных Вам сайтах произошли следующие изменения:\n");

        List<String> disappearedPages = findDisappearedPages(yesterdayState.keySet(), todayState.keySet());
        List<String> newPages = findNewPages(yesterdayState.keySet(), todayState.keySet());
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

    private static List<String> findDisappearedPages(Set<String> yesterdayUrls, Set<String> todayUrls) {
        return yesterdayUrls.stream()
                .filter(url -> !todayUrls.contains(url))
                .collect(Collectors.toCollection(LinkedList::new));
    }

    private static List<String> findNewPages(Set<String> yesterdayUrls, Set<String> todayUrls) {
        return todayUrls.stream()
                .filter(url -> !yesterdayUrls.contains(url))
                .collect(Collectors.toCollection(LinkedList::new));
    }

    private static List<String> findChangedPages(Map<String, String> yesterdayState, Map<String, String> todayState) {
        return yesterdayState.entrySet().stream()
                .filter(entry -> todayState.containsKey(entry.getKey()) &&
                        !Objects.equals(todayState.get(entry.getKey()), entry.getValue()))
                .map(Map.Entry::getKey)
                .collect(Collectors.toCollection(LinkedList::new));
    }

    private static void appendPageList(StringBuilder report, Collection<String> pages) {
        StringJoiner joiner = new StringJoiner("\n");
        pages.forEach(joiner::add);
        report.append(" - ").append(joiner).append("\n");
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
