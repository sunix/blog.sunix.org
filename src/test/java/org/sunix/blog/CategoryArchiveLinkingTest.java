package org.sunix.blog;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class CategoryArchiveLinkingTest {

    @Test
    void categoryHashesAreUrlEncoded() {
        assertEquals("Web%20Dev%20%26%20AI", UrlEncodingExtension.hashEncoded("Web Dev & AI"),
            "Category hashes should be URL-encoded for safe archive links");
    }

    @Test
    void postLayoutCategoryBadgeLinksToFilteredArchives() throws IOException {
        String template = Files.readString(Path.of("templates/layouts/post.html"));

        assertTrue(template.contains("href=\"/archives#{page.data.category.hashEncoded}\""),
            "Post category badge should link to the archives page filtered by category");
        assertTrue(template.contains("class=\"post-category-link"),
            "Post category badge should expose a stable class for category-aware behavior");
    }

    @Test
    void articleCardCategoryBadgeLinksToFilteredArchives() throws IOException {
        String template = Files.readString(Path.of("templates/partials/article-card.html"));

        assertTrue(template.contains("href=\"/archives#{post.data.category.hashEncoded}\""),
            "Article card category badge should link to the archives page filtered by category");
        assertTrue(template.contains("class=\"post-category-link"),
            "Article card category badge should expose a stable class for category-aware behavior");
    }

    @Test
    void archivesPageContainsCategoryFilteringHook() throws IOException {
        String archivesPage = Files.readString(Path.of("content/archives.html"));

        assertTrue(archivesPage.contains("function filterByCategory()"),
            "Archives page should define category filtering logic");
        assertTrue(archivesPage.contains("post.querySelector('.post-category-link')"),
            "Archives page should read the rendered category badge when filtering posts");
        assertTrue(archivesPage.contains("window.addEventListener('hashchange', filterByCategory);"),
            "Archives page should react to category hash changes");
    }
}
