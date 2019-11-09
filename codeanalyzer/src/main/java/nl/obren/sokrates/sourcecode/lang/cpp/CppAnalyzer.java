package nl.obren.sokrates.sourcecode.lang.cpp;

import nl.obren.sokrates.common.utils.ProgressFeedback;
import nl.obren.sokrates.sourcecode.SourceFile;
import nl.obren.sokrates.sourcecode.cleaners.CleanedContent;
import nl.obren.sokrates.sourcecode.cleaners.SourceCodeCleanerUtils;
import nl.obren.sokrates.sourcecode.dependencies.DependenciesAnalysis;
import nl.obren.sokrates.sourcecode.lang.LanguageAnalyzer;
import nl.obren.sokrates.sourcecode.units.CStyleHeuristicUnitParser;
import nl.obren.sokrates.sourcecode.units.UnitInfo;

import java.util.List;

public class CppAnalyzer extends LanguageAnalyzer {
    public CppAnalyzer() {
    }

    @Override
    public CleanedContent cleanForLinesOfCodeCalculations(SourceFile sourceFile) {
        return SourceCodeCleanerUtils.cleanCommentsAndEmptyLines(sourceFile.getContent(), "//", "/*", "*/");
    }

    @Override
    public CleanedContent cleanForDuplicationCalculations(SourceFile sourceFile) {
        String content = SourceCodeCleanerUtils.emptyComments(sourceFile.getContent(), "//", "/*", "*/").getCleanedContent();

        content = SourceCodeCleanerUtils.trimLines(content);
        content = SourceCodeCleanerUtils.emptyLinesMatchingPattern("namespace .*;", content);
        content = SourceCodeCleanerUtils.emptyLinesMatchingPattern("using .*;", content);
        content = SourceCodeCleanerUtils.emptyLinesMatchingPattern("[#].*", content);
        content = SourceCodeCleanerUtils.emptyLinesMatchingPattern("[{]", content);
        content = SourceCodeCleanerUtils.emptyLinesMatchingPattern("[}]", content);

        return SourceCodeCleanerUtils.cleanEmptyLinesWithLineIndexes(content);
    }

    @Override
    public List<UnitInfo> extractUnits(SourceFile sourceFile) {
        return new CStyleHeuristicUnitParser().extractUnits(sourceFile);
    }

    @Override
    public DependenciesAnalysis extractDependencies(List<SourceFile> sourceFiles, ProgressFeedback progressFeedback) {
        CIncludesDependenciesExtractor dependenciesExtractor = new CIncludesDependenciesExtractor();
        return dependenciesExtractor.extractDependencies(sourceFiles, progressFeedback);
    }

}
