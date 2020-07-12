/*
 * Copyright (c) 2020 Željko Obrenović. All rights reserved.
 */

package nl.obren.sokrates.sourcecode.landscape;

import nl.obren.sokrates.sourcecode.Metadata;

import java.util.ArrayList;
import java.util.List;

public class LandscapeConfiguration {
    private Metadata metadata = new Metadata();
    private String analysisRoot = "";
    private String projectReportsUrlPrefix = "../";

    private int extensionThresholdLoc = 0;
    private int projectThresholdLocMain = 0;
    private int projectThresholdContributors = 2;
    private int contributorThresholdCommits = 2;

    private List<SubLandscapeLink> subLandscapes = new ArrayList<>();
    private List<SokratesProjectLink> projects = new ArrayList<>();

    public Metadata getMetadata() {
        return metadata;
    }

    public void setMetadata(Metadata metadata) {
        this.metadata = metadata;
    }

    public String getAnalysisRoot() {
        return analysisRoot;
    }

    public void setAnalysisRoot(String analysisRoot) {
        this.analysisRoot = analysisRoot;
    }

    public String getProjectReportsUrlPrefix() {
        return projectReportsUrlPrefix;
    }

    public void setProjectReportsUrlPrefix(String projectReportsUrlPrefix) {
        this.projectReportsUrlPrefix = projectReportsUrlPrefix;
    }

    public List<SubLandscapeLink> getSubLandscapes() {
        return subLandscapes;
    }

    public void setSubLandscapes(List<SubLandscapeLink> subLandscapes) {
        this.subLandscapes = subLandscapes;
    }

    public List<SokratesProjectLink> getProjects() {
        return projects;
    }

    public void setProjects(List<SokratesProjectLink> projects) {
        this.projects = projects;
    }

    public int getExtensionThresholdLoc() {
        return extensionThresholdLoc;
    }

    public void setExtensionThresholdLoc(int extensionThresholdLoc) {
        this.extensionThresholdLoc = extensionThresholdLoc;
    }

    public int getProjectThresholdLocMain() {
        return projectThresholdLocMain;
    }

    public void setProjectThresholdLocMain(int projectThresholdLocMain) {
        this.projectThresholdLocMain = projectThresholdLocMain;
    }

    public int getContributorThresholdCommits() {
        return contributorThresholdCommits;
    }

    public void setContributorThresholdCommits(int contributorThresholdCommits) {
        this.contributorThresholdCommits = contributorThresholdCommits;
    }

    public int getProjectThresholdContributors() {
        return projectThresholdContributors;
    }

    public void setProjectThresholdContributors(int projectThresholdContributors) {
        this.projectThresholdContributors = projectThresholdContributors;
    }
}
