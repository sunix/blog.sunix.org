package org.sunix.blog;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class CategoryArchiveLinkingTest {

    private String readProjectFile(String relativePath) throws IOException {
        Path projectRoot = Path.of(System.getProperty("basedir", "."));
        return Files.readString(projectRoot.resolve(relativePath));
    }

    @Test
    void categoryHashesAreUrlEncoded() {
        assertEquals("Web%20Dev%20%26%20AI", UrlEncodingExtension.encodeForHash("Web Dev & AI"),
            "Category hashes should be URL-encoded for safe archive links");
    }

    @Test
    void postLayoutCategoryBadgeLinksToFilteredArchives() throws IOException {
        String template = readProjectFile("templates/layouts/post.html");

        assertTrue(template.contains("href=\"/archives#{page.data.category.encodeForHash}\""),
            "Post category badge should link to the archives page filtered by category");
        assertTrue(template.contains("class=\"post-category-link"),
            "Post category badge should expose a stable class for category-aware behavior");
    }

    @Test
    void articleCardCategoryBadgeLinksToFilteredArchives() throws IOException {
        String template = readProjectFile("templates/partials/article-card.html");

        assertTrue(template.contains("href=\"/archives#{post.data.category.encodeForHash}\""),
            "Article card category badge should link to the archives page filtered by category");
        assertTrue(template.contains("class=\"post-category-link"),
            "Article card category badge should expose a stable class for category-aware behavior");
    }

    @Test
    void archivesPageContainsCategoryFilteringHook() throws IOException {
        String archivesPage = readProjectFile("content/archives.html");

        assertTrue(archivesPage.contains("function filterByCategory()"),
            "Archives page should define category filtering logic");
        assertTrue(archivesPage.contains("function readSelectedCategory()"),
            "Archives page should extract the selected category through a dedicated helper");
        assertTrue(archivesPage.contains("return decodeURIComponent(hash);"),
            "Archives page should decode valid category hashes");
        assertTrue(archivesPage.contains("catch (error)"),
            "Archives page should fall back safely when category hash decoding fails");
        assertTrue(archivesPage.contains("postsContainer.querySelectorAll('.post-item')"),
            "Archives page should query the current post list when applying filters");
        assertTrue(archivesPage.contains("post.querySelector('.post-category-link')"),
            "Archives page should read the rendered category badge when filtering posts");
        assertTrue(archivesPage.contains("window.addEventListener('hashchange', filterByCategory);"),
            "Archives page should react to category hash changes");
    }
}
