package topBlocCodingTest;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
// using apache.poi to open XLSX files
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.json.JSONArray;
import org.json.JSONObject;

public class App {

	public static void main(String[] args) throws IOException 
	{
		// Create Hash Map of students
		HashMap<String, Student> studentMap = mapStudents("Student Info.xlsx");
		
		// read first test scores
		ArrayList<TestScore> scores = readScores("Test Scores.xlsx");
		
		// read retake scores
		ArrayList<TestScore> retakeScores = readScores("Test Retake Scores.xlsx");
		
		// add initial test scores to each student
		for(TestScore s : scores)
		{
			studentMap.get(s.getID()).setScore(s.getScore());
		}
		
		// update scores with retake scores
		updateScores(studentMap, retakeScores);
		
		// compute average
		int averageScore = averageScore(studentMap);
		
		String[] femaleCSstudents = femaleCompSci(studentMap);
		
		// create JSON object
		JSONObject json = createJSON(femaleCSstudents, averageScore);
		
		// post request
		postRequest(json);
		
	}
	
	// method that posts JSON request to server
	public static void postRequest(JSONObject json) throws IOException
	{
		// create httpClient
		CloseableHttpClient httpClient = HttpClientBuilder.create().build();
		
		// create string entity from JSON object and post request
		try 
		{
			HttpPost request = new HttpPost("http://3.86.140.38:5000/challenge");
			StringEntity jsonStringEntity = new StringEntity(json.toString());
			request.addHeader("content-type", "application/json");
			request.setEntity(jsonStringEntity);
			httpClient.execute(request);
			
		} 
		catch (UnsupportedEncodingException e) 
		{
			// TODO Auto-generated catch block
			System.out.println("Unsupported Encoding Exception");
			e.printStackTrace();
		} 
		catch (ClientProtocolException e) 
		{
			// TODO Auto-generated catch block
			System.out.println("Client Protocol Exception");
			e.printStackTrace();
		} 
		catch (IOException e) 
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		finally
		{
			httpClient.close();
			System.out.println("Success!");
		}
		
	}
	
	// method that creates JSON object with list of CS students, average score and my information
	public static JSONObject createJSON(String[] femaleCSstudents, int averageScore)
	{
		// new JSON object using JSON java
		JSONObject json = new JSONObject();
		
		// adding my name and email
		json.put("id", "abscheele@gmail.com");
		json.put("name", "Alec Scheele");
		
		// adding averageScore
		json.put("average", averageScore);
		
		// creating JSON array from femaleCSstudents array and adding to JSON obj 
		JSONArray JSON_ids_array = new JSONArray(Arrays.asList(femaleCSstudents));
		json.put("studentIds", JSON_ids_array);
		
		return json;
	}
	
	// method to iterate through hashmap and return sorted array
	// of IDs of students who are female and major in computer science
	public static String[] femaleCompSci(HashMap<String, Student> studentMap)
	{
		// will iterate through and add ids to this array list
		// and then will sort and return as array
		// using array list rather than array because we do not know
		// how many of the students will be female and CS majors
		ArrayList<Student> students = new ArrayList<Student>();
		
		Set<String> set = studentMap.keySet();
		Iterator<String> iter = set.iterator();
		
		// iterate through keys
		while(iter.hasNext())
		{
			Student s = studentMap.get(iter.next());
			
			// if gender == F and major == CS add to list
			if(s.getGender().equals("F") && s.getMajor().equals("computer science"))
			{
				students.add(s);

			}
		}
		
		// sort arraylist of female cs students
		students.sort(null);
		
		// arrayList to Array
		Object[] studentArray = students.toArray();
		
		// create array to be filled and returned
		String[] femaleCSstudents = new String[students.size()];
		
		for(int i = 0; i < students.size(); i++)
		{
			femaleCSstudents[i] = ((Student) studentArray[i]).getID();
		}
		
		return femaleCSstudents;
		
	}
	
	
	// method takes Hash map of students and computes the average score
	public static int averageScore(HashMap<String, Student> studentMap)
	{
		Set<String> set = studentMap.keySet();
		Iterator<String> iter = set.iterator();
		
		int totalScore = 0;
		
		while(iter.hasNext())
		{
			totalScore = totalScore + studentMap.get(iter.next()).getScore();
		}
		
		int averageScore = totalScore / studentMap.size();
		
		return averageScore;
	}
	
