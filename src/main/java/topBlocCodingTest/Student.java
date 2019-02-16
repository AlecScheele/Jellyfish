package topBlocCodingTest;

public class Student implements Comparable<Student>
{
	private String studentID;
	private String major;
	private String gender;
	private int testScore;
	
	public Student(String id, String major, String gender)
	{
		this.studentID = id;
		this.major = major;
		this.gender = gender;
	}

	// implement compareTo method so students can be sorted by ID
	public int compareTo(Student o) 
	{
		// TODO Auto-generated method stub
		if(Integer.valueOf(o.studentID) > Integer.valueOf(this.studentID))
			return -1;
		else if(Integer.valueOf(o.studentID) < Integer.valueOf(this.studentID))
			return 1;
		else
			return 0;
	}
	
	public String getID()
	{
		return studentID;
	}
	
	public void setScore(int score)
	{
		this.testScore = score;
	}
	
	public int getScore()
	{
		return testScore;
	}
	
	public String getGender()
	{
		return gender;
	}
	
	public String getMajor()
	{
		return major;
	}
}
