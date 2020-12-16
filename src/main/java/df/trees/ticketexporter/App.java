package df.trees.ticketexporter;

import java.io.File;
import java.io.FileReader;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

public class App {
	public enum OBJECT_RUN {
		TICKETS, TICKET_FIELDS, USERS, BRANDS, ORGANIZATIONS, METRIC_EVENTS, SCHEDULE
	}

	public static void main(String[] args) {
		SnapSnap snap = new SnapSnap();
		GetTickets tickets = new GetTickets();
		GetUsers users = new GetUsers();
		GetTicketFields ticketFields = new GetTicketFields();
		GetBrands brands = new GetBrands();
		GetOrganizations organizations = new GetOrganizations();
		GetSchedule schedule = new GetSchedule();
		App app = new App();
		JSONObject configJson = app.readConfig();

		// GetTicketMetrics metrics = new GetTicketMetrics(); // ALREADY INCLUDE ON
		// TICKET INCREMENTAL
		GetTicketMetricsEvent metricsEvent = new GetTicketMetricsEvent();

		String newDomain = configJson.get("domain").toString();
		String newUsername = configJson.get("username").toString();
		String newPassowrd = configJson.get("password").toString();

		long timeStart = (long) configJson.get("start_time");
		long timeEnd = (long) configJson.get("end_time");

		OBJECT_RUN objectRun = OBJECT_RUN.TICKETS;

//		Date date = new Date();
//		Calendar cal = Calendar.getInstance();
//		cal.setTime(date);
//
//		// REDUCE MONTH
//		cal.add(Calendar.DATE, -incrementalParameter);
//		long unixTime = (cal.getTimeInMillis() / 1000L);

		System.out.println("=== EXPORT DOMAIN: " + newDomain + " ===");
		System.out.println("=== TIMESTAMP START: " + timeStart + " ===");
		System.out.println("=== TIMESTAMP END: " + timeEnd + " ===");

		switch (objectRun) {
		case TICKETS:
			/* START FROM TICKET FIELDS */
			tickets.doGetTicketFields(snap.ZD_TICKET_FIELDS_API(newDomain),
					snap.ZD_TICKET_INCREMENTAL_API(timeStart, newDomain), newDomain, newUsername, newPassowrd, timeEnd);
			break;
		case TICKET_FIELDS:
			ticketFields.doGetTicketFields(snap.ZD_TICKET_FIELDS_API(newDomain), newDomain, newUsername, newPassowrd);
			break;
		case USERS:
			users.doGetUser(snap.ZD_USER_API(newDomain), newDomain, newUsername, newPassowrd);
			break;
		case ORGANIZATIONS:
			organizations.doGetOrganizations(snap.ZD_ORGANIZATION_API(newDomain), newDomain, newUsername, newPassowrd);
			break;
		case BRANDS:
			brands.doGetBrands(snap.ZD_BRAND_API(newDomain), newDomain, newUsername, newPassowrd);
			break;
		case METRIC_EVENTS:
			metricsEvent.doGetMetricsEvent(snap.ZD_TICKETEVENT_INCREMENTAL_API(timeStart, newDomain), newDomain,
					newUsername, newPassowrd);
			break;
		case SCHEDULE:
			schedule.doGetSchedule(snap.ZD_SCHEDULE_API(newDomain), newDomain, newUsername, newPassowrd);
			break;
		default:
			System.out.println("DEFAULT");
			break;
		}
	}

	public JSONObject readConfig() {
		JSONParser parser = new JSONParser();
		JSONObject configJson = new JSONObject();
		SnapSnap snap = new SnapSnap();

		String folderPath = "D:\\Work From Home\\Java\\new_ticket_exporter\\java-ticketexporter-dev";

		File f = new File(System.getProperty("java.class.path"));
		File dir = f.getAbsoluteFile().getParentFile();
		String path = "";
		if (snap.JAR_RUN) {
			path = dir.toString();
		} else {
			path = folderPath;
		}
		try {
			Object obj = parser.parse(new FileReader(path + "\\config.json"));

			JSONObject jsonObject = (JSONObject) obj;
			configJson = jsonObject;
			
			/*CHECKING ALL VALUE*/
			configJson.get("domain").toString();
			configJson.get("username").toString();
			configJson.get("password").toString();
			configJson.get("start_time").toString();
			configJson.get("end_time").toString();

		} catch (Exception e) {
			System.out.println("===== CONFIG IS NULL =====");
			System.exit(0);
		}
		return configJson;
	}
}
