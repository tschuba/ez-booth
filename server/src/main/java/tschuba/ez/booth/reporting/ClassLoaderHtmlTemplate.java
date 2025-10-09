/**
 * Copyright (c) 2025 Thomas Schulte-Bahrenberg
 * All rights reserved.
 */
package tschuba.ez.booth.reporting;

import java.io.IOException;
import java.io.InputStream;
import java.io.Writer;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.stream.Stream;
import lombok.NonNull;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Entities;
import org.jsoup.parser.Parser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;
import org.thymeleaf.extras.java8time.dialect.Java8TimeDialect;
import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.templateresolver.ClassLoaderTemplateResolver;
import tschuba.ez.booth.services.ReportingException;

/**
 * HTML report template implementation that loads templates from the classpath.
 * Uses Thymeleaf as the template engine and Jsoup for HTML manipulation.
 *
 * @param <T> The type of data model used for rendering the template.
 */
public class ClassLoaderHtmlTemplate<T> implements ReportTemplate<T> {

    private static final Logger LOGGER = LoggerFactory.getLogger(ClassLoaderHtmlTemplate.class);

    private static final String RESOLVER_PREFIX = "reports/templates/";
    private static final String RESOLVER_SUFFIX = ".template.html";
    private static final String REPORT_DATA_CONTEXT_VAR = "reportData";

    private static final String STYLE_TAG = "style";

    private static final URI BASE_URI;

    static {
        URL url = ClassLoaderHtmlTemplate.class.getClassLoader().getResource(RESOLVER_PREFIX);
        if (url == null) {
            String message = "Template base URL '%s' not found!".formatted(RESOLVER_PREFIX);
            LOGGER.error(message);
            throw new ReportingException(message);
        }
        try {
            BASE_URI = url.toURI();
        } catch (URISyntaxException ex) {
            String message = "Failed to determine template base URI!";
            LOGGER.error(message, ex);
            throw new ReportingException(message, ex);
        }
    }

    private final String template;
    private final TemplateEngine engine = new TemplateEngine();

    /**
     * Creates a new HTML report template instance.
     *
     * @param template The name of the template file (without prefix/suffix).
     */
    public ClassLoaderHtmlTemplate(@NonNull String template) {
        this.template = template;

        ClassLoaderTemplateResolver templateResolver = new ClassLoaderTemplateResolver();
        templateResolver.setPrefix(RESOLVER_PREFIX);
        templateResolver.setSuffix(RESOLVER_SUFFIX);
        templateResolver.setTemplateMode(TemplateMode.HTML);

        engine.setTemplateResolver(templateResolver);
        engine.addDialect(new Java8TimeDialect());
    }

    @Override
    public void render(@NonNull Writer writer, @NonNull T data) throws ReportingException {
        Context context = new Context();
        context.setVariable(REPORT_DATA_CONTEXT_VAR, data);

        String html = engine.process(this.template, context);

        Document document = Jsoup.parse(html, BASE_URI.toString(), Parser.xmlParser());
        embedStyleSheets(document);

        try {
            document.outputSettings().prettyPrint(false).escapeMode(Entities.EscapeMode.extended);
            writer.write(document.outerHtml());
        } catch (IOException ex) {
            throw new ReportingException("Failed to write report output!", ex);
        }

        LOGGER.debug("Successfully finished rendering template {}", this.template);
    }

    /**
     * Embeds external stylesheets into the HTML document by converting
     * <link> elements to <style> elements with the actual CSS content.
     *
     * @param htmlDoc The HTML document to process.
     */
    static void embedStyleSheets(Document htmlDoc) {
        Element head = htmlDoc.head();

        // find all external stylesheet links
        Stream<Element> stylesheetLinks =
                head.children().stream()
                        .filter(
                                element -> {
                                    boolean isLink = element.nameIs("link");
                                    String typeAttr = element.attr("type");
                                    String relAttr = element.attr("rel");
                                    String hrefAttr = element.attr("abs:href");
                                    return isLink
                                            && !hrefAttr.isEmpty()
                                            && ("text/css".equals(typeAttr)
                                                    || "stylesheet".equals(relAttr));
                                });

        // embed CSS of external stylesheets
        stylesheetLinks.forEach(
                link -> {
                    String href = link.attr("abs:href");
                    String stylesheetContent = null;
                    try (InputStream stylesheetInputStream =
                            URI.create(href).toURL().openStream()) {
                        stylesheetContent = new String(stylesheetInputStream.readAllBytes());
                    } catch (IOException ex) {
                        throw new RuntimeException(
                                "Failed to read stylesheet file at %s!".formatted(href), ex);
                    }
                    head.appendElement(STYLE_TAG).html(stylesheetContent);
                    link.remove();
                });
    }
}
