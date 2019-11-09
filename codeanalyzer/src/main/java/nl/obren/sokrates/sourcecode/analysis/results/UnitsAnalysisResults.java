package nl.obren.sokrates.sourcecode.analysis.results;

import nl.obren.sokrates.sourcecode.stats.RiskDistributionStats;
import nl.obren.sokrates.sourcecode.units.UnitInfo;

import java.util.ArrayList;
import java.util.List;

public class UnitsAnalysisResults {
    private int totalNumberOfUnits;
    private int linesOfCodeInUnits;

    private RiskDistributionStats unitSizeRiskDistribution = new RiskDistributionStats("system");
    private RiskDistributionStats cyclomaticComplexityRiskDistribution = new RiskDistributionStats("system");

    private List<RiskDistributionStats> unitSizeRiskDistributionPerExtension = new ArrayList<>();
    private List<List<RiskDistributionStats>> unitSizeRiskDistributionPerComponent = new ArrayList<>();
    private List<UnitInfo> longestUnits = new ArrayList<>();

    private List<RiskDistributionStats> cyclomaticComplexityRiskDistributionPerExtension = new ArrayList<>();
    private List<List<RiskDistributionStats>> cyclomaticComplexityRiskDistributionPerComponent = new ArrayList<>();
    private List<UnitInfo> mostComplexUnits = new ArrayList<>();

    private List<UnitInfo> allUnits = new ArrayList<>();

    public int getTotalNumberOfUnits() {
        return totalNumberOfUnits;
    }

    public void setTotalNumberOfUnits(int totalNumberOfUnits) {
        this.totalNumberOfUnits = totalNumberOfUnits;
    }

    public int getLinesOfCodeInUnits() {
        return linesOfCodeInUnits;
    }

    public void setLinesOfCodeInUnits(int linesOfCodeInUnits) {
        this.linesOfCodeInUnits = linesOfCodeInUnits;
    }

    public RiskDistributionStats getUnitSizeRiskDistribution() {
        return unitSizeRiskDistribution;
    }

    public void setUnitSizeRiskDistribution(RiskDistributionStats unitSizeRiskDistribution) {
        this.unitSizeRiskDistribution = unitSizeRiskDistribution;
    }

    public RiskDistributionStats getCyclomaticComplexityRiskDistribution() {
        return cyclomaticComplexityRiskDistribution;
    }

    public void setCyclomaticComplexityRiskDistribution(RiskDistributionStats cyclomaticComplexityRiskDistribution) {
        this.cyclomaticComplexityRiskDistribution = cyclomaticComplexityRiskDistribution;
    }

    public List<RiskDistributionStats> getUnitSizeRiskDistributionPerExtension() {
        return unitSizeRiskDistributionPerExtension;
    }

    public void setUnitSizeRiskDistributionPerExtension(List<RiskDistributionStats> unitSizeRiskDistributionPerExtension) {
        this.unitSizeRiskDistributionPerExtension = unitSizeRiskDistributionPerExtension;
    }

    public List<List<RiskDistributionStats>> getUnitSizeRiskDistributionPerComponent() {
        return unitSizeRiskDistributionPerComponent;
    }

    public void setUnitSizeRiskDistributionPerComponent(List<List<RiskDistributionStats>> unitSizeRiskDistributionPerComponent) {
        this.unitSizeRiskDistributionPerComponent = unitSizeRiskDistributionPerComponent;
    }

    public List<RiskDistributionStats> getCyclomaticComplexityRiskDistributionPerExtension() {
        return cyclomaticComplexityRiskDistributionPerExtension;
    }

    public void setCyclomaticComplexityRiskDistributionPerExtension(List<RiskDistributionStats> cyclomaticComplexityRiskDistributionPerExtension) {
        this.cyclomaticComplexityRiskDistributionPerExtension = cyclomaticComplexityRiskDistributionPerExtension;
    }

    public List<List<RiskDistributionStats>> getCyclomaticComplexityRiskDistributionPerComponent() {
        return cyclomaticComplexityRiskDistributionPerComponent;
    }

    public void setCyclomaticComplexityRiskDistributionPerComponent(List<List<RiskDistributionStats>> cyclomaticComplexityRiskDistributionPerComponent) {
        this.cyclomaticComplexityRiskDistributionPerComponent = cyclomaticComplexityRiskDistributionPerComponent;
    }

    public List<UnitInfo> getLongestUnits() {
        return longestUnits;
    }

    public void setLongestUnits(List<UnitInfo> longestUnits) {
        this.longestUnits = longestUnits;
    }

    public List<UnitInfo> getMostComplexUnits() {
        return mostComplexUnits;
    }

    public void setMostComplexUnits(List<UnitInfo> mostComplexUnits) {
        this.mostComplexUnits = mostComplexUnits;
    }

    public List<UnitInfo> getAllUnits() {
        return allUnits;
    }

    public void setAllUnits(List<UnitInfo> allUnits) {
        this.allUnits = allUnits;
    }
}