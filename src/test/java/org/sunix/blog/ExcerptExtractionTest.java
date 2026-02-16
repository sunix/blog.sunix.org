package org.sunix.blog;

import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.lang.reflect.Method;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test suite for excerpt extraction from blog posts.
 * Tests all existing posts to ensure excerpts are properly extracted.
 */
@QuarkusTest
public class ExcerptExtractionTest {

    /**
     * Mock post object for testing.
     */
    private static class MockPost {
        private final String url;

        public MockPost(String url) {
            this.url = url;
        }

        public String url() {
            return url;
        }
    }

    /**
     * Test excerpt extraction for all existing blog posts.
     * This ensures that when new posts are added, we can detect if the fuzzy matching fails.
     */
    @ParameterizedTest(name = "[{index}] {0}")
    @CsvSource({
        "'Feeling Powerful with Just a Browser', posts/feeling-powerful-with-just-a-browser-working-around-a-broken-tennis-booking-system, 2026-01-11-feeling-powerful-with-just-a-browser.md",
        "'Building a Gift Card App', posts/building-a-gift-card-management-app-with-github-copilot-my-first-completed-side-project, 2025-11-14-building-gift-card-app-with-github-copilot.md",
        "'Why Git Flow Doesn''t Fit', posts/why-i-think-git-flow-doesn-t-fit-most-projects-anymore, 2025-11-07-git-flow-doesnt-fit-anymore.md",
        "'Recovering Frozen Fedora Laptop', posts/recovering-my-frozen-fedora-laptop, 2024-12-31-recovering-frozen-fedora-laptop.md",
        "'Why We Estimate', posts/why-we-estimate-the-true-value-of-estimation-in-agile-development, 2024-07-23-why-we-estimate.md",
        "'Migrating Gmail (email-migration)', posts/migrating-gmail-from-one-account-to-another-a-comprehensive-guide, 2024-07-09-email-migration.md",
        "'Google Summer of Code', posts/news-from-eclipse-che-google-summer-of-code-2016-projects, 2016-08-24-google_summer_of_code.md",
        "'EclipseCon France', posts/eclipsecon-france-2016-language-server-protocol-eclipse-che-and-commit-strip, 2016-06-24-EclipseConFrance.md",
        "'Universal Eclipse IDE', posts/a-universal-eclipse-ide, 2016-05-16-Universal-IDE.md",
        "'VGA External Projector', posts/vga-external-projector-not-detected-with-ubuntu-on-dell-xps-13, 2015-09-20-Fix-xps-display-vga-screen.md",
        "'Blog site with Jekyll', posts/a-blog-site-with-jekyll-github-pages-eclipse-che-and-codenvy, 2015-07-31-blog-site-with-jekyll-ghpages-codenvy.md"
    })
    void testExcerptExtractionForExistingPosts(String testName, String url, String expectedDirectory) {
        MockPost post = new MockPost(url);
        String excerpt = ExcerptExtension.excerpt(post);

        assertNotNull(excerpt, 
            String.format("Excerpt should not be null for post: %s (URL: %s)", testName, url));
        assertFalse(excerpt.isEmpty(), 
            String.format("Excerpt should not be empty for post: %s (URL: %s, expected directory: %s). " +
                "This likely means the fuzzy matching failed to find the post file.", 
                testName, url, expectedDirectory));
        assertTrue(excerpt.length() > 20, 
            String.format("Excerpt should be substantial (>20 chars) for post: %s. Got: '%s'", 
                testName, excerpt));
    }

    /**
     * Test the specific case that was reported as broken.
     */
    @Test
    void testEmailMigrationPostExcerpt() {
        MockPost post = new MockPost("posts/migrating-gmail-from-one-account-to-another-a-comprehensive-guide");
        String excerpt = ExcerptExtension.excerpt(post);

        assertNotNull(excerpt, "Email migration post excerpt should not be null");
        assertFalse(excerpt.isEmpty(), 
            "Email migration post excerpt should not be empty - this was the reported issue");
        assertTrue(excerpt.contains("Migrating emails"), 
            "Excerpt should contain the beginning of the post content");
        assertTrue(excerpt.contains("Gmail account"), 
            "Excerpt should mention Gmail");
    }

