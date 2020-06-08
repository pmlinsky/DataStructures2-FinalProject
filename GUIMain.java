package finalProject;

import java.sql.*;
import java.util.*;
import java.util.concurrent.*;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import javafx.application.Application;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.scene.layout.*;
import javafx.scene.control.*;


public class GUIMain extends Application {

	public static void main(String[] args) {
		
		launch(args);
	}
	
	@Override
	public void start(Stage primaryStage) {
		
		Set<String> allInfo = new HashSet<>();
		Queue<String> urls = new LinkedList<>();
		List<Document> htmls = new ArrayList<>();
		Set<String> phoneNumbers = new HashSet<>();
		Set<String> emails = new HashSet<>();
		Set<String> iLinks = new HashSet<>();
		Set<String> eLinks = new HashSet<>();
		Set<String> dates = new HashSet<>();
		Set<String> times = new HashSet<>();
		Flag uIRunning = new Flag();
		Locks locks = new Locks();
			
		Connection conn = loadProgram(allInfo, urls, htmls, phoneNumbers, emails, dates, times, iLinks, eLinks);
	
		Crawler crawler = new Crawler(urls, htmls, iLinks, eLinks, uIRunning, locks);
		Scraper scraper1 = new Scraper(htmls, phoneNumbers, emails, dates, times, uIRunning, locks);
		Scraper scraper2 = new Scraper(htmls, phoneNumbers, emails, dates, times, uIRunning, locks);
		
		uIRunning.setFlag(true);

		ExecutorService es = Executors.newFixedThreadPool(3);
		es.execute(crawler);
		es.execute(scraper1);
		es.execute(scraper2);
		
		Button b1 = new Button("1");
		Label l1 = new Label("view phone numbers");
		Button b2 = new Button("2");
		Label l2 = new Label("view email addresses");
		Button b3 = new Button("3");
		Label l3 = new Label("view dates");
		Button b4 = new Button("4");
		Label l4 = new Label("view times");
		Button b5 = new Button("5");
		Label l5 = new Label("view internal (Touro website) links");
		Button b6 = new Button("6");
		Label l6 = new Label("view external links");
		Button b7 = new Button("7");
		Label l7 = new Label("view total number of pages that were scraped");
		Button b8 = new Button("8");
		Label l8 = new Label("view number of pages left to be scraped");
		Button b9 = new Button("9");
		Label l9 = new Label("view phone numbers by area code");
		Label inputLabel = new Label();
		TextField input = new TextField();
		Button b10 = new Button("10");
		Label l10 = new Label("view Touro NEWS links");
		Button b11 = new Button("11");
		Label l11 = new Label("exit program");		
		
		HBox hbox1 = new HBox(b1, l1);
		HBox hbox2 = new HBox(b2, l2);
		HBox hbox3 = new HBox(b3, l3);
		HBox hbox4 = new HBox(b4, l4);
		HBox hbox5 = new HBox(b5, l5);
		HBox hbox6 = new HBox(b6, l6);
		HBox hbox7 = new HBox(b7, l7);
		HBox hbox8 = new HBox(b8, l8);
		HBox hbox9 = new HBox(b9, l9, inputLabel, input);
		HBox hbox10 = new HBox(b10, l10);
		HBox hbox11 = new HBox(b11, l11);
		
		VBox vbox1 = new VBox(hbox1, hbox2, hbox3, hbox4, hbox5, hbox6, hbox7, hbox8, hbox9, hbox10, hbox11);
		
		ListView<String> results = new ListView<>();
		
		VBox vbox2 = new VBox(vbox1, results);
		
		b1.setOnAction(b -> {
			results.getItems().clear();
			for (String n : phoneNumbers) {
				synchronized(locks.getPn_LOCK()) {
					results.getItems().add(n);
				}
			}
		});
		
		b2.setOnAction(b -> {
			results.getItems().clear();
			for (String n : emails) {
				synchronized(locks.getEmail_LOCK()) {
					results.getItems().add(n);
				}
			}
		});
		
		b3.setOnAction(b -> {
			results.getItems().clear();
			for (String n : dates) {
				synchronized(locks.getDate_LOCK()) {
					results.getItems().add(n);
				}
			}
		});
		
		b4.setOnAction(b -> {
			results.getItems().clear();
			for (String n : times) {
				synchronized(locks.getTime_LOCK()) {
					results.getItems().add(n);
				}
			}
		});
		
		b5.setOnAction(b -> {
			results.getItems().clear();
			for (String n : iLinks) {
				synchronized(locks.getTl_LOCK()) {
					results.getItems().add(n);
				}
			}
		});
		
		b6.setOnAction(b -> {
			results.getItems().clear();
			for (String n : eLinks) {
				synchronized(locks.getEl_LOCK()) {
					results.getItems().add(n);
				}
			}
		});
		
		b7.setOnAction(b -> {
			results.getItems().clear();
			int iLinksSize, urlsSize;
			synchronized(locks.getTl_LOCK()) {
				iLinksSize = iLinks.size();
			}
			synchronized(locks.getUrl_LOCK()) {
				urlsSize = urls.size();
			}
			int pagesDone = iLinksSize - urlsSize;
			results.getItems().add("Total pages scraped: " + pagesDone);
		});
		
		b8.setOnAction(b -> {
			results.getItems().clear();
			synchronized(locks.getUrl_LOCK()) {
				results.getItems().add("Total pages remaining to be scraped: " + urls.size());
			}
		});
		
		b9.setOnAction(b -> {
			results.getItems().clear();
			inputLabel.setText("Enter the area code of your choice:");
			String areaCode = input.getText();
			synchronized(locks.getPn_LOCK()) {		
				phoneNumbers.stream().filter(n -> n.startsWith(areaCode) || n.startsWith("("+areaCode))
				.forEach(n -> results.getItems().add(n));
			}
			inputLabel.setText("");
			input.setText("");
		});
		
		b10.setOnAction(b -> {
			results.getItems().clear();
			synchronized(locks.getTl_LOCK()) {			
				iLinks.stream().filter(i -> i.contains("news")).forEach(s -> results.getItems().add(s)); 
			}
		});
		
		b11.setOnAction(b -> {
			uIRunning.setFlag(false);
			results.getItems().clear();
			results.getItems().add("Saving work to database....");
			
			try {			
				String sql = "INSERT INTO CURRENT_QUEUE VALUES(?);";
				PreparedStatement st = conn.prepareStatement(sql);
				for (String s : urls) {
					if (!allInfo.contains(s)) {
						st.setString(1, s);
						try {
							st.executeUpdate();
						} catch (SQLException e) {
							continue;
						}
					}
				}
				sql = "INSERT INTO PHONE_NUMS VALUES(?);";
				st = conn.prepareStatement(sql);
				for (String s : phoneNumbers) {
					if (!allInfo.contains(s)) {
						st.setString(1, s);
						try {
							st.execute();
						} catch (SQLException e) {
							continue;
						}
					}
				}
				sql = "INSERT INTO EMAILS VALUES(?);";
				st = conn.prepareStatement(sql);
				for (String s : emails) {
					if (!allInfo.contains(s)) {
						st.setString(1, s);
						try {
							st.execute();
						} catch (SQLException e) {
							continue;
						}
					}
				}
				sql = "INSERT INTO TOURO_URLS VALUES(?);";
				st = conn.prepareStatement(sql);
				for (String s : iLinks) {
					if (!allInfo.contains(s)) {
						st.setString(1, s);
						try {
							st.execute();
						} catch (SQLException e) {
							continue;
						}
					}
				}
				sql = "INSERT INTO EXTERNAL_URLS VALUES(?);";
				st = conn.prepareStatement(sql);
				for (String s : eLinks) {
					if (!allInfo.contains(s)) {
						st.setString(1, s);
						try {
							st.execute();
						} catch (SQLException e) {
							continue;
						}
					}
				}
				sql = "INSERT INTO DATES VALUES(?);";
				st = conn.prepareStatement(sql);
				for (String s : dates) {
					if (!allInfo.contains(s)) {
						st.setString(1, s);
						try {
							st.executeUpdate();
						} catch (SQLException e) {
							continue;
						}
					}
				}
				sql = "INSERT INTO TIMES VALUES(?);";
				st = conn.prepareStatement(sql);
				for (String s : times) {
					if (!allInfo.contains(s)) {
						st.setString(1, s);
						try {
							st.execute();
						} catch (SQLException e) {
							continue;
						}
					}
				}
				sql = "INSERT INTO HTMLS VALUES(?);";
				st = conn.prepareStatement(sql);
				for (Document d : htmls) {
					st.setString(1, d.html());
					try {
						st.execute();
					} catch (SQLException e) {
						continue;
					}			
				}				
			} catch(Exception e) {
				System.out.println(e.getMessage());
				System.out.println(e.getStackTrace());
			}
			
			results.getItems().add("Thank you and good bye!");
		});
		
		Scene scene = new Scene(vbox2);
		
		primaryStage.setScene(scene);
		primaryStage.show();

		es.shutdown();
	}
	
	public static Connection loadProgram(Set<String> allInfo, Queue<String> urls, List<Document> htmls, Set<String> phoneNumbers,
			Set<String> emails, Set<String> dates, Set<String> times, Set<String> iLinks, Set<String> eLinks) {
		
		String[] tables = {"CURRENT_QUEUE", 
				"PHONE_NUMS", "EMAILS", "TOURO_URLS", 
				 "EXTERNAL_URLS", "DATES", "TIMES", "HTMLS"};
		Connection conn = null;
		try {
			String dbUrl = "jdbc:sqlserver://localhost;" + 
				"instance=DESKTOP-FGFE61N\\SQLEXPRESS01;" +
				"databaseName=DS2_PROJ;" + 
				"integratedSecurity=true";
			conn = DriverManager.getConnection(dbUrl);
			
			String table, info;
			Statement stmnt;
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
							htmls.add(Jsoup.parse(rs.getString("ScrapedInfo")));
							break;
						default:
							throw new Exception("Invalid table.");
					}
				}
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
		return conn;
	}
}


