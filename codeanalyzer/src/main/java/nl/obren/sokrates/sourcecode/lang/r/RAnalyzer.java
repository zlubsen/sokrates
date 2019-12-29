/*
 * Copyright (c) 2019 Željko Obrenović. All rights reserved.
 */

package nl.obren.sokrates.sourcecode.lang.r;

import nl.obren.sokrates.common.utils.ProgressFeedback;
import nl.obren.sokrates.common.utils.RegexUtils;
import nl.obren.sokrates.sourcecode.SourceFile;
import nl.obren.sokrates.sourcecode.cleaners.CleanedContent;
import nl.obren.sokrates.sourcecode.cleaners.CommentsAndEmptyLinesCleaner;
import nl.obren.sokrates.sourcecode.cleaners.SourceCodeCleanerUtils;
import nl.obren.sokrates.sourcecode.dependencies.DependenciesAnalysis;
import nl.obren.sokrates.sourcecode.lang.LanguageAnalyzer;
import nl.obren.sokrates.sourcecode.units.CStyleHeuristicUnitParser;
import nl.obren.sokrates.sourcecode.units.UnitInfo;

import java.util.ArrayList;
import java.util.List;

public class RAnalyzer extends LanguageAnalyzer {
    public RAnalyzer() {
    }

    @Override
    public CleanedContent cleanForLinesOfCodeCalculations(SourceFile sourceFile) {
        return getCleaner().clean(sourceFile.getContent());
    }

    private CommentsAndEmptyLinesCleaner getCleaner() {
        CommentsAndEmptyLinesCleaner cleaner = new CommentsAndEmptyLinesCleaner();

        cleaner.addCommentBlockHelper("#", "\n");
        cleaner.addStringBlockHelper("\"", "\\");
        cleaner.addStringBlockHelper("'", "\\");

        return cleaner;
    }

    @Override
    public CleanedContent cleanForDuplicationCalculations(SourceFile sourceFile) {
        String content = getCleaner().cleanKeepEmptyLines(sourceFile.getContent());

        content = SourceCodeCleanerUtils.trimLines(content);

        return SourceCodeCleanerUtils.cleanEmptyLinesWithLineIndexes(content);
    }

    private String emptyComments(SourceFile sourceFile) {
        String content = sourceFile.getContent();

        content = SourceCodeCleanerUtils.emptyComments(content, null, "/*", "*/").getCleanedContent();

        return content;
    }

    @Override
    public List<UnitInfo> extractUnits(SourceFile sourceFile) {
        CStyleHeuristicUnitParser heuristicUnitParser = new CStyleHeuristicUnitParser() {
            @Override
            public boolean isUnitSignature(String line) {
                return RegexUtils.matchesEntirely("[ ]*[a-zA-Z_0-9]+[ ]* [<][-][ ]*function[ ]*[(].*", line);
            }

        };
        return heuristicUnitParser.extractUnits(sourceFile);
    }

    @Override
    public DependenciesAnalysis extractDependencies(List<SourceFile> sourceFiles, ProgressFeedback progressFeedback) {
        return new DependenciesAnalysis();
    }

    @Override
    public List<String> getFeaturesDescription() {
        List<String> features = new ArrayList<>();

        features.add(FEATURE_ALL_STANDARD_ANALYSES);
        features.add(FEATURE_ADVANCED_CODE_CLEANING);
        features.add(FEATURE_UNIT_SIZE_ANALYSIS);
        features.add(FEATURE_CONDITIONAL_COMPLEXITY_ANALYSIS);
        features.add(FEATURE_NO_DEPENDENCIES_ANALYSIS);

        return features;
    }
}
