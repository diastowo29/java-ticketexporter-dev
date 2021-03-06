package df.trees.ticketexporter;

import kong.unirest.JsonNode;
import kong.unirest.Unirest;
import kong.unirest.json.JSONArray;

public class GetBrands {

	CSVWritter write = new CSVWritter();
	int ticketPageCounter = 0;

	public void doGetBrands(String brandsApi, String zdDomain, String token) {
		ticketPageCounter++;
		SnapSnap snap = new SnapSnap();
		System.out.print("Calling: " + brandsApi + " ");
		Unirest.get(brandsApi).header("Authorization", "basic " + token).asJson().ifSuccess(response -> {
			System.out.println(response.getStatus());
			JsonNode brandObj = response.getBody();

			JSONArray brandsList = brandObj.getObject().getJSONArray("brands");
			 write.doWriteBrands(brandsList, ticketPageCounter, zdDomain);
			if (!snap.FIRST_PAGE_ONLY) {
				if (brandObj.getObject().get("next_page") != null) {
					doGetBrands(brandObj.getObject().get("next_page").toString(), zdDomain, token);
				}
			}
		}).ifFailure(response -> {
			System.out.println(response.getStatus());
			System.out.println(response.getStatusText());
		});
	}

}
