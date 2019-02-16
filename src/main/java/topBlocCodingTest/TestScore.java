package topBlocCodingTest;


// class for a single test score 
public class TestScore 
{
	// private attributes
	private int score;
	private String studentID;
	
	
	// get id method
	public String getID()
	{
		return studentID;
	}
	
	// get score method
	public int getScore()
	{
		return score;
	}
	
	// constructor
	public TestScore(int score, String id)
	{
		this.score = score;
		this.studentID = id;
	}

}
