package nl.obren.sokrates.sourcecode.aspects;

import nl.obren.sokrates.sourcecode.SourceFile;
import nl.obren.sokrates.sourcecode.SourceFileFilter;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class SourceCodeAspectUtils {
    private static final Log LOG = LogFactory.getLog(SourceCodeAspectUtils.class);

    public static int getMaxLinesOfCode(List<? extends SourceCodeAspect> aspects) {
        int maxFileLinesOfCode = 0;

        for (SourceCodeAspect aspect : aspects) {
            maxFileLinesOfCode = Math.max(maxFileLinesOfCode, aspect.getLinesOfCode());
        }

        return maxFileLinesOfCode;
    }

    public static int getMaxFileCount(List<? extends SourceCodeAspect> aspects) {
        int maxFileCount = 0;

        for (SourceCodeAspect aspect : aspects) {
            maxFileCount = Math.max(maxFileCount, aspect.getSourceFiles().size());
        }

        return maxFileCount;
    }

    public static List<SourceCodeAspect> getSourceCodeAspectBasedOnFolderDepth(String srcRoot, List<SourceFile>
            sourceFiles, int depth) {
        List<String> paths = getUniquePaths(sourceFiles, depth);

        String greatestCommonPrefix = greatestCommonPrefix(paths);

        List<SourceCodeAspect> aspects = new ArrayList<>();

        paths.forEach(path -> {
            String aspectName = path;
            if (!aspectName.equals(greatestCommonPrefix)) {
                aspectName = path.substring(greatestCommonPrefix.length());
            }
            aspectName = StringUtils.defaultIfBlank(aspectName, "ROOT");
            SourceCodeAspect aspect = new SourceCodeAspect(aspectName);
            String pathPattern = srcRoot + File.separator + path.toString() + File.separator + ".*";
            pathPattern = pathPattern.replace(File.separator + File.separator, File.separator);
            aspect.getSourceFileFilters().add(new SourceFileFilter(pathPattern, ""));

            paths.forEach(otherPath -> {
                if (!path.equals(otherPath)) {
                    addExclusiveFilterIfNeeded(path, otherPath, srcRoot, aspect);
                }
            });

            aspects.add(aspect);
        });

        return aspects;
    }

    private static void addExclusiveFilterIfNeeded(String path, String otherPath, String srcRoot, SourceCodeAspect
            aspect) {
        if (otherPath.startsWith(path)) {
            String otherPathPattern = srcRoot + File.separator + otherPath.toString() + File.separator + ".*";
            otherPathPattern = otherPathPattern.replace(File.separator + File.separator, File.separator);
            SourceFileFilter otherSourceFileFilter = new SourceFileFilter(otherPathPattern, "");
            otherSourceFileFilter.setInclude(false);
            aspect.getSourceFileFilters().add(otherSourceFileFilter);
        }
    }

    public static List<String> getUniquePaths(List<SourceFile> sourceFiles, int depth) {
        List<String> paths = new ArrayList<>();
        sourceFiles.forEach(sourceFile -> {
            String componentName = getFolderBasedComponentName(sourceFile, depth);

            if (!paths.contains(componentName)) {
                paths.add(componentName);
            }
        });
        return paths;
    }

    public static String getFolderBasedComponentName(SourceFile sourceFile, int depth) {
        String relativePath = sourceFile.getRelativePath().replace("\\", "/");
        String[] subFolders = relativePath.split("/");
        StringBuilder aspectPath = new StringBuilder();
        for (int i = 0; i < Math.min(depth, subFolders.length - 1); i++) {
            aspectPath.append(subFolders[i] + File.separator);
        }
        String componentName = aspectPath.toString().trim();
        if (componentName.length() > 1) {
            componentName = componentName.substring(0, componentName.lastIndexOf(File.separator));
        }
        return componentName;
    }

    public static String greatestCommonPrefix(List<String> strings) {
        if (strings.size() > 0) {
            int minLength[] = {Integer.MAX_VALUE};
            strings.forEach(string -> minLength[0] = Math.min(minLength[0], string.length()));

            for (int i = 0; i < minLength[0]; i++) {
                char c = strings.get(0).charAt(i);
                for (String string : strings) {
                    if (string.charAt(i) != c) {
                        return getWholeFolderNamesString(strings.get(0).substring(0, i));
                    }
                }
            }
            String commonPrefix = getWholeFolderNamesString(strings.get(0).substring(0, minLength[0]));
            return commonPrefix;
        }
        return "";
    }

    private static String getWholeFolderNamesString(String commonPrefix) {
        if (!StringUtils.containsAny(commonPrefix, "/", "\\")) {
            return "";
        }

        if (!StringUtils.endsWithAny(commonPrefix, "/", "\\") && StringUtils.containsAny(commonPrefix, "/", "\\")) {
            int lastIndexOfSeparator1 = commonPrefix.lastIndexOf("/");
            int lastIndexOfSeparator2 = commonPrefix.lastIndexOf("\\");
            int lastIndexOfSeparator = Math.max(lastIndexOfSeparator1, lastIndexOfSeparator2);
            commonPrefix = commonPrefix.substring(0, lastIndexOfSeparator + 1);
        }
        return commonPrefix;
    }
}