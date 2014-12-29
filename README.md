Serach Engine : Text Base
================================================================
This project covers the basic functionality that search engine provides.
It included fetaures like Url crawling and Indexing web pages based on terms.
This project also includes creating medium range web browsers using Java. Used to simulate the search result.
Multilingual fetures not supported in the project. That will be future part.

Following are the performance measures considered:

While implementing parallelization of search engine implementation is considered. The demo application has incorporated I/O hashing based database and Flat file system based index generation. It has added optimization statistics display through UI charts.

The system used for developments is as follows:
Open source Application program Interface(API) , used are , Apache Lucene for indexing and inbuilt searching implementation support, Crawler-4j for crawling the web, Tika API parser for parsing web and document text for transforming into indexing format and JFree chart for displaying optimization statistics.
The Following metric are measured:

Metric:	Quantity/Unit
-----------------------------------------------------------
System:	 i5 –intel core , 6 GB RAM, L3 Cache
Number Of Crawler thread (number of  lightweight process-for multiprocessing) used:	6 for each web URL crawling
Thread Pool Size (contain crawler thread, reporting thread, database reader and writer thread): 	10 Main thread, out of which 7 threads each contains 6 crawling thread and remaining 3 thread for other operations.
Number Of Indexer thread:	20 
Number Of search thread:	1 (Can be more than one, but it is kept to simulate one user.)
Hash Db I/O hash implementation	Can handle data read and write in parallel. Can handle data up to terabytes.
Search efficiency	On average: 2000+ hits in 120 or lesser milliseconds.
Updated Search results while crawling achieved	Yes
Not Supported query	Start with wildcard such as “*hi” or “?hi” for efficiency purpose.
Number Of URL crawled per minute in average:	270 on average using above system
Number Of URL indexed per minute:	300/minute
Number Of Per page search result displayed	10 
Pagination supported	Yes, for optimization of search and avoiding load/stress on the Application
UI used to display the Search Result	Self-made Web Browser using Java WebView and Application support class.
