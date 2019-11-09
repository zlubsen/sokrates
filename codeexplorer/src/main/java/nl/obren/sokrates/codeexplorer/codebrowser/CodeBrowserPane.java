package nl.obren.sokrates.codeexplorer.codebrowser;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Orientation;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import nl.obren.sokrates.codeexplorer.configuration.CodeConfigurationView;
import nl.obren.sokrates.codeexplorer.console.WebViewConsole;
import nl.obren.sokrates.common.io.UserProperties;
import nl.obren.sokrates.common.utils.ProgressFeedback;
import nl.obren.sokrates.sourcecode.SourceCodeFiles;
import nl.obren.sokrates.sourcecode.aspects.CrossCuttingConcern;
import nl.obren.sokrates.sourcecode.aspects.CrossCuttingConcernsGroup;
import nl.obren.sokrates.sourcecode.aspects.LogicalDecomposition;
import nl.obren.sokrates.sourcecode.aspects.SourceCodeAspect;
import nl.obren.sokrates.sourcecode.core.CodeConfiguration;
import nl.obren.sokrates.sourcecode.core.CodeConfigurationUtils;
import nl.obren.sokrates.sourcecode.findings.Findings;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.Executors;

import static nl.obren.sokrates.codeexplorer.configuration.CodeConfigurationView.RECENT_CONFIGURATION_FILES_PROPERTY;

public class CodeBrowserPane extends SplitPane {
    public static final String DEFAULT_FONT_STYLE_FRAGMENT = "-fx-font-family: 'Menlo', 'Ubuntu Mono', 'Consolas', 'source-code-pro', monospace; -fx-font-size: 9pt";

    private final AspectFilesBrowserPane aspectFilesBrowserPane;
    private final CodeConfigurationView codeConfigurationView;

    private BorderPane codeViewerPane = new BorderPane();

    private SourceCodeFiles sourceCodeFiles = new SourceCodeFiles();
    private AspectsTablePane scopeAspectsTablePane;
    private AspectsTablePane logicalComponentsTablePane;
    private AspectsTablePane crossCuttingAspectsTablePane;
    private CodeConfiguration codeConfiguration = CodeConfiguration.getDefaultConfiguration();
    private SplitPane splitPane = new SplitPane();

    private BorderPane mainPane = new BorderPane();

    private WebViewConsole console = new WebViewConsole();

    private Findings findings;
    private File findingsFile;

