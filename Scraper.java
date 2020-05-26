package finalProject;

import java.util.*;

public class Scraper implements Runnable{
	
	//Remember to lock on shared memoryQQ
	
	private Set<String> htmls;
	private Set<String> phoneNumbers;
	private Set<String> emails;
	private Set<String> eLinks;
	private Set<String> dates;
	private Set<String> times;
	private Flag uIRunning;
	private Locks locks;
	
	public Scraper(Set<String> htmls, Set<String> phoneNumbers,
			Set<String> emails, Set<String> eLinks,
			Set<String> dates, Set<String> times, Flag uIRunning, Locks locks) {
		this.htmls = htmls;
		this.phoneNumbers = phoneNumbers;
		this.emails = emails;
		this.eLinks = eLinks;
		this.dates = dates;
		this.times = times;
		this.uIRunning = uIRunning;
		this.locks = locks;
	}
	
	@Override
	public void run() {
		
		while (!htmls.isEmpty() && uIRunning.getFlag()) {
			//add regexes with jsoup and add info to sets (Chani)
		}
	}

}
