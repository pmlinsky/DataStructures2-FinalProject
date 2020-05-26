package finalProject;

import java.io.IOException;
import java.util.Queue;
import java.util.Set;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

//This class if finished (for now :)
public class Crawler implements Runnable {
	
	private Queue<String> urls;
	private Set<String> htmls;
	private Set<String> touroLinks;
	private Set<String> eLinks;
	private Flag uIRunning;
	private Locks locks;


	public Crawler(Queue<String> urls, Set<String> htmls, Set<String> touroLinks, Set<String> eLinks, Flag uIRunning, Locks locks) {
		this.urls = urls;
		this.touroLinks = touroLinks;
		this.eLinks = eLinks;
		this.uIRunning = uIRunning;
		this.locks = locks;
		this.htmls = htmls;
	}
	
	@Override
	public void run() {
		Document doc;
		
		while (!urls.isEmpty() && uIRunning.getFlag()) {
			try {
				long startDownload = System.currentTimeMillis();
				doc = Jsoup.connect(
						urls.poll()).get();
				long endDownload = System.currentTimeMillis();
				htmls.add(doc.html());
				Elements links = doc.select("a[href]");
				for (Element link :links) {
					String absHref = link.attr("abs:href");
					if (absHref.contains("https://www.touro.edu/") || 
							absHref.contains("https://touroone.touro.edu/") ||
							absHref.contains("https://tcus.") ||
							absHref.contains("https://tourocom.") ||
							absHref.contains("https://gse.touro.edu") ||
							absHref.contains("http://apply.touro")) {
							if (!touroLinks.contains(absHref)) {
								urls.add(absHref);
								touroLinks.add(absHref);
							}
					}
					else {
						eLinks.add(absHref);
					}
				}
				long endScrape = System.currentTimeMillis();
				long timeDownloaded = endDownload - startDownload;
				long timeScraped = endScrape - endDownload;
				long waitTime = Long.max(10000 - timeScraped, 2 * timeDownloaded - timeScraped);
				Thread.sleep(waitTime);
				
			} catch (IOException e) {
				e.printStackTrace();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		
	}
}
