package nl.obren.sokrates.reports.generators.statichtml;

import nl.obren.sokrates.common.renderingutils.RichTextRenderingUtils;
import nl.obren.sokrates.reports.core.RichTextReport;
import nl.obren.sokrates.reports.utils.ScopesRenderer;
import nl.obren.sokrates.sourcecode.SourceFileFilter;
import nl.obren.sokrates.sourcecode.analysis.results.AspectAnalysisResults;
import nl.obren.sokrates.sourcecode.analysis.results.CodeAnalysisResults;
import nl.obren.sokrates.sourcecode.metrics.NumericMetric;
import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.*;

public class OverviewReportGenerator {
    private CodeAnalysisResults codeAnalysisResults;
    private File codeConfigurationFile;

    public OverviewReportGenerator(CodeAnalysisResults codeAnalysisResults, File codeConfigurationFile) {
        this.codeAnalysisResults = codeAnalysisResults;
        this.codeConfigurationFile = codeConfigurationFile;
    }

    public void addScopeAnalysisToReport(RichTextReport report) {
        appendHeader(report);

        List<NumericMetric> code = Arrays.asList(new NumericMetric("main", codeAnalysisResults.getMainAspectAnalysisResults().getLinesOfCode()),
                new NumericMetric("test", codeAnalysisResults.getTestAspectAnalysisResults().getLinesOfCode()),
                new NumericMetric("generated", codeAnalysisResults.getGeneratedAspectAnalysisResults().getLinesOfCode()),
                new NumericMetric("build", codeAnalysisResults.getBuildAndDeployAspectAnalysisResults().getLinesOfCode()),
                new NumericMetric("other", codeAnalysisResults.getOtherAspectAnalysisResults().getLinesOfCode()));
        List<NumericMetric> counts = Arrays.asList(new NumericMetric("main", codeAnalysisResults.getMainAspectAnalysisResults().getLinesOfCode()),
                new NumericMetric("test", codeAnalysisResults.getTestAspectAnalysisResults().getFilesCount()),
                new NumericMetric("generated", codeAnalysisResults.getGeneratedAspectAnalysisResults().getFilesCount()),
                new NumericMetric("build", codeAnalysisResults.getBuildAndDeployAspectAnalysisResults().getFilesCount()),
                new NumericMetric("other", codeAnalysisResults.getOtherAspectAnalysisResults().getFilesCount()));

        report.startSection("Overview of Analyzed Files", "Basic stats on analyzed files");
        report.startSubSection("Intro", "For analysis purposes we separate files in scope into several categories: <b>main</b>, <b>test</b>, <b>generated</b>, <b>deployment and build</b>, and <b>other</b>.");
        appendIntroduction(report);
        ScopesRenderer renderer = getScopesRenderer("", "", counts, code);
        renderer.setInSection(false);
        renderer.renderReport(report, "");
        report.endSection();

        renderScopes(report, codeAnalysisResults.getMainAspectAnalysisResults(), "Main Code", "All <b>manually</b> created or maintained source code that defines logic of the product " +
                "that is  run in a <b>production</b> environment.");
        renderScopes(report, codeAnalysisResults.getTestAspectAnalysisResults(), "Test Code", "Used only for testing of the product. Normally not deployed in a production environment.");
        renderScopes(report, codeAnalysisResults.getGeneratedAspectAnalysisResults(), "Generated Code", "Automatically generated files, not manually changed after generation.");
        renderScopes(report, codeAnalysisResults.getBuildAndDeployAspectAnalysisResults(), "Build and Deployment Code", "Source code used to configure or support build and deployment process.");
        renderScopes(report, codeAnalysisResults.getOtherAspectAnalysisResults(), "Other Code", "");

        /*report.startSection();
        report.addHtmlContent(ChartUtils.getAspectsVolumePieChart("Lines of code per type of code", code).toString());
        report.endDiv();*/

        report.endSection();

        /*report.startSection();
        report.addLevel2Header("Details", "background-color: yellow");

        renderScopesDetails(report, codeAnalysisResults.getMainAspectAnalysisResults(), "Main Code", "");
        renderScopesDetails(report, codeAnalysisResults.getTestAspectAnalysisResults(), "Test Code", "");
        renderScopesDetails(report, codeAnalysisResults.getGeneratedAspectAnalysisResults(), "Generated Code", "");
        renderScopesDetails(report, codeAnalysisResults.getBuildAndDeployAspectAnalysisResults(), "Build and Deployment Code", "");
        renderScopesDetails(report, codeAnalysisResults.getOtherAspectAnalysisResults(), "Other Code", "");

        report.endDiv();*/

        addFooter(report);
    }

    private void appendIntroduction(RichTextReport report) {
        String extra = "";
        extra += "<ul>";
        extra += "<li>The <b>main</b> category contains all <b>manually</b> created source code files that are being used in the <b>production</b>.</li>";
        extra += "<li>Files in the <b>main</b> category are used as input for other analyses: logical decomposition, cross-cutting concerns, duplication, file size, unit size, and " +
                "cyclomatic complexity" +
                ".</li>";
        extra += "<li><b>Test</b> source code files are used only for testing of the product. These files are normally not deployed to production.</li>";
        extra += "<li><b>Build and deployment</b> source code files are used to configure or support build and deployment process.</li>";
        extra += "<li><b>Generated</b> source code files are automatically generated files that have not been manually changed after generation.</li>";
        extra += "<li>While a source code folder may contain a number of files, we are primarily interested in the source code files that are being written and maintained by developers.</li>";
        extra += "<li>Files containing binaries, documentation, or third-party libraries, for instance, are excluded from analysis. The exception are third-party libraries " +
                "that have been changed by developers.</li>";
        extra += "</ul>";
        report.addParagraph(extra);
    }

