package df.trees.ticketexporter;

import kong.unirest.JsonNode;
import kong.unirest.Unirest;
import kong.unirest.json.JSONArray;

public class GetOrganizations {

	CSVWritter write = new CSVWritter();
	int ticketPageCounter = 0;

	public void doGetOrganizations(String organizationsApi, String zdDomain) {
		ticketPageCounter++;
		SnapSnap snap = new SnapSnap();
		String zD_Username = snap.ZD_USERNAME;
		String zD_Password = snap.ZD_PASSWORD;
		System.out.print("Calling: " + organizationsApi + " ");
		Unirest.get(organizationsApi).basicAuth(zD_Username, zD_Password).asJson().ifSuccess(response -> {
			System.out.println(response.getStatus());
			JsonNode organizationsObj = response.getBody();

			JSONArray organizationsList = organizationsObj.getObject().getJSONArray("organizations");
			write.doWriteOrganizations(organizationsList, ticketPageCounter, zdDomain);
			if (!snap.FIRST_PAGE_ONLY) {
				if (organizationsObj.getObject().get("next_page") != null) {
					doGetOrganizations(organizationsObj.getObject().get("next_page").toString(), zdDomain);
				}
			}
		}).ifFailure(response -> {
			System.out.println(response.getStatus());
			System.out.println(response.getStatusText());
		});
	}

}
