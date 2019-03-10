import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;

import javax.swing.JFrame;
import javax.swing.JPanel;
import java.awt.BorderLayout;
import java.awt.Color;

import javax.swing.JTextField;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.data.general.DefaultPieDataset;
import org.jfree.data.general.PieDataset;

import javax.swing.JLabel;
import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonObject;
import javax.json.JsonReader;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JTextArea;

public class NutritionFact {

	private JFrame frame;
	private JTextField ingredientList;
	private String ingrd;
	private JPanel panel;
	private JPanel meal_panel;
	private JComboBox meals;
	private JPanel meal_ingr;
	private JTextArea text_meal_ingr;
	MealSuggest ms;
	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					NutritionFact window = new NutritionFact();
					window.frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the application.
	 */
	public NutritionFact() {
		initialize();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frame = new JFrame();
		frame.setBounds(100, 100, 505, 432);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.getContentPane().setLayout(null);
		
		JLabel ingredient = new JLabel("Foods");
		ingredient.setBounds(45, 22, 46, 14);
		frame.getContentPane().add(ingredient);
		
		ingredientList = new JTextField();
		ingredientList.setBounds(169, 19, 86, 20);
		frame.getContentPane().add(ingredientList);
		ingredientList.setColumns(10);
		
		JButton search = new JButton("search");
		search.setBounds(301, 18, 89, 23);
		frame.getContentPane().add(search);
		
		panel = new JPanel();
		panel.setBounds(214, 132, 231, 204);
		panel.setLayout(new java.awt.BorderLayout());
		frame.getContentPane().add(panel);
		
		meal_ingr = new JPanel();
		meal_ingr.setBounds(25, 178, 179, 158);
		//frame.getContentPane().add(meal_ingr);
		meal_ingr.setLayout(null);
		
		text_meal_ingr = new JTextArea();
		text_meal_ingr.setBounds(10, 11, 159, 136);
		meal_ingr.add(text_meal_ingr);
		
		meal_panel = new JPanel();
		meal_panel.setBounds(89, 47, 166, 60);
		//frame.getContentPane().add(meal_panel);
		meal_panel.setLayout(null);
		
		meals = new JComboBox();
		meals.setBounds(10, 11, 146, 38);
		meal_panel.add(meals);
		meals.addItemListener(new MealChange());
		
		search.addActionListener(new SearchListener());
	}
	private static PieDataset createDataset(double[] nutritionvalue) {
	      DefaultPieDataset dataset = new DefaultPieDataset( );
	      dataset.setValue( "fat" , nutritionvalue[1] ); 
	      dataset.setValue("carbs", nutritionvalue[2]);
	      dataset.setValue("protein", nutritionvalue[3]);
	      return dataset;         
	   }
	   
	   private static JFreeChart createChart( PieDataset dataset ) {
	      JFreeChart chart = ChartFactory.createPieChart(      
	         "Nutrition",   // chart title 
	         dataset,          // data    
	         true,             // include legend   
	         true, 
	         false);

	      return chart;
	   }
	   public static JPanel createDemoPanel(double[] value ) {
		      JFreeChart chart = createChart(createDataset(value) );  
		      return new ChartPanel( chart ); 
	   }
	  
	   class MealChange implements ItemListener{

		@Override
		public void itemStateChanged(ItemEvent e) {
			// TODO Auto-generated method stub
			if(e.getStateChange() == ItemEvent.SELECTED) {
				MealSuggest item =  (MealSuggest) e.getItem();
				System.out.println(item.ID);
				//Call function get data of the meal
				ArrayList<String> ingredList = null;
				try {
					ingredList = ingredientList(item.ID);
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				for (String s: ingredList)
					text_meal_ingr.append(s+"\n");
				frame.getContentPane().add(meal_ingr);
			}
		}
		
		   
	   }
	   class SearchListener implements ActionListener{

			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				ingrd = ingredientList.getText();
				MealSuggest ms = new MealSuggest();
				try {
					ArrayList<MealSuggest> mealsgg = ms.getMealSuggest(ingrd);
					for (MealSuggest s: mealsgg) {
						   meals.addItem(s);
					   }
					 frame.getContentPane().add(meal_panel);
					 if(mealsgg.size()!=0) {
						 panel.add(createDemoPanel(getNutritionInfor("Chick-fil-A Chicken Nuggets")), BorderLayout.CENTER);
					 }
					 
				} catch (IOException e2) {
					// TODO Auto-generated catch block
					e2.printStackTrace();
				}
				panel.validate();
			}
			
		}
	   private void addMealtoList(ArrayList<MealSuggest> mealSuggest) {
			// TODO Auto-generated method stub
			 for (MealSuggest ms: mealSuggest) {
				   meals.addItem(ms);
			   }
			   frame.getContentPane().add(meal_panel);
		}
	   public double[] getNutritionInfor(String mealName) throws IOException {
		   double[] nutrition = new double[4];
		   String urlTitle = URLEncoder.encode(mealName, "UTF-8");

		   URL url = new URL("https://spoonacular-recipe-food-nutrition-v1.p.rapidapi.com/recipes/guessNutrition?title="+urlTitle);
		   HttpURLConnection conn = (HttpURLConnection) url.openConnection();
		   conn.setRequestMethod("GET");
		   //conn.setRequestProperty("title", "Easy & Delish! ~ Apple Crumble");
		   conn.setRequestProperty("X-RapidAPI-Key", "1b341b45b4mshdf3e475c11851e2p15d65ajsnd6f7ef22b0be");
		   JsonReader jsRead = Json.createReader(conn.getInputStream());

		   JsonObject js = jsRead.readObject();
		   System.out.println(js);
			   nutrition[0]=js.getJsonObject("calories").getJsonNumber("value").doubleValue();
			   nutrition[1]= js.getJsonObject("fat").getJsonNumber("value").doubleValue();
			   nutrition[2]=js.getJsonObject("carbs").getJsonNumber("value").doubleValue();
			   nutrition[3]= js.getJsonObject("protein").getJsonNumber("value").doubleValue();
		
		   
		   conn.disconnect();
		   return nutrition;
	   }

	public ArrayList<String> ingredientList(int mealID) throws IOException {
		ArrayList<String> ingList = new ArrayList<String>();
		URL urlRecipe = new URL(
				"https://spoonacular-recipe-food-nutrition-v1.p.rapidapi.com/recipes/" + mealID + "/information");
		HttpURLConnection connR = (HttpURLConnection) urlRecipe.openConnection();
		connR.setRequestMethod("GET");
		connR.setRequestProperty("X-RapidAPI-Key", "1b341b45b4mshdf3e475c11851e2p15d65ajsnd6f7ef22b0be");
		JsonReader jsRead = Json.createReader(connR.getInputStream());
		JsonObject js = jsRead.readObject();
		JsonArray jsA = js.getJsonArray("extendedIngredients");
		for (int i = 0; i < jsA.size(); i++) {
			JsonObject jso = jsA.getJsonObject(i);
			String name = jso.getString("name");
			double amount = jso.getJsonNumber("amount").doubleValue();
			String unit = jso.getString("unit");
			ingList.add(name + " " + amount + " " + unit);
		}
		return ingList;
	}
	   
 
	   
}