	// method takes Hash map of students and list of scores and updates scores for students if 
	// retake is higher than original
	public static void updateScores(HashMap<String, Student> studentMap, ArrayList<TestScore> retakeScores)
	{
		// add retake score if retake score is higher than original score
		for(TestScore s : retakeScores)
		{
			int originalScore = studentMap.get(s.getID()).getScore();
			int retakeScore = s.getScore();
			
			if(retakeScore > originalScore)
			{
				studentMap.get(s.getID()).setScore(retakeScore);
			}
		}
	}
	// method takes the file name of the XLSX file and returns
	// a hash map with the students with their IDs as keys
	// static method
	public static HashMap<String, Student> mapStudents(String filename1) throws IOException
	{
		
		// Hash map to store students, uses ID string as key
		HashMap<String, Student> studentMap = new HashMap<String, Student>();
		
		try 
		{
			// new FileStream for the XLSX file
			FileInputStream studentFile = new FileInputStream(new File(filename1));
			
			// make workbook
	        XSSFWorkbook workbook = new XSSFWorkbook(studentFile);
	        
	        // make sheet
	        XSSFSheet sheet = workbook.getSheetAt(0);
	        
	        // row iterator
	        Iterator<Row> rowIter = sheet.iterator();
	        
	        // iterate through rows
	        while(rowIter.hasNext())
	        {
	        	// current row
	        	Row row = rowIter.next();
	        	
	        	// strings for the Student info from this row
	        	String studentID = null;
	        	String major = null;
	        	String gender = null;
	        	
	        	// cell iterator
	        	Iterator<Cell> cellIter = row.cellIterator();
	        	
	        	// iterate through cells
	        	while(cellIter.hasNext() && row.getRowNum() != 0)
	        	{
	        		// current cell and cell type
	        		Cell cell = cellIter.next();
	        		CellType type = cell.getCellType();
	        		
                    switch (type)
                    {
                        case NUMERIC:
                        	// if numeric cell then is student id, cast to string
                        	studentID = Integer.toString((int) cell.getNumericCellValue());
                            break;
                        case STRING:
                        	// if cell is string then is student's major or gender
                            // if column index is 1 then major column
                            if(cell.getColumnIndex() == 1)
                            {
                            	major = cell.getStringCellValue();
                            }
                            
                            // if column index is 2 then gender column
                            else if(cell.getColumnIndex() == 2)
                            {
                            	gender = cell.getStringCellValue();
                            }
                            
                            break;
                    }
	        	}
	        	
	        	// if not header row create student object and add to hashmap
	        	if(row.getRowNum() != 0)
	        	{
		        	// create new student object and add to hashmap
		        	Student student = new Student(studentID, major, gender);
		        	studentMap.put(studentID, student);
	        	}
	        	
	        }
		} 
		catch (FileNotFoundException e) 
		{
			// TODO Auto-generated catch block
			System.out.println("Student information file not found");
			e.printStackTrace();
		}
		
		// return hash Map of students
		return studentMap;
	}
	
	// static method reads an XLSX file and returns a list of testscore objects
	// each of which contains a student's ID and Score
	public static ArrayList<TestScore> readScores(String filename) throws IOException
	{
		ArrayList<TestScore> scores = new ArrayList<TestScore>();
		
		try 
		{
			// new FileStream for the XLSX file
			FileInputStream scoresFile = new FileInputStream(new File(filename));
			
			// make workbook
	        XSSFWorkbook workbook = new XSSFWorkbook(scoresFile);
	        
	        // make sheet
	        XSSFSheet sheet = workbook.getSheetAt(0);
	        
	        // row iterator
	        Iterator<Row> rowIter = sheet.iterator();
	        
	        // iterate through rows
	        while(rowIter.hasNext())
	        {
	        	// current row
	        	Row row = rowIter.next();
	        	
	        	// strings for the Student info from this row
	        	String studentID = null;
	        	int score = -1;
	        	
	        	// cell iterator
	        	Iterator<Cell> cellIter = row.cellIterator();
	        	
	        	// iterate through cells
	        	while(cellIter.hasNext() && row.getRowNum() != 0)
	        	{
	        		// current cell and cell type
	        		Cell cell = cellIter.next();
	        		CellType type = cell.getCellType();
	        		
	        		// if col index is 0 then is first column so save student id as string
	        		if(cell.getColumnIndex() == 0)
                    	studentID = Integer.toString((int) cell.getNumericCellValue());

	        		// else col index is 1 so second column so is test score save as integer
	        		else if(cell.getColumnIndex() == 1)
	        			score = (int) cell.getNumericCellValue();
                   
	        	}
	        	
	        	// if row num is not header create new TestScore object and add to list
	        	if(row.getRowNum() != 0)
	        	{
	        		TestScore studentScore = new TestScore(score, studentID);
	        		scores.add(studentScore);

	        	}      	
	        }
		} 
		catch (FileNotFoundException e) 
		{
			// TODO Auto-generated catch block
			System.out.println("Scores file not found");
			e.printStackTrace();
		}
		
		return scores;
		
	}

}
