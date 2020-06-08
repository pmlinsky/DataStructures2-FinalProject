package finalProject;

import java.io.IOException;
import java.util.List;
import java.util.Queue;
import java.util.Set;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import javafx.concurrent.Task;

public class Crawler extends Task<Void> {
	
	private Queue<String> urls;
	private List<Document> htmls;
	private Set<String> touroLinks;
	private Set<String> eLinks;
	private Flag uIRunning;
	private Locks locks;


	public Crawler(Queue<String> urls, List<Document> htmls, Set<String> touroLinks, Set<String> eLinks, Flag uIRunning, Locks locks) {
		this.urls = urls;
		this.touroLinks = touroLinks;
		this.eLinks = eLinks;
		this.uIRunning = uIRunning;
		this.locks = locks;
		this.htmls = htmls;
	}
	
	@Override
	public Void call() {
		Document doc;
		
		while (uIRunning.getFlag()) {
			if (urls.isEmpty()) {
				try {
					Thread.sleep(5000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
			else {
				try {
					long startDownload = System.currentTimeMillis();
					synchronized (locks.getUrl_LOCK()) {
						doc = Jsoup.connect(
							urls.poll()).get();
					}
					long endDownload = System.currentTimeMillis();
					synchronized (locks.getHTML_LOCK()) {
						htmls.add(doc);
					}
					Elements links = doc.select("a[href]");
					for (Element link :links) {
						String absHref = link.attr("abs:href");
						if (absHref.contains("https://www.touro.edu") ||
								absHref.contains("http://las.touro") ||
								absHref.contains("http://shs.touro") ||
								absHref.contains("http://www.touro.") ||
								absHref.contains("https://las.touro.") ||
								absHref.contains("https://las.touro") ||
								absHref.contains("https://lcm.touro") ||
								absHref.contains("https://touroone.touro.edu") ||
								absHref.contains("https://tcus.") ||
								absHref.contains("https://tourocom.") ||
								absHref.contains("https://gse.touro.edu") ||
								absHref.contains("http://apply.touro")) {
								if (!touroLinks.contains(absHref)) {
									synchronized(locks.getUrl_LOCK()) {
										urls.add(absHref);
									}
									synchronized(locks.getTl_LOCK()) {
										touroLinks.add(absHref);
									}
								}
						}
						else {
							synchronized(locks.getEl_LOCK()) {
								eLinks.add(absHref);
							}
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
		return null;
	}
}