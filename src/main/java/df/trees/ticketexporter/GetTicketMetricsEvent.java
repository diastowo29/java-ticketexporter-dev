package df.trees.ticketexporter;

import kong.unirest.JsonNode;
import kong.unirest.Unirest;
import kong.unirest.json.JSONArray;

public class GetTicketMetricsEvent {
	int metricsPageCounter = 0;
	SnapSnap snap = new SnapSnap();
	CSVWritter write = new CSVWritter();

	public void doGetMetricsEvent(String metricsEventApi, String ZDDomain, String token) {
		metricsPageCounter++;
		SnapSnap snap = new SnapSnap();
		System.out.print("Calling: " + metricsEventApi + " ");
		Unirest.get(metricsEventApi).header("Authorization", "basic " + token).asJson().ifSuccess(response -> {
			System.out.println(response.getStatus());
			JsonNode metricsObj = response.getBody();

			JSONArray metricsList = metricsObj.getObject().getJSONArray("ticket_metric_events");
			write.doWriteTicketMetricEvents(metricsList, metricsPageCounter, ZDDomain);
			if (!snap.FIRST_PAGE_ONLY) {
				if (!metricsObj.getObject().get("next_page").toString().equals("null")) {
					long currDate = System.currentTimeMillis() / 1000L;
					String unixTimeParam = metricsObj.getObject().get("next_page").toString()
							.substring(metricsObj.getObject().get("next_page").toString().indexOf("=") + 1);
					if (currDate > Long.parseLong(unixTimeParam)) {
						if (!metricsObj.getObject().get("next_page").toString().equals(metricsEventApi)) {
							doGetMetricsEvent(metricsObj.getObject().get("next_page").toString(), ZDDomain, token);
						}
					}
				}
			}
		}).ifFailure(response -> {
			System.out.println(response.getStatus());
			System.out.println(response.getStatusText());
		});
	}
}
