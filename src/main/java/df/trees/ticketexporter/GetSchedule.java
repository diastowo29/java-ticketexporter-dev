package df.trees.ticketexporter;

import kong.unirest.JsonNode;
import kong.unirest.Unirest;
import kong.unirest.json.JSONArray;
import kong.unirest.json.JSONObject;

public class GetSchedule {
	SnapSnap snap = new SnapSnap();
	CSVWritter write = new CSVWritter();
	JSONArray scheduleList = new JSONArray();

	public void doGetSchedule(String scheduleApi, String domain, String token) {
		System.out.print("=== Getting all schedule === ");
		Unirest.get(scheduleApi).header("Authorization", "basic " + token).asJson().ifSuccess(response -> {
			System.out.println(response.getStatus());
			JsonNode scheduleListObj = response.getBody();
			this.scheduleList = scheduleListObj.getObject().getJSONArray("schedules");
			write.doWriteSchedule(scheduleList, domain, "schedule");

			for (Object object : scheduleList) {
				JSONObject schedule = (JSONObject) object;
				doGetHolidays(snap.ZD_HOLIDAY_API(domain, schedule.get("id").toString()), domain,
						schedule.get("name").toString(), token);
			}

		}).ifFailure(response -> {
			System.out.println("=== GET SCHEDULES - FAIL ===");
			System.out.println(response.getStatus());
			System.out.println(response.getStatusText());
		});
	}

	private void doGetHolidays(String holidaysApi, String domain, String scheduleName, String token) {
		System.out.print("=== Getting holidays " + scheduleName + " === ");
		Unirest.get(holidaysApi).header("Authorization", "basic " + token).asJson().ifSuccess(response -> {
			System.out.println(response.getStatus());
			JsonNode holidaysObj = response.getBody();
			JSONArray holidayList = holidaysObj.getObject().getJSONArray("holidays");
			String alias = "holidays_" + scheduleName;

			if (holidayList.length() > 0) {
				write.doWriteSchedule(holidayList, domain, alias);
			}

		}).ifFailure(response -> {
			System.out.println("=== GET HOLIDAYS - FAIL ===");
			System.out.println(response.getStatus());
			System.out.println(response.getStatusText());
		});
	}

}
