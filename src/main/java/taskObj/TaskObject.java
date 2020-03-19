package taskObj;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.sql.*;
import java.text.SimpleDateFormat;

public abstract class TaskObject {
		
	public static final String TASK_FORMAT_REGEX = "^.+?\\?.+?\\!";
	public static final String TASK_FORMAT_TEXT_REPRESENTATION = "open question? answer! optional answer! optional answer!";
	private final Integer MAX_REPEATS = 5;	
	protected Integer ID;
	protected Integer numberOfRepeats = 0;	
	protected String question;
	protected String answer0;
	protected String answer1;
	protected String answer2;	
	protected Calendar nextRepeat;
	
	private void setID(Integer iD) {
		this.ID = iD;
	}	
	
	private void setNumberOfRepeats(Integer numberOfRepeats) {
		this.numberOfRepeats = numberOfRepeats;
	}

	private void setQuestion(String question) {
		this.question = question;
	}
	
	private void setAnswer0(String answer0) {
		this.answer0 = answer0;
	}

	private void setAnswer1(String answer1) {
		this.answer1 = answer1;
	}

	private void setAnswer2(String answer2) {
		this.answer2 = answer2;
	}

	private void setNextRepeat(java.sql.Date date) {
		this.nextRepeat = Calendar.getInstance();
		this.nextRepeat.setTime(date);
	}

	public TaskObject(){	
	}	
	
	public TaskObject(String stringRepresentation) { //creates taskObject from parsed stringRepresentation and saves it into the database
						
		String[] stringArray = parseStringRepresentation(stringRepresentation);		
		this.question = stringArray[0];
		this.answer0 = stringArray[1];
		this.answer1 = stringArray[2];
		this.answer2 = stringArray[3];
		this.nextRepeat = GregorianCalendar.getInstance();
		this.nextRepeat.set(Calendar.HOUR_OF_DAY, 0);
		this.nextRepeat.set(Calendar.MINUTE, 0);
		this.nextRepeat.set(Calendar.SECOND, 0);	
		this.nextRepeat.set(Calendar.MILLISECOND, 0);
		
		saveObjectInDatabaseWithNewID();

	}

	public abstract void changeQuestion();
	
	protected void saveObjectInDatabaseWithNewID() {
		
		PreparedStatement insertObject = null;
		Connection connectionDatabse = null;
		
		String insertString =   "insert into TASK  SET " + 
									"QUESTION = ?, " + 
									"ANSWER0 = ?, " + 
									"ANSWER1 = ?, " + 
									"ANSWER2 = ?, " + 
									"REPEAT_NUMBER = ?, " + 
									"LAST_REPEAT = ?, " + 
									"NEXT_REPEAT = ?;";	
		
		try {
			Class.forName ("org.h2.Driver");
			connectionDatabse = DriverManager.getConnection ("jdbc:h2:~/TaskBase", "sa","123654");
			insertObject = connectionDatabse.prepareStatement(insertString);
			insertObject.setString(1, this.question);
			insertObject.setString(2, this.answer0);
			insertObject.setString(3, this.answer1);
			insertObject.setString(4, this.answer2);
			insertObject.setInt(5, numberOfRepeats);
			insertObject.setDate(6, new java.sql.Date(0));;
			insertObject.setDate(7, new java.sql.Date(this.nextRepeat.getTimeInMillis()));	
			insertObject.executeUpdate();	
		
		} catch (ClassNotFoundException e) {			
				e.printStackTrace();			
		} catch (SQLException e) {	       
				e.printStackTrace();			
		}
		finally {
			if (insertObject != null) {
				try {
					insertObject.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}				
			}			
			if (connectionDatabse != null) {
				try {
					connectionDatabse.close();
				} catch (SQLException e) {					
					e.printStackTrace();
				}				
			}
			
		}		
		
	}
	
	private void updateObjectInDatabase() {
		
		PreparedStatement udateObject = null;
		Connection connectionDatabse = null;
		
		String updateString =   "update TASK  SET " + 
									"QUESTION = ?, " + 
									"ANSWER0 = ?, " + 
									"ANSWER1 = ?, " + 
									"ANSWER2 = ?, " + 
									"REPEAT_NUMBER = ?, " + 
									"LAST_REPEAT = ?, " + 
									"NEXT_REPEAT = ? " +
								"where ID = ?;";
								
		
		try {
			Class.forName ("org.h2.Driver");
			connectionDatabse = DriverManager.getConnection ("jdbc:h2:~/TaskBase", "sa","123654");
			udateObject = connectionDatabse.prepareStatement(updateString);
			udateObject.setString(1, this.question);
			udateObject.setString(2, this.answer0);
			udateObject.setString(3, this.answer1);
			udateObject.setString(4, this.answer2);
			udateObject.setInt(5, numberOfRepeats);
			udateObject.setDate(6, new java.sql.Date(0));;
			udateObject.setDate(7, new java.sql.Date(this.nextRepeat.getTimeInMillis()));
			udateObject.setInt(8, ID);
			udateObject.executeUpdate();			

		} catch (ClassNotFoundException e) {			
			e.printStackTrace();			
		} catch (SQLException e) {	       
			e.printStackTrace();			
		}
		finally {
			if (udateObject != null) {
				try {
					udateObject.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}				
			}			
			if (connectionDatabse != null) {
				try {
					connectionDatabse.close();
				} catch (SQLException e) {					
					e.printStackTrace();
				}				
			}
			
		}		
		
	}

