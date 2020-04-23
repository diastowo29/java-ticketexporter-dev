package df.trees.ticketexporter;

import kong.unirest.JsonNode;
import kong.unirest.Unirest;
import kong.unirest.json.JSONArray;

public class GetTicketFields {
	CSVWritter write = new CSVWritter();

	public void doGetTicketFields(String ticketFieldsApi, String zdDomain) {
		SnapSnap snap = new SnapSnap();
		String zD_Username = snap.ZD_USERNAME;
		String zD_Password = snap.ZD_PASSWORD;
		System.out.print("Calling: " + ticketFieldsApi + " ");
		Unirest.get(ticketFieldsApi).basicAuth(zD_Username, zD_Password).asJson().ifSuccess(response -> {
			System.out.println(response.getStatus());
			JsonNode ticketFieldsObj = response.getBody();

			JSONArray ticketFieldsList = ticketFieldsObj.getObject().getJSONArray("ticket_fields");
			for (int i = 0; i < ticketFieldsList.length(); i++) {
				if (ticketFieldsList.getJSONObject(i).getString("type").equals("tagger")
						&& ticketFieldsList.getJSONObject(i).getBoolean("active")) {
					write.doWriteTicketFields(ticketFieldsList.getJSONObject(i),
							ticketFieldsList.getJSONObject(i).getString("title"), zdDomain);
				}
			}
			if (!snap.FIRST_PAGE_ONLY) {
				if (ticketFieldsObj.getObject().get("next_page") != null) {
					doGetTicketFields(ticketFieldsObj.getObject().get("next_page").toString(), zdDomain);
				}
			}
		}).ifFailure(response -> {
			System.out.println(response.getStatus());
			System.out.println(response.getStatusText());
		});

	}
}
