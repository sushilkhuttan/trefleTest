package zodiaTest;

import static io.restassured.RestAssured.given;

import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import io.restassured.RestAssured;
import io.restassured.path.json.JsonPath;


public class testZodia {
	
	int dataId;
	@Test(dataProvider = "authdata")
	public void plant(String authdata1, int code) {
		
		
		RestAssured.baseURI = "https://trefle.io";
		String response = given().header("Authorization",authdata1)
		.when().get("api/v1/plants")
		.then().assertThat().statusCode(code).extract().response().asString();
		
		JsonPath jp = new JsonPath(response);
		System.out.println(jp);
		if (code == 200) {
		dataId = jp.getInt("data[0].id");
		System.out.println("First Data Id is :" + dataId);
		String self = jp.getString("links.self");
		System.out.println("Self Link is :" + self);
		String first = jp.getString("links.first");
		System.out.println("First Link is :" + first);
		String next = jp.getString("links.next");
		System.out.println("Next Link is :" + next);
		String last = jp.getString("links.last");
		System.out.println("Last Link is :" + last);
		
		Assert.assertEquals(77116, dataId);
		Assert.assertEquals("/api/v1/plants", self);
		}
		else if (code == 401) {//Test negative scenario when Auth token is incorrect
			String msg = jp.getString("messages");
			System.out.println("messages : "+msg);
			Assert.assertEquals("Invalid access token", msg);
		}
	}
	
	@DataProvider(name="authdata")
	public Object[][] authdata() {
		return new Object[][] {{"mgrV-WrockCPe4COtTrnb_L9XR0CG3ck15rSysP-PXk",200},{"mgrV-WrockCPe4COtTrnb_L9XR0CG3ck15rSysP-123",401}};
	}
	
	@Test
	public void plantlist() {
		System.out.println("In Plant List, data id is: "+dataId);
		RestAssured.baseURI = "https://trefle.io";
		String PLresponse = given().header("Authorization","mgrV-WrockCPe4COtTrnb_L9XR0CG3ck15rSysP-PXk")
		.when().get("api/v1/plants/"+dataId)
		.then().assertThat().statusCode(200).extract().response().asString(); //Verifies the code is 200
		
		System.out.println("response is :"+PLresponse);
		
		JsonPath js = new JsonPath(PLresponse);
		String commonName = js.getString("data.common_name");
		System.out.println("common name is :"+commonName);
		Assert.assertEquals("Evergreen oak", commonName);
	}
	
}