	public void deleteObjectFromDatabase(boolean silentInConsole) {
		
		PreparedStatement deleteObject = null;
		Connection connectionDatabse = null;
		
		String deleteString =   "DELETE FROM TASK WHERE ID = ?;"; 								
		
		try {
			Class.forName ("org.h2.Driver");
			connectionDatabse = DriverManager.getConnection ("jdbc:h2:~/TaskBase", "sa","123654");
			deleteObject = connectionDatabse.prepareStatement(deleteString);
			deleteObject.setInt(1, ID);
			deleteObject.executeUpdate();
			System.out.println("Task deleted.");

		} catch (ClassNotFoundException e) {			
			e.printStackTrace();			
		} catch (SQLException e) {			
			e.printStackTrace();			
		}
		finally {
			if (deleteObject != null) {
				try {
					deleteObject.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}				
			}			
			if (connectionDatabse != null) {
				try {
					connectionDatabse.close();
				} catch (SQLException e) {					
					e.printStackTrace();
				}				
			}
			
		}		
		
	}	


	public static String[] parseStringRepresentation(String stringRepresentation) {
				
		String[] returnArray = new String[4];
		Arrays.fill(returnArray, "");
		String[] tokensArray = stringRepresentation.split("[\\?\\!]");		
		System.arraycopy(tokensArray, 0, returnArray, 0, Math.min(tokensArray.length, 4));		
		
		for (int i = 0; i < returnArray.length; i++) {
			returnArray[i] = returnArray[i].trim();			
		}
		
		return returnArray;
	}

	public void askQuestionTroughConsole() {

		System.out.println(question);
		
	}

	public boolean answerIsCorrect(String inputString) {
		inputString = inputString.trim();
		if (!inputString.isEmpty() 
				&& (inputString.equals(answer0)
					|| inputString.equals(answer1)
					|| inputString.equals(answer2))) {
			return true;
		} else {
			return false;
		}		
	}

	public void showCurrentTaskCondition() {
		SimpleDateFormat dateFormat = new SimpleDateFormat("d MMMM yyyy");
		System.out.println(String.valueOf(numberOfRepeats) 
							+ " from " + String.valueOf(MAX_REPEATS) 
							+ " next repeat on " + dateFormat.format(nextRepeat.getTime()));		
	}

	public void sceduleNextTrainingForTheTask(boolean notFromBegining) {
		
		if (notFromBegining) {
			numberOfRepeats++;
		}
		else {
			numberOfRepeats = 0;
		}
				
		if (numberOfRepeats > MAX_REPEATS) {
			deleteObjectFromDatabase(false);
			return;
		}
		
		nextRepeat.add(Calendar.DAY_OF_MONTH, numberOfRepeats * numberOfRepeats);		
		updateObjectInDatabase();
		
	}

	public void showAnswers() {
		System.out.println("Correct answers is: " + answer0 + " " + answer1 + " " + answer2);		
	}

	public static void getTasksForTrainng(ArrayList<TaskObject> tasksForTraining) {
		
				
		Calendar today = Calendar.getInstance();		
		ResultSet resSet = null; 
		PreparedStatement selectObject = null;
		Connection connectionDatabse = null;		
		
		today.set(Calendar.HOUR_OF_DAY, 23);
		today.set(Calendar.MINUTE, 59);
		today.set(Calendar.SECOND, 59);
		today.set(Calendar.MILLISECOND, 0);
		
		tasksForTraining.clear();
		
		String selectionString = 	"select * " + 
									"from  TASK " + 
									"where NEXT_REPEAT <= ?;";  
		
		try {
			Class.forName ("org.h2.Driver");
			connectionDatabse = DriverManager.getConnection ("jdbc:h2:~/TaskBase", "sa","123654");
			selectObject = connectionDatabse.prepareStatement(selectionString);
			selectObject.setDate(1, new java.sql.Date(today.getTimeInMillis()));	
			resSet = selectObject.executeQuery();
			
			while (resSet.next()) {
				TaskObject readTask = new TaskObjectOpenQuestion();
				readTask.setID(resSet.getInt("ID"));
				readTask.setQuestion(resSet.getString("QUESTION"));
				readTask.setAnswer0(resSet.getString("ANSWER0"));
				readTask.setAnswer1(resSet.getString("ANSWER1"));
				readTask.setAnswer2(resSet.getString("ANSWER2"));
				readTask.setNumberOfRepeats(resSet.getInt("REPEAT_NUMBER"));
				readTask.setNextRepeat(resSet.getDate("NEXT_REPEAT"));
				
				tasksForTraining.add(readTask);				
			}
			
	
		} catch (ClassNotFoundException e) {			
			e.printStackTrace();			
		} catch (SQLException e) {	       
			e.printStackTrace();			
		} finally {
			if (resSet != null) {
				try {
					resSet.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}			
			if (selectObject != null) {
				try {
					selectObject.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}				
			}			
			if (connectionDatabse != null) {
				try {
					connectionDatabse.close();
				} catch (SQLException e) {					
					e.printStackTrace();
				}				
			}
			
		}		
		
	}
	
}