    public CodeBrowserPane(Stage primaryStage) {
        setStyle(DEFAULT_FONT_STYLE_FRAGMENT);
        this.findings = new Findings(() -> {
            try {
                String content = findingsFile.exists() ? FileUtils.readFileToString(findingsFile, StandardCharsets.UTF_8) : "";
                FileUtils.write(findingsFile,
                        content
                                + new Date().toString() + "\n"
                                + "Summary: " + findings.getSummary() + "\n\n"
                                + findings.getContent() + "\n"
                                + "------------------------------------------------\n\n"
                        , StandardCharsets.UTF_8);
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        this.aspectFilesBrowserPane = new AspectFilesBrowserPane(this, findings);

        codeViewerPane.setCenter(aspectFilesBrowserPane);

        codeConfigurationView = new CodeConfigurationView(primaryStage, this);
        BorderPane editorPane = new BorderPane();
        editorPane.setCenter(codeConfigurationView);
        editorPane.setTop(new CodeExplorerToolbar(this));
        mainPane.setCenter(splitPane);
        splitPane.getItems().add(editorPane);
        splitPane.getItems().add(codeViewerPane);

        setMenuBar();

        getItems().addAll(mainPane, console);
        setOrientation(Orientation.VERTICAL);
        setDividerPosition(0, 0.9);
    }

    public WebViewConsole getConsole() {
        return console;
    }

    private void setMenuBar() {
        final Menu fileMenu = new Menu("File");
        fileMenu.getItems().add(getMenuItem("New", e -> codeConfigurationView.newConfiguration()));
        fileMenu.getItems().add(new SeparatorMenuItem());
        fileMenu.getItems().add(getMenuItem("Open", e -> codeConfigurationView.openConfiguration()));
        fileMenu.getItems().add(getOpenRecentMenu());
        fileMenu.getItems().add(new SeparatorMenuItem());
        fileMenu.getItems().add(getMenuItem("Save", e -> codeConfigurationView.completeAndSave()));
        fileMenu.getItems().add(getMenuItem("Save As...", e -> codeConfigurationView.saveAs()));

        final Menu editMenu = new Menu("Edit");
        editMenu.getItems().add(getMenuItem("Find...", e -> codeConfigurationView.openEditorFindDialog()));
        editMenu.getItems().add(getMenuItem("Replace..", e -> codeConfigurationView.openEditorReplaceDialog()));

        final Menu findingsMenu = new Menu("Findings");
        findingsMenu.getItems().add(getMenuItem("Open Findings...", e -> codeConfigurationView.openFindings()));

        final Menu reportMenu = new Menu("Report");
        reportMenu.getItems().add(getMenuItem("Open Reports Folder...", e -> codeConfigurationView.openReportsFolder()));
        reportMenu.getItems().add(new SeparatorMenuItem());
        reportMenu.getItems().add(getMenuItem("Generate All Reports...", e -> codeConfigurationView.generateFullReport()));
        reportMenu.getItems().add(new SeparatorMenuItem());
        reportMenu.getItems().add(getMenuItem("Update Overview Report...", e -> codeConfigurationView.generateFilesInScopeReport()));
        reportMenu.getItems().add(getMenuItem("Update Duplication Report...", e -> codeConfigurationView.generateDuplicationReport()));
        reportMenu.getItems().add(getMenuItem("Update Logical Decomposition Report...", e -> codeConfigurationView.generateLogicalDecompositionReport()));
        reportMenu.getItems().add(getMenuItem("Update Cross Cutting Concerns Report...", e -> codeConfigurationView.generateCrossCuttingConcernsReport()));
        reportMenu.getItems().add(getMenuItem("Update File Size Report...", e -> codeConfigurationView.generateFileSizeReport()));
        reportMenu.getItems().add(getMenuItem("Update Unit Size Report...", e -> codeConfigurationView.generateUnitSizeReport()));
        reportMenu.getItems().add(getMenuItem("Update Cyclomatic Complexity Report...", e -> codeConfigurationView.generateCyclomaticComplexity()));
        reportMenu.getItems().add(getMenuItem("Update Metrics Overview Report...", e -> codeConfigurationView.generateMetricsOverviewReport()));
        reportMenu.getItems().add(getMenuItem("Update Controls Report...", e -> codeConfigurationView.generateControlsReport()));

        MenuBar menuBar = new MenuBar();
        menuBar.getMenus().addAll(fileMenu, editMenu, findingsMenu, reportMenu);

        final String os = System.getProperty("os.name");
        if (os != null && os.startsWith("Mac")) {
            menuBar.setUseSystemMenuBar(true);
        }
        mainPane.setTop(new BorderPane(mainPane.getTop(), menuBar, null, null, null));
    }

    private MenuItem getMenuItem(String text, EventHandler<ActionEvent> actionEventEventHandler) {
        MenuItem menuItem = new MenuItem(text);
        menuItem.setOnAction(actionEventEventHandler);
        return menuItem;
    }

    private Menu getOpenRecentMenu() {
        Menu menu = new Menu("Open Recent");

        List<File> recentFiles = UserProperties.getInstance("sokrates").getFileListProperty(RECENT_CONFIGURATION_FILES_PROPERTY);
        int count[] = {0};
        recentFiles.forEach(recentFile -> {
            if (recentFile.exists() && count[0]++ < 20) {
                MenuItem menuItem = new MenuItem(recentFile.getPath());
                menuItem.setOnAction(e -> {
                    codeConfigurationView.setFile(recentFile);
                    codeConfigurationView.openFile();
                });
                menu.getItems().add(menuItem);
            }
        });

        return menu;
    }

    public void load() {
        codeConfiguration = codeConfigurationView.getConfigurationFromEditor();
        if (codeConfiguration != null) {
            clearViews();
            Executors.newCachedThreadPool().execute(() -> {
                load(codeConfiguration);
            });
        }
    }

    private void clearViews() {
        scopeAspectsTablePane.refresh(new ArrayList<>(), null);
        logicalComponentsTablePane.setAspectSelections(new ArrayList<>());
        logicalComponentsTablePane.refresh(new ArrayList<>(), null);
        crossCuttingAspectsTablePane.setConcernsSelections(new ArrayList<>());
        crossCuttingAspectsTablePane.refresh(new ArrayList<>(), null);
        aspectFilesBrowserPane.clear();
    }

    public void load(CodeConfiguration codeConfiguration) {
        this.codeConfiguration = codeConfiguration;
        File codeConfigurationFile = codeConfigurationView.getFile();
        this.findingsFile = CodeConfigurationUtils.getDefaultSokratesFindingsFile(codeConfigurationFile.getParentFile());

        ProgressFeedback progressFeedback = console.getProgressFeedback();
        progressFeedback.clear();
        progressFeedback.setDetailedText("");

        sourceCodeFiles = new SourceCodeFiles();
        sourceCodeFiles.load(new File(CodeConfiguration.getAbsoluteSrcRoot(codeConfiguration.getSrcRoot(), codeConfigurationFile)), progressFeedback);
        codeConfiguration.load(sourceCodeFiles, codeConfigurationFile);

        List<SourceCodeAspect> scopesWithExtensions = codeConfiguration.getScopesWithExtensions();

        List<LogicalDecomposition> logicalDecompositions = codeConfiguration.getLogicalDecompositions();
        if (logicalDecompositions == null) {
            logicalDecompositions = new ArrayList<>();
        }
        if (logicalDecompositions.size() == 0) {
            logicalDecompositions.add(new LogicalDecomposition("primary"));
        }

        List<CrossCuttingConcernsGroup> crossCuttingConcerns = codeConfiguration.getCrossCuttingConcerns();

        List<Pair<String, List<SourceCodeAspect>>> logicalDecompositionPairs = new ArrayList<>();
        logicalDecompositions.forEach(logicalDecomposition -> logicalDecompositionPairs.add(new ImmutablePair<>(logicalDecomposition.getName(), logicalDecomposition.getComponents())));

        List<SourceCodeAspect> logicalComponents = logicalDecompositions.get(0).getComponents();

        List<Pair<String, List<CrossCuttingConcern>>> concernsDecompositionPairs = new ArrayList<>();
        List<CrossCuttingConcern> allConcerns = new ArrayList<>();
        concernsDecompositionPairs.add(0, new ImmutablePair<>("all", allConcerns));
        codeConfiguration.getCrossCuttingConcerns().forEach(group -> {
            concernsDecompositionPairs.add(new ImmutablePair<>(group.getName(), group.getConcerns()));
            group.getConcerns().forEach(concernInGroup -> {
                concernInGroup.setName(group.getName() + ": " + concernInGroup.getName());
                allConcerns.add(concernInGroup);
            });
        });

        Platform.runLater(() -> {
            scopeAspectsTablePane.refresh(scopesWithExtensions, codeConfiguration.getMain());
            logicalComponentsTablePane.setAspectSelections(logicalDecompositionPairs);
            logicalComponentsTablePane.refresh(logicalComponents, codeConfiguration.getMain());
            crossCuttingAspectsTablePane.setConcernsSelections(concernsDecompositionPairs);
            if (crossCuttingConcerns.size() > 0) {
                crossCuttingAspectsTablePane.refresh(allConcerns, codeConfiguration.getMain());
            }
        });

        progressFeedback.setText("Done.");
    }

    public void load(SourceCodeAspect aspect) {
        aspectFilesBrowserPane.getSearchPane().createSearcheableFilesCache(aspect);
    }

    public void reload() {
    }

    public AspectsTablePane getScopeAspectsTablePane() {
        return scopeAspectsTablePane;
    }

    public void setScopeAspectsTablePane(AspectsTablePane scopeAspectsTablePane) {
        this.scopeAspectsTablePane = scopeAspectsTablePane;
    }

    public AspectsTablePane getLogicalComponentsTablePane() {
        return logicalComponentsTablePane;
    }

    public void setLogicalComponentsTablePane(AspectsTablePane logicalComponentsTablePane) {
        this.logicalComponentsTablePane = logicalComponentsTablePane;
    }

    public AspectsTablePane getCrossCuttingAspectsTablePane() {
        return crossCuttingAspectsTablePane;
    }

    public void setCrossCuttingAspectsTablePane(AspectsTablePane crossCuttingAspectsTablePane) {
        this.crossCuttingAspectsTablePane = crossCuttingAspectsTablePane;
    }

    public CodeConfiguration getCodeConfiguration() {
        return codeConfiguration;
    }

    public CodeConfigurationView getCodeConfigurationView() {
        return codeConfigurationView;
    }

    public AspectFilesBrowserPane getAspectFilesBrowserPane() {
        return aspectFilesBrowserPane;
    }
}