    private void addFooter(RichTextReport report) {
        report.addLineBreak();
        report.addHorizontalLine();
        report.addParagraph(RichTextRenderingUtils.italic(new SimpleDateFormat("yyyy-MM-dd HH:mm").format(new Date())));
    }


    public void renderScopes(RichTextReport report, AspectAnalysisResults aspectAnalysisResults, String title, String description) {
        List<NumericMetric> fileCountPerExtension = aspectAnalysisResults.getFileCountPerExtension();
        List<NumericMetric> linesOfCodePerExtension = aspectAnalysisResults.getLinesOfCodePerExtension();

        ScopesRenderer renderer = getScopesRenderer(title, "", fileCountPerExtension, linesOfCodePerExtension);

        if (fileCountPerExtension.size() > 0) {
            renderer.setAspect(aspectAnalysisResults.getAspect());
            renderer.renderReport(report, description);
        }
    }

    private ScopesRenderer getScopesRenderer(String title, String description, List<NumericMetric> fileCounts, List<NumericMetric> linesOfCode) {
        ScopesRenderer renderer = new ScopesRenderer();
        renderer.setLinesOfCodeInMain(codeAnalysisResults.getMainAspectAnalysisResults().getLinesOfCode());
        renderer.setTitle(title.replace("-", "").trim());
        renderer.setDescription(description);
        renderer.setFileCountPerComponent(fileCounts);
        renderer.setLinesOfCode(linesOfCode);

        renderer.setMaxFileCount(fileCounts.stream().map(a -> a.getValue().intValue()).mapToInt(Integer::intValue).sum());
        renderer.setMaxLinesOfCode(linesOfCode.stream().map(a -> a.getValue().intValue()).mapToInt(Integer::intValue).sum());

        return renderer;
    }


    private void appendHeader(RichTextReport report) {
        report.startSection("Source Code Analysis Scope", "Files includes and excluded from analyses");
        report.startUnorderedList();
        int totalNumberOfFilesInScope = codeAnalysisResults.getTotalNumberOfFilesInScope();
        int numberOfExcludedFiles = codeAnalysisResults.getNumberOfExcludedFiles();
        List<String> extensions = codeAnalysisResults.getCodeConfiguration().getExtensions();
        int extensionsCount = extensions.size();
        report.addListItem("<b>" + extensionsCount + "</b> extension" + (extensionsCount > 1 ? "s are" : " is") + " included in analyses: " + getExtensionsString(extensions));
        ArrayList<SourceFileFilter> exclusions = codeAnalysisResults.getCodeConfiguration().getIgnore();
        if (exclusions != null && exclusions.size() > 0) {
            report.addListItem(RichTextRenderingUtils.renderNumberStrong(exclusions.size())
                    + (exclusions.size() == 1 ? " criterion is " : " criteria are ") +
                    " used to exclude files from analysis:");
            report.startUnorderedList();
            exclusions.forEach(exclusion -> {
                report.addListItem(describeExclusion(exclusion));
            });
            report.endUnorderedList();
        }
        report.addListItem("The source code folder contains " + RichTextRenderingUtils.renderNumberStrong(totalNumberOfFilesInScope) + " files.");
        report.startUnorderedList();
        report.addListItem(RichTextRenderingUtils.renderNumberStrong(totalNumberOfFilesInScope - numberOfExcludedFiles) + " files are included in analyses.");
        report.addHtmlContent("<li>");
        report.addShowMoreBlock(RichTextRenderingUtils.renderNumberStrong(numberOfExcludedFiles) + " files are excluded from analyses.", getExcludedExtensionsOverview
                (codeAnalysisResults
                        .getExcludedExtensions()), "(...)");
        report.addHtmlContent("</li>");
        report.endUnorderedList();
        report.endUnorderedList();
        report.endSection();
    }

    private String describeExclusion(SourceFileFilter exclusion) {
        String description = !exclusion.getInclude() ? "do not exclude" : "exclude";
        description += " files with ";
        boolean add = false;
        if (StringUtils.isNotBlank(exclusion.getPathPattern())) {
            description += "path like \"<b>" + exclusion.getPathPattern() + "</b>\"";
            add = true;
        }
        if (StringUtils.isNotBlank(exclusion.getContentPattern())) {
            if (add) {
                description += " AND ";
            }
            description += "content like \"<b>" + exclusion.getContentPattern() + "</b>\"";
        }
        if (StringUtils.isNotBlank(exclusion.getNote())) {
            description += " (" + exclusion.getNote() + ")";
        }
        return description + ".";
    }

    private String getExtensionsString(List<String> extensions) {
        StringBuilder stringBuilder = new StringBuilder();

        extensions.forEach(extension -> {
            if (stringBuilder.length() > 0) {
                stringBuilder.append(", ");
            }
            stringBuilder.append(extension);
        });

        return stringBuilder.toString();
    }

    private String getExcludedExtensionsOverview(Map<String, Integer> excludedExtensions) {
        List<String> extensions = new ArrayList<>(excludedExtensions.keySet());
        Collections.sort(extensions, (o1, o2) -> -Integer.compare(excludedExtensions.get(o1), excludedExtensions.get(o2)));

        StringBuilder string = new StringBuilder();

        extensions.forEach(extension -> {
            if (string.length() > 0) {
                string.append(", ");
            }
            string.append(excludedExtensions.get(extension) + " <b>" + extension + "</b>");
        });

        return "Excluded files per extension: " + string.toString();
    }

}