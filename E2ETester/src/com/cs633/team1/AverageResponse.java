package com.cs633.team1;

/**
 * Class to store data related to average response time
 * @author mckeon
 *
 */
public class AverageResponse {

	private int testNum;
	private int numRuns;
	private int avgResponse;
	
	public AverageResponse() {
		this.testNum = 1;
		this.numRuns = 0;
		this.avgResponse = 0;
	}
	
	/**
	 * Calculate the average response time
	 * @param responseTime
	 */
	public void calcAverage(int responseTime) {
		this.avgResponse = (this.avgResponse * this.numRuns + responseTime) / (++numRuns);
	}

	public int getTestNum() {
		return testNum;
	}

	public void setTestNum(int testNum) {
		this.testNum = testNum;
	}

	public int getNumRuns() {
		return numRuns;
	}

	public void setNumRuns(int numRuns) {
		this.numRuns = numRuns;
	}

	public int getAvgResponse() {
		return avgResponse;
	}

	public void setAvgResponse(int avgResponse) {
		this.avgResponse = avgResponse;
	}
}
