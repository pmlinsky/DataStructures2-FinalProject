package finalProject;

import java.sql.*;
import java.util.*;

public class Main {
	
	public static void main(String[] args) {
		
		Set<String> allInfo = new HashSet<>();
		Queue<String> urls = new LinkedList<>();
		Set<String> htmls = new HashSet<>();
		Set<String> phoneNumbers = new HashSet<>();
		Set<String> emails = new HashSet<>();
		Set<String> iLinks = new HashSet<>();
		Set<String> eLinks = new HashSet<>();
		Set<String> dates = new HashSet<>();
		Set<String> times = new HashSet<>();
		Flag uIRunning = new Flag();
		Locks locks = new Locks();
			
		String[] tables = {"CURRENT_QUEUE", 
				"PHONE_NUMS", "EMAILS", "TOURO_URLS", 
				 "EXTERNAL_URLS", "DATES", "TIMES", "HTMLS"};
		
		try {
			String dbUrl = "jdbc:sqlserver://localhost;" + 
				"instance=DESKTOP-FGFE61N\\SQLEXPRESS01;" +
				"databaseName=DS2_PROJ;" + 
				"integratedSecurity=true";
			Connection conn = DriverManager.getConnection(dbUrl);
			
			String table, info, delete;
			Statement stmnt, stmntD;
			ResultSet rs;

			for (int i = 0; i < tables.length; i++) {
				table = tables[i];
			    info = "SELECT * FROM "+table+";";
			    stmnt = conn.createStatement();
			    conn.createStatement();
				rs = stmnt.executeQuery(info);
				while(rs.next()) {
					switch (table) {					
						case "CURRENT_QUEUE":
							urls.add(rs.getString("Urls"));
							break;
						case "PHONE_NUMS":
							phoneNumbers.add(rs.getString("ScrapedInfo"));
							break;
						case "EMAILS":
							emails.add(rs.getString("ScrapedInfo"));
							break;
						case "TOURO_URLS":
							iLinks.add(rs.getString("ScrapedInfo"));
							break;
						case "EXTERNAL_URLS":
							eLinks.add(rs.getString("ScrapedInfo"));
							break;
						case "DATES":
							dates.add(rs.getString("ScrapedInfo"));
							break;
						case "TIMES":
							times.add(rs.getString("ScrapedInfo"));
							break;
						case "HTMLS":
							htmls.add(rs.getString("ScrapedInfo"));
							break;
						default:
							throw new Exception("Invalid table.");
					}
				}
			}
			String[] deleteTables = {"CURRENT_QUEUE", "HTMLS"};
			for (int i = 0; i < deleteTables.length; i++) {
			    delete = "DELETE FROM "+deleteTables[i]+";";
			    stmntD = conn.createStatement();
				stmntD.executeUpdate(delete);
			}
		}
		catch(Exception e) {
			System.out.println(e.getMessage());
			System.out.println(e.getStackTrace());
		}
		
		allInfo.addAll(phoneNumbers);
		allInfo.addAll(emails);
		allInfo.addAll(iLinks);
		allInfo.addAll(eLinks);
		allInfo.addAll(dates);
		allInfo.addAll(times);

		if (urls.isEmpty()) {
			urls.add("https://www.touro.edu/");
		}
	
		Thread crawler = new Thread(new Crawler(urls, htmls, iLinks, eLinks, uIRunning, locks));
		Thread scraper1 = new Thread(new Scraper(htmls, phoneNumbers, emails, eLinks, dates, times, uIRunning, locks));
		Thread scraper2 = new Thread(new Scraper(htmls, phoneNumbers, emails, eLinks, dates, times, uIRunning, locks));
		Thread ui = new Thread(new UI(allInfo, urls, htmls, phoneNumbers, emails, iLinks, eLinks, dates, times, uIRunning, locks));
		
		uIRunning.setFlag(true);

		crawler.start();
		scraper1.start();
		scraper2.start();
		ui.start();
		
		
		try {
			crawler.join();
			scraper1.join();
			scraper2.join();
			ui.join();
		} catch (InterruptedException e1) {
			e1.printStackTrace();
		}
		
		System.out.println("Thank you and good bye!");

	}
	
	
}
