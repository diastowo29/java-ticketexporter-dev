package df.trees.ticketexporter;

public class SnapSnap {
	public Boolean INC_COMMENTS = true;
	public Boolean INC_METRICS = true;
	public Boolean JAR_RUN = true;
	public Boolean FIRST_PAGE_ONLY = false;

	public String ZD_TICKET_API = "/api/v2/tickets.json";
	public String ZD_TICKET_SEARCH_API = "/api/v2/search.json?query=type:ticket%20order_by:created_at%20sort:asc";
//	public String ZD_TICKET_INCREMENTAL_API = "/api/v2/incremental/tickets.json?start_time=1483228800";

	public String ZD_TICKETS_API_SL = "api/v2/tickets?include=ticket_forms,groups,brands,users";
	public String ZD_TICKET_METRICS_API = "/api/v2/ticket_metrics.json";
	
	public String delimiter = " | ";

	public String ZD_BRAND_API(String zdDomain) {
		return "https://" + zdDomain + ".zendesk.com/api/v2/brands.json";
	}

	public String ZD_ORGANIZATION_API(String zdDomain) {
		return "https://" + zdDomain + ".zendesk.com/api/v2/organizations.json";
	}

	public String ZD_USER_API(String zdDomain) {
		return "https://" + zdDomain + ".zendesk.com/api/v2/users.json";
	}

	public String ZD_TICKET_FIELDS_API(String zdDomain) {
		return "https://" + zdDomain + ".zendesk.com/api/v2/ticket_fields.json";
	}

	public String ZD_TICKET_INCREMENTAL_API(long unixTime, String zdDomain) {
		return "https://" + zdDomain + ".zendesk.com/api/v2/incremental/tickets.json?start_time=" + unixTime;
	}

	public String ZD_TICKETEVENT_INCREMENTAL_API(long unixTime, String zdDomain) {
		return "https://" + zdDomain + ".zendesk.com/api/v2/incremental/ticket_metric_events.json?start_time="
				+ unixTime;
	}

	public String ZD_TICKET_COMMENT_API(String ticketId, String zdDomain) {
		return "https://" + zdDomain + ".zendesk.com/api/v2/tickets/" + ticketId + "/comments.json";
	}

	public String ZD_TICKET_METRICS_BYID_API(String ticketId, String zdDomain) {
		return "https://" + zdDomain + ".zendesk.com/api/v2/tickets/" + ticketId + "/metrics.json";
	}

	public String ZD_SCHEDULE_API(String zdDomain) {
		return "https://" + zdDomain + ".zendesk.com/api/v2/business_hours/schedules.json";
	}

	public String ZD_HOLIDAY_API(String zdDomain, String scheduleId) {
		return "https://" + zdDomain + ".zendesk.com/api/v2/business_hours/schedules/" + scheduleId + "/holidays.json";
	}
}
