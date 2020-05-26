package finalProject;

import java.sql.*;
import java.util.*;
import java.util.stream.Stream;

public class UI implements Runnable {

	private Set<String> allInfo;
	private Queue<String> urls;
	private Set<String> htmls;
	private Set<String> phoneNumbers;
	private Set<String> emails;
	private Set<String> iLinks;
	private Set<String> eLinks;
	private Set<String> dates;
	private Set<String> times;
	private Flag uIRunning;
	private Locks locks;
	
	public UI(Set<String> allInfo, Queue<String> urls, Set<String> htmls, Set<String> phoneNumbers,
			Set<String> emails, Set<String> iLinks, Set<String> eLinks,
			Set<String> dates, Set<String> times, Flag uIRunning, Locks locks) {
		this.allInfo = allInfo;
		this.urls = urls;
		this.htmls = htmls;
		this.phoneNumbers = phoneNumbers;
		this.emails = emails;
		this.iLinks = iLinks;
		this.eLinks = eLinks;
		this.dates = dates;
		this.times = times;
		this.uIRunning = uIRunning;
		this.locks = locks;
		
	}
	
	enum MenuOption{PHONE_NUMBERS, EMAIL_ADDRESSES, ILINKS, ELINKS, DATES, TIMES, PAGES_DONE, PAGES_LEFT, NEWS, EXIT}

	@Override
	public void run() {
		
		MenuOption choice;
		while ((choice = getUserChoice()) != MenuOption.EXIT) {
			switch (choice) {
			case DATES:
				System.out.println("These are all the dates found until now: ");
				System.out.println("________________________________________");
				for (String d : dates) {
					System.out.println(d);
				}
				break;
			case ELINKS:
				System.out.println("These are all the external links found until now: ");
				System.out.println("________________________________________");
				for (String l : eLinks) {
					System.out.println(l);
				}
				break;
			case EMAIL_ADDRESSES:
				System.out.println("These are all the email addresses found until now: ");
				System.out.println("_________________________________________________");
				for (String e : emails) {
					System.out.println(e);
				}
				break;
			case ILINKS:
				System.out.println("These are all the internal links found until now: ");
				System.out.println("________________________________________");
				for (String i : iLinks) {
					System.out.println(i);
				}
				break;
			case NEWS:
				System.out.println("These are all the internal links about news found until now: ");
				System.out.println("________________________________________");
				Stream<String> stream = iLinks.stream().filter(i -> i.contains("news")); 
				stream.forEach(s -> System.out.println(s));
				break;
			case PAGES_DONE:
				int pagesDone = iLinks.size() - urls.size();
				System.out.println("The total number of pages scraped already: " + pagesDone);
				//this can become negative FIX THIS!!!!!
				break;
			case PAGES_LEFT:
				System.out.println("The number of pages remaining to be scraped: " + urls.size());
				break;
			case PHONE_NUMBERS:
				System.out.println("These are all the phone numbers found until now: ");
				System.out.println("________________________________________________");
				for (String n : phoneNumbers) {
					System.out.println(n);
				}
				break;
			case TIMES:
				System.out.println("These are all the times found until now: ");
				System.out.println("________________________________________");
				for (String t : times) {
					System.out.println(t);
				}
				break;
			default:
				System.out.println("Invalid option. Please try again.");
				break;
			}
		}
		System.out.println("Saving work to database....");

		try {
			String dbUrl = "jdbc:sqlserver://localhost;" + 
				"instance=DESKTOP-FGFE61N\\SQLEXPRESS01;" +
				"databaseName=DS2_PROJ;" + 
				"integratedSecurity=true";
			Connection conn = DriverManager.getConnection(dbUrl);
			
			String sql = "INSERT INTO CURRENT_QUEUE VALUES(?);";
			PreparedStatement st = conn.prepareStatement(sql);
			for (String s : urls) {
				st.setString(1, s);
				st.execute();
			}
			sql = "INSERT INTO PHONE_NUMS VALUES(?);";
			st = conn.prepareStatement(sql);
			for (String s : phoneNumbers) {
				if (!allInfo.contains(s)) {
					st.setString(1, s);
					st.execute();
				}
			}
			sql = "INSERT INTO EMAILS VALUES(?);";
			st = conn.prepareStatement(sql);
			for (String s : emails) {
				if (!allInfo.contains(s)) {
					st.setString(1, s);
					st.execute();
				}
			}
			sql = "INSERT INTO TOURO_URLS VALUES(?);";
			st = conn.prepareStatement(sql);
			for (String s : iLinks) {
				if (!allInfo.contains(s)) {
					st.setString(1, s);
					st.execute();
				}
			}
			sql = "INSERT INTO EXTERNAL_URLS VALUES(?);";
			st = conn.prepareStatement(sql);
			for (String s : eLinks) {
				if (!allInfo.contains(s)) {
					st.setString(1, s);
					st.execute();
				}
			}
			sql = "INSERT INTO DATES VALUES(?);";
			st = conn.prepareStatement(sql);
			for (String s : dates) {
				if (!allInfo.contains(s)) {
					st.setString(1, s);
					st.execute();
				}
			}
			sql = "INSERT INTO TIMES VALUES(?);";
			st = conn.prepareStatement(sql);
			for (String s : times) {
				if (!allInfo.contains(s)) {
					st.setString(1, s);
					st.execute();
				}
			}/*
			sql = "INSERT INTO HTMLS VALUES(?);";
			st = conn.prepareStatement(sql);
			for (String s : htmls) {
				st.setString(1, s);
				st.execute();			
			}*/
			
			uIRunning.setFlag(false);
			
		}
		catch(Exception e) {
			System.out.println(e.getMessage());
			System.out.println(e.getStackTrace());
		}

	}
	
	public static MenuOption getUserChoice() {
		displayMenu();
		MenuOption[] options = new MenuOption[] {MenuOption.PHONE_NUMBERS, MenuOption.EMAIL_ADDRESSES, MenuOption.DATES, 
				MenuOption.TIMES, MenuOption.ILINKS, MenuOption.ELINKS, MenuOption.PAGES_DONE, MenuOption.PAGES_LEFT, MenuOption.NEWS, MenuOption.EXIT};
		Scanner kb = new Scanner(System.in);
		int choice;
		while ((choice = kb.nextInt()) <= 0 || choice > 10) {
			System.out.println("Invalid option. Please enter a choice between 1 and 10 ");
		}
		kb.nextLine();
		return options[choice-1];
	}		
		
	public static void displayMenu() {
		System.out.println("Please choose from the following options:");
		System.out.println("_________________________________________");
		System.out.println("1. view phone numbers");
		System.out.println("2. view email addresses");
		System.out.println("3. view dates");
		System.out.println("4. view times");
		System.out.println("5. view internal (Touro website) links");
		System.out.println("6. view external links");
		System.out.println("7. view total number of pages that were scraped");
		System.out.println("8. view number of pages left to be scraped");
		System.out.println("9. view Touro NEWS links");
		System.out.println("10. exit program");
	}


}
