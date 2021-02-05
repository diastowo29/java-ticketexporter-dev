package df.trees.ticketexporter;

import kong.unirest.JsonNode;
import kong.unirest.Unirest;
import kong.unirest.json.JSONArray;

public class GetUsers {

	CSVWritter write = new CSVWritter();
	int ticketPageCounter = 0;

	public void doGetUser(String usersApi, String zdDomain, String token) {
		ticketPageCounter++;
		SnapSnap snap = new SnapSnap();
		System.out.print("Calling: " + usersApi + " ");
		Unirest.get(usersApi).header("Authorization", "basic " + token).asJson().ifSuccess(response -> {
			System.out.println(response.getStatus());
			JsonNode userObj = response.getBody();

			JSONArray usersList = userObj.getObject().getJSONArray("users");
			write.doWriteUsers(usersList, ticketPageCounter, zdDomain);
			if (!snap.FIRST_PAGE_ONLY) {
				if (userObj.getObject().get("next_page") != null) {
					doGetUser(userObj.getObject().get("next_page").toString(), zdDomain, token);
				}
			}
		}).ifFailure(response -> {
			System.out.println(response.getStatus());
			System.out.println(response.getStatusText());
		});
	}

}
