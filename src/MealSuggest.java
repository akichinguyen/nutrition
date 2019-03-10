import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;

import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonObject;
import javax.json.JsonReader;

public class MealSuggest {
	public int ID;
	public String title;
	public String picture;
	public MealSuggest(int id, String title, String pic) {
		this.ID = id;
		this.title = title;
		this.picture = pic;
	}
	public MealSuggest() {
	}
	public ArrayList<MealSuggest> getMealSuggest(String ingr) throws IOException{
//		String ingrediants = "apples,flour,milk";
		ArrayList<MealSuggest> ms = new ArrayList<MealSuggest>();
		String urlTitleI = URLEncoder.encode(ingr, "UTF-8");

		URL urlIng = new URL(
				"https://spoonacular-recipe-food-nutrition-v1.p.rapidapi.com/recipes/findByIngredients?number=5&ranking=1&ingredients="
						+ urlTitleI);
		HttpURLConnection connI = (HttpURLConnection) urlIng.openConnection();
		connI.setRequestMethod("GET");
		connI.setRequestProperty("X-RapidAPI-Key", "1b341b45b4mshdf3e475c11851e2p15d65ajsnd6f7ef22b0be");
		JsonReader jsRead = Json.createReader(connI.getInputStream());

		JsonArray js = jsRead.readArray();
		for (int i = 0; i < js.size(); i++) {
			JsonObject jso = js.getJsonObject(i);
			int id = jso.getJsonNumber("id").intValue();
			String title = jso.getJsonString("title").getString();
			String pic = jso.getJsonString("image").getString();
			ms.add(new MealSuggest(id, title, pic));
		}
		return ms;
	}
	
	public static void main(String[] args) throws IOException {
		MealSuggest ms = new MealSuggest();
		ms.getMealSuggest("apples,flour,milk");
	}
}
