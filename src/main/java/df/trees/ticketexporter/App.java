package df.trees.ticketexporter;

/**
 * Hello world!
 *
 */
public class App {
	public enum OBJECT_RUN {
		TICKETS, TICKET_FIELDS, USERS, BRANDS, ORGANIZATIONS, METRIC_EVENTS
	}

	public static void main(String[] args) {
		SnapSnap snap = new SnapSnap();
		GetTickets tickets = new GetTickets();
		GetUsers users = new GetUsers();
		GetTicketFields ticketFields = new GetTicketFields();
		GetBrands brands = new GetBrands();
		GetOrganizations organizations = new GetOrganizations();
		// GetTicketMetrics metrics = new GetTicketMetrics(); // ALREADY INCLUDE ON
		// TICKET INCREMENTAL
		GetTicketMetricsEvent metricsEvent = new GetTicketMetricsEvent();
		String domain = "";
		
		String startTime = "";
		String endTime = "";

		OBJECT_RUN objectRun = OBJECT_RUN.USERS;

		if (!snap.JAR_RUN) {
			domain = "hydroclean-cs";
			startTime = "1598572800";
			endTime = "1609286400";
			
		} else {
			try {
				domain = args[0].toString();
				switch (objectRun) {
				case TICKETS:
					startTime = args[1].toString();
					endTime = args[2].toString();
					
//					incrementalParameter = Integer.parseInt(args[1].toString());
					break;
				case METRIC_EVENTS:
//					incrementalParameter = Integer.parseInt(args[1].toString());
					break;
				default:
					break;
				}
			} catch (IndexOutOfBoundsException e) {
				System.out.println("===== PARAMETER NULL =====");
				System.exit(0);
			}
		}

//		Date date = new Date();
//		Calendar cal = Calendar.getInstance();
//		cal.setTime(date);
//
//		// REDUCE MONTH
//		cal.add(Calendar.MONTH, -incrementalParameter);
//		long unixTime = (cal.getTimeInMillis() / 1000L);

		switch (objectRun) {
		case TICKETS:
			/* START FROM TICKET FIELDS */
			tickets.doGetTicketFields(snap.ZD_TICKET_FIELDS_API(domain),
					snap.ZD_TICKET_INCREMENTAL_API(startTime, domain), domain, endTime);
			break;
		case TICKET_FIELDS:
			ticketFields.doGetTicketFields(snap.ZD_TICKET_FIELDS_API(domain), domain);
			break;
		case USERS:
			users.doGetUser(snap.ZD_USER_API(domain), domain);
			break;
		case ORGANIZATIONS:
			organizations.doGetOrganizations(snap.ZD_ORGANIZATION_API(domain), domain);
			break;
		case BRANDS:
			brands.doGetBrands(snap.ZD_BRAND_API(domain), domain);
			break;
		case METRIC_EVENTS:
//			metricsEvent.doGetMetricsEvent(snap.ZD_TICKETEVENT_INCREMENTAL_API(unixTime, domain), domain);
			break;
		default:
			System.out.println("DEFAULT");
			break;
		}
	}
}