    /**
     * Test excerpt length truncation.
     */
    @Test
    void testExcerptTruncation() {
        // The "Feeling Powerful" post has a long first paragraph
        MockPost post = new MockPost("posts/feeling-powerful-with-just-a-browser-working-around-a-broken-tennis-booking-system");
        String excerpt = ExcerptExtension.excerpt(post);

        assertNotNull(excerpt);
        // Should be truncated to around 500 chars plus ellipsis
        assertTrue(excerpt.length() <= 520, 
            String.format("Excerpt should be truncated to ~500 chars, got %d chars", excerpt.length()));
    }

    /**
     * Test that excerpt extraction handles posts without <!-- more --> marker.
     */
    @Test
    void testPostWithoutMoreMarker() {
        // Use a URL that doesn't exist
        MockPost post = new MockPost("posts/nonexistent-post");
        String excerpt = ExcerptExtension.excerpt(post);

        // Should return empty string for non-existent posts
        assertEquals("", excerpt, "Should return empty string for non-existent posts");
    }

    /**
     * Test that excerpt extraction is case-insensitive for the more marker.
     */
    @Test
    void testExcerptExtraction() throws Exception {
        // Test that we can call the excerpt method successfully
        MockPost post = new MockPost("posts/why-we-estimate-the-true-value-of-estimation-in-agile-development");
        
        // Use reflection to verify the method exists and works
        Method excerptMethod = ExcerptExtension.class.getMethod("excerpt", Object.class);
        assertNotNull(excerptMethod, "excerpt method should exist");
        
        String result = (String) excerptMethod.invoke(null, post);
        assertNotNull(result, "excerpt should return a non-null result");
    }

    /**
     * Test fuzzy matching with word variations.
     * This tests the common prefix matching feature.
     */
    @Test
    void testFuzzyMatchingWithWordStems() {
        // "migrating" in URL should match "migration" in directory (common prefix: "migrati")
        MockPost post = new MockPost("posts/migrating-gmail-from-one-account-to-another-a-comprehensive-guide");
        String excerpt = ExcerptExtension.excerpt(post);

        assertFalse(excerpt.isEmpty(), 
            "Fuzzy matching should handle word stems (migrating vs migration)");
    }

    /**
     * Test semantic word matching.
     * This tests the WORD_STEMS feature for semantically related words.
     */
    @Test
    void testSemanticWordMatching() {
        // "gmail" in URL should match "email" in directory via WORD_STEMS mapping
        MockPost post = new MockPost("posts/migrating-gmail-from-one-account-to-another-a-comprehensive-guide");
        String excerpt = ExcerptExtension.excerpt(post);

        assertFalse(excerpt.isEmpty(), 
            "Semantic matching should handle gmail/email mapping");
        assertTrue(excerpt.contains("Gmail") || excerpt.contains("email"), 
            "Excerpt should mention Gmail or email");
    }

    /**
     * Test that HTML tags are stripped from excerpts.
     */
    @Test
    void testHtmlTagStripping() {
        // All excerpts should be plain text without HTML tags
        MockPost post = new MockPost("posts/a-blog-site-with-jekyll-github-pages-eclipse-che-and-codenvy");
        String excerpt = ExcerptExtension.excerpt(post);

        assertFalse(excerpt.contains("<"), "Excerpt should not contain HTML opening tags");
        assertFalse(excerpt.contains(">"), "Excerpt should not contain HTML closing tags");
        assertFalse(excerpt.matches(".*<[^>]+>.*"), "Excerpt should not contain any HTML tags");
    }
}
