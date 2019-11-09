package nl.obren.sokrates.sourcecode.lang.ruby;

import nl.obren.sokrates.common.utils.ProgressFeedback;
import nl.obren.sokrates.sourcecode.SourceFile;
import nl.obren.sokrates.sourcecode.cleaners.CleanedContent;
import nl.obren.sokrates.sourcecode.cleaners.SourceCodeCleanerUtils;
import nl.obren.sokrates.sourcecode.dependencies.DependenciesAnalysis;
import nl.obren.sokrates.sourcecode.lang.LanguageAnalyzer;
import nl.obren.sokrates.sourcecode.units.UnitInfo;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;

public class RubyAnalyzer extends LanguageAnalyzer {
    public RubyAnalyzer() {
    }

    @Override
    public CleanedContent cleanForLinesOfCodeCalculations(SourceFile sourceFile) {
        return SourceCodeCleanerUtils.cleanCommentsAndEmptyLines(sourceFile.getContent(), "#", "=begin", "=end");
    }

    @Override
    public CleanedContent cleanForDuplicationCalculations(SourceFile sourceFile) {
        String content = SourceCodeCleanerUtils.emptyComments(sourceFile.getContent(), "#", "=begin", "=end").getCleanedContent();

        content = SourceCodeCleanerUtils.trimLines(content);
        content = SourceCodeCleanerUtils.emptyLinesMatchingPattern("import .*;", content);
        content = SourceCodeCleanerUtils.emptyLinesMatchingPattern("package.*", content);
        content = SourceCodeCleanerUtils.emptyLinesMatchingPattern("[{]", content);
        content = SourceCodeCleanerUtils.emptyLinesMatchingPattern("[}]", content);

        return SourceCodeCleanerUtils.cleanEmptyLinesWithLineIndexes(content);
    }

    @Override
    public List<UnitInfo> extractUnits(SourceFile sourceFile) {
        List<UnitInfo> units = new ArrayList<>();

        List<String> lines = sourceFile.getLines();
        for (int i = 0; i < lines.size(); i++) {
            String line = lines.get(i).replace("\t", "    ");
            String trimmedLine = line.trim();
            if (trimmedLine.startsWith("def ")) {
                UnitInfo unit = getUnitInfo(sourceFile, line, trimmedLine);
                unit.setStartLine(i);

                String unitEnd = line.substring(0, line.indexOf("def ")) + "end";
                int loc = 0;
                String bodyLine;
                StringBuilder body = new StringBuilder();
                StringBuilder cleanBody = new StringBuilder();
                do {
                    body.append(lines.get(i) + "\n");
                    bodyLine = lines.get(i).replace("\t", "   ");
                    if (!bodyLine.trim().isEmpty() && !bodyLine.trim().startsWith("#")) {
                        loc++;
                        cleanBody.append(lines.get(i) + "\n");
                    }
                    i++;
                } while (i < lines.size() && !bodyLine.equals(unitEnd));
                unit.setEndLine(i);
                unit.setLinesOfCode(loc);
                unit.setBody(body.toString());
                unit.setMcCabeIndex(getMcCabeIndex(body.toString()));
                units.add(unit);
            }
        }

        return units;
    }


    private int getMcCabeIndex(String body) {
        String bodyForSearch = " " + body.replace("\n", " ");
        bodyForSearch = bodyForSearch.replace("(", " ( ");
        bodyForSearch = bodyForSearch.replace(")", " ) ");

        int mcCabeIndex = 1;
        mcCabeIndex += StringUtils.countMatches(bodyForSearch, " if ");
        mcCabeIndex += StringUtils.countMatches(bodyForSearch, " elsif ");
        mcCabeIndex += StringUtils.countMatches(bodyForSearch, " while ");
        mcCabeIndex += StringUtils.countMatches(bodyForSearch, " unless ");
        mcCabeIndex += StringUtils.countMatches(bodyForSearch, " until ");
        mcCabeIndex += StringUtils.countMatches(bodyForSearch, " for ");
        mcCabeIndex += StringUtils.countMatches(bodyForSearch, " when ");
        mcCabeIndex += StringUtils.countMatches(bodyForSearch, " while ");
        mcCabeIndex += StringUtils.countMatches(bodyForSearch, " rescue ");
        mcCabeIndex += StringUtils.countMatches(bodyForSearch, "&&");
        mcCabeIndex += StringUtils.countMatches(bodyForSearch, "||");

        return mcCabeIndex;
    }


    private UnitInfo getUnitInfo(SourceFile sourceFile, String line, String trimmedLine) {
        UnitInfo unit = new UnitInfo();
        unit.setSourceFile(sourceFile);
        if (trimmedLine.contains("(")) {
            String name = trimmedLine.substring(3, trimmedLine.indexOf("(")).trim();
            unit.setShortName(name);
            unit.setLongName(name);
            if (line.contains(")")) {
                unit.setNumberOfParameters(line.substring(line.indexOf("("), line.indexOf(")")).split(",").length);
            }
        } else {
            String name = trimmedLine.substring(3).trim();
            unit.setShortName(name);
            unit.setLongName(name);
        }
        return unit;
    }


    @Override
    public DependenciesAnalysis extractDependencies(List<SourceFile> sourceFiles, ProgressFeedback progressFeedback) {
        return new RubyHeuristicDependenciesExtractor().extractDependencies(sourceFiles, progressFeedback);
    }
}