/**
 * 
 */
package crawlerlucene.google.crawler4j.multithreaded.constant;

import org.jfree.data.Range;

/**
 * This contains all the compile constants required during the execution of the
 * report or statistical display of the data sets.
 * 
 * @author sumit
 * 
 */
public final class ReportConstants {

	public static final int FETCH_RATE_DB = 1000;
	public static final int TIMER_SERIES_CHART_DOMAIN_MILLISEONDS = 60 * FETCH_RATE_DB;

	public static final Range DFLT_Y_AXIS_RANGE = new Range(0.0, 200.0);
}
