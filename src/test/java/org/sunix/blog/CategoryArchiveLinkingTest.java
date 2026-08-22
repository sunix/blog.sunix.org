package org.sunix.blog;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertTrue;

class CategoryArchiveLinkingTest {

    @Test
    void postLayoutCategoryBadgeLinksToFilteredArchives() throws IOException {
        String template = Files.readString(Path.of("templates/layouts/post.html"));

        assertTrue(template.contains("href=\"/archives#{page.data.category}\""),
            "Post category badge should link to the archives page filtered by category");
    }

    @Test
    void articleCardCategoryBadgeLinksToFilteredArchives() throws IOException {
        String template = Files.readString(Path.of("templates/partials/article-card.html"));

        assertTrue(template.contains("href=\"/archives#{post.data.category}\""),
            "Article card category badge should link to the archives page filtered by category");
    }

    @Test
    void archivesPageContainsCategoryFilteringHook() throws IOException {
        String archivesPage = Files.readString(Path.of("content/archives.html"));

        assertTrue(archivesPage.contains("data-category=\"{post.data.category}\""),
            "Archives page should expose each post category for client-side filtering");
        assertTrue(archivesPage.contains("function filterByCategory()"),
            "Archives page should define category filtering logic");
        assertTrue(archivesPage.contains("window.addEventListener('hashchange', filterByCategory);"),
            "Archives page should react to category hash changes");
    }
}
