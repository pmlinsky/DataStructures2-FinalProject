package finalProject;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import javafx.concurrent.Task;

public class Scraper extends Task<Void> {
		
	private List<Document> htmls;
	private Set<String> phoneNumbers;
	private Set<String> emails;
	private Set<String> dates;
	private Set<String> times;
	private Flag uIRunning;
	private Locks locks;
	
	public Scraper(List<Document> htmls, Set<String> phoneNumbers,
			Set<String> emails, Set<String> dates, Set<String> times, Flag uIRunning, Locks locks) {
		this.htmls = htmls;
		this.phoneNumbers = phoneNumbers;
		this.emails = emails;
		this.dates = dates;
		this.times = times;
		this.uIRunning = uIRunning;
		this.locks = locks;
	}
	
	@Override
	public Void call() {
		Document doc;
		
		while (uIRunning.getFlag()) {
			boolean empty;
			synchronized (locks.getHTML_LOCK()) {
				empty = htmls.isEmpty();
			}
			if (empty) {
				try {
					Thread.sleep(5000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}		
			else {
				synchronized(locks.getHTML_LOCK()) {
					doc = htmls.remove(0);
				}
				String phone = "\\(?\\d{3}\\)?(-|\\s+|\\.)\\d{3}(-|\\s+|\\.)\\d{4}";
				Pattern phonePattern = Pattern.compile(phone);
				Elements pNums = doc.getElementsMatchingOwnText(phonePattern);
				
				for (Element num : pNums) {
					Matcher m = phonePattern.matcher(num.text());
					if (m.find()) {
						synchronized (locks.getPn_LOCK()){
							phoneNumbers.add(m.group().trim());
						}
					}
				}
				
				String email = "\\S+@\\S+(\\.\\S+)+";
				Pattern emlPattern = Pattern.compile(email);
				Elements emls = doc.getElementsMatchingOwnText(emlPattern);
				
				for (Element ml : emls) {
					Matcher m = emlPattern.matcher(ml.text());
					if (m.find()) {
						synchronized(locks.getEmail_LOCK()) {
							emails.add(m.group().trim());
						}
					}
				}
				
				String date = "\\S+\\s\\d{1,2}(\\s|,\\s)\\d{4}";
				Pattern datePattern = Pattern.compile(date);
				Elements dts = doc.getElementsMatchingOwnText(datePattern);
				
				for(Element dt : dts) {
					Matcher m = datePattern.matcher(dt.text());
					if (m.find()) {
						synchronized(locks.getDate_LOCK()) {
							dates.add(m.group().trim());
						}
					}
				}
				
				String time = "\\d{1,2}:\\d{2}\\s(AM|PM)";
				Pattern timePattern = Pattern.compile(time);
				Elements tms = doc.getElementsMatchingOwnText(timePattern);
				
				for(Element tm : tms) {
					Matcher m = timePattern.matcher(tm.text());
					if (m.find()) {
						synchronized(locks.getTime_LOCK()) {
							times.add(m.group().trim());
						}
					}
				}
			}
		}
		return null;
	}

}
