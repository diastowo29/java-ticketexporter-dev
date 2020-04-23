package df.trees.ticketexporter;

import kong.unirest.JsonNode;
import kong.unirest.Unirest;
import kong.unirest.json.JSONArray;

public class GetTicketMetrics {
	int metricsPageCounter = 0;
	SnapSnap snap = new SnapSnap();
	CSVWritter write = new CSVWritter();

	public void doGetMetrics(String metricsApi, String zdDomain) {
		metricsPageCounter++;
		SnapSnap snap = new SnapSnap();
		String zD_Username = snap.ZD_USERNAME;
		String zD_Password = snap.ZD_PASSWORD;
		System.out.print("Calling: " + metricsApi + " ");
		Unirest.get(metricsApi).basicAuth(zD_Username, zD_Password).asJson().ifSuccess(response -> {
			System.out.println(response.getStatus());
			JsonNode metricsObj = response.getBody();

			JSONArray metricsList = metricsObj.getObject().getJSONArray("ticket_metrics");
			write.doWriteTicketMetrics(metricsList, metricsPageCounter, zdDomain);
			if (!snap.FIRST_PAGE_ONLY) {
				if (metricsObj.getObject().get("next_page") != null) {
					doGetMetrics(metricsObj.getObject().get("next_page").toString(), zdDomain);
				}
			}
		}).ifFailure(response -> {
			System.out.println(response.getStatus());
			System.out.println(response.getStatusText());
		});
	}
}